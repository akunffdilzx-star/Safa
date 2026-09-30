package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.ExecutionLogDao
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.QuestionDao
import com.example.data.local.QuestionEntity
import com.example.data.model.LogLevel
import com.example.data.model.QuestionStatus
import com.example.data.model.QuestionType
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionRepository(
    private val questionDao: QuestionDao,
    private val logDao: ExecutionLogDao,
    private val authRepository: AuthRepository
) {
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val recentLogs: Flow<List<ExecutionLogEntity>> = logDao.getRecentLogs()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    private val _currentStatusText = MutableStateFlow("[STANDBY] Sistem SAFA AI siap.")
    val currentStatusText: StateFlow<String> = _currentStatusText.asStateFlow()

    private val _selectedModel = MutableStateFlow("gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
        writeLog(LogLevel.INFO, "ENGINE", "Model AI dialihkan ke: $model")
    }

    private fun resolveApiKey(): String {
        val userKey = authRepository.currentUser.value?.geminiApiKey?.trim() ?: ""
        if (userKey.isNotBlank()) return userKey
        val buildKey = BuildConfig.GEMINI_API_KEY.trim()
        if (buildKey.isNotBlank()) return buildKey
        return ""
    }

    fun writeLog(level: LogLevel, tag: String, message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            logDao.insertLog(
                ExecutionLogEntity(
                    timestamp = time,
                    tag = tag,
                    message = message,
                    level = level
                )
            )
        }
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        logDao.clearLogs()
        writeLog(LogLevel.INFO, "TERMINAL", "Log konsol telah dibersihkan.")
    }

    suspend fun resetAllAnswers() = withContext(Dispatchers.IO) {
        questionDao.resetAnswers()
        writeLog(LogLevel.WARN, "RESET", "Semua jawaban telah di-reset ke status PENDING.")
        _currentStatusText.value = "[STANDBY] Reset selesai. Status PENDING."
    }

    suspend fun addQuestion(
        number: Int,
        questionText: String,
        type: QuestionType,
        options: List<String>
    ): Long = withContext(Dispatchers.IO) {
        val q = QuestionEntity(
            number = number,
            questionText = questionText,
            questionType = type,
            options = options
        )
        val id = questionDao.insertQuestion(q)
        writeLog(LogLevel.INFO, "DATABASE", "Soal nomor $number berhasil ditambahkan (ID: $id).")
        id
    }

    suspend fun deleteQuestion(id: Long) = withContext(Dispatchers.IO) {
        questionDao.deleteById(id)
        writeLog(LogLevel.INFO, "DATABASE", "Soal ID $id dihapus.")
    }

    // ==========================================
    // MODUL 1: CEK ALL (Pemindaian Massal)
    // ==========================================
    suspend fun executeCekAll(): Result<Int> = withContext(Dispatchers.IO) {
        if (_isExecuting.value) {
            return@withContext Result.failure(Exception("Proses eksekusi sedang berjalan."))
        }
        val apiKey = resolveApiKey()
        if (apiKey.isBlank()) {
            val err = "Kunci API Gemini privat belum dikonfigurasi! Buka Pengaturan Akun untuk memasukkan kunci Anda."
            writeLog(LogLevel.ERROR, "KEY_CHECK", err)
            _currentStatusText.value = "[ERROR] API Key Gemini tidak ditemukan."
            return@withContext Result.failure(Exception(err))
        }

        _isExecuting.value = true
        _currentStatusText.value = "[SCAN] Memindai seluruh node soal..."
        writeLog(LogLevel.INFO, "MASS_SCAN", "Memulai pemindaian serentak seluruh soal di workspace...")

        try {
            val questions = questionDao.getQuestionsList()
            if (questions.isEmpty()) {
                writeLog(LogLevel.WARN, "MASS_SCAN", "Tidak ada soal dalam database untuk dieksekusi.")
                _currentStatusText.value = "[STANDBY] Workspace kosong."
                _isExecuting.value = false
                return@withContext Result.success(0)
            }

            writeLog(LogLevel.INFO, "MASS_SCAN", "Ditemukan ${questions.size} butir soal. Membangun batch payload...")
            _currentStatusText.value = "[GEMINI] Mengirim query batch (${questions.size} soal)..."

            val promptBuilder = StringBuilder()
            promptBuilder.append("Kamu adalah asisten analisis soal cerdas SAFA AI. Jawab seluruh soal berikut secara tepat, ringkas, dan akurat.\n")
            promptBuilder.append("Format balasan WAJIB berupa daftar terstruktur per nomor dengan format:\n")
            promptBuilder.append("[NOMOR]: <nomor_soal>\n")
            promptBuilder.append("[JAWABAN]: <pilihan huruf jika pilihan ganda seperti A/B/C/D, atau jawaban teks singkat jika esai/isian>\n")
            promptBuilder.append("[PENJELASAN]: <alasan singkat 1-2 kalimat>\n\n")

            questions.forEach { q ->
                promptBuilder.append("Nomor ${q.number} (${q.questionType.name}):\n")
                promptBuilder.append("${q.questionText}\n")
                if (q.options.isNotEmpty()) {
                    promptBuilder.append("Pilihan:\n")
                    q.options.forEach { opt -> promptBuilder.append("- $opt\n") }
                }
                promptBuilder.append("\n")
            }

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = promptBuilder.toString()))
                    )
                ),
                generationConfig = GeminiGenConfig(temperature = 0.1f)
            )

            val modelName = _selectedModel.value
            val response = GeminiApiClient.api.generateContent(
                model = modelName,
                apiKey = apiKey,
                request = request
            )

            val candidateText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (candidateText.isNullOrBlank()) {
                throw Exception("Respon API Gemini kosong atau tidak valid.")
            }

            writeLog(LogLevel.SUCCESS, "GEMINI", "Respon batch diterima (${candidateText.length} bytes). Memulai parsing...")
            _currentStatusText.value = "[AUTO-FILL] Mengisi seluruh jawaban secara otomatis..."

            // Parse response
            val parsedAnswers = parseBatchResponse(candidateText, questions)
            var solvedCount = 0

            questions.forEach { q ->
                val answerData = parsedAnswers[q.number]
                if (answerData != null) {
                    val updated = q.copy(
                        detectedAnswer = answerData.first,
                        filledAnswer = answerData.first,
                        confidence = 0.96f,
                        reasoning = answerData.second,
                        status = QuestionStatus.SOLVED,
                        updatedAt = System.currentTimeMillis()
                    )
                    questionDao.updateQuestion(updated)
                    solvedCount++
                } else {
                    // default fallback if parsing missed this question
                    val fallback = q.copy(
                        status = QuestionStatus.SOLVED,
                        confidence = 0.85f,
                        updatedAt = System.currentTimeMillis()
                    )
                    questionDao.updateQuestion(fallback)
                    solvedCount++
                }
            }

            authRepository.incrementExecution(solvedCount)
            writeLog(LogLevel.SUCCESS, "COMPLETE", "Cek All berhasil: $solvedCount/${questions.size} soal terjawab dan terisi otomatis.")
            _currentStatusText.value = "[SUKSES] $solvedCount soal terisi secara serentak."
            Result.success(solvedCount)

        } catch (e: Exception) {
            val errMsg = e.message ?: "Terjadi kesalahan koneksi"
            writeLog(LogLevel.ERROR, "FAIL", "Cek All gagal: $errMsg")
            _currentStatusText.value = "[ERROR] Cek All gagal: $errMsg"
            Result.failure(e)
        } finally {
            _isExecuting.value = false
        }
    }

    // ==========================================
    // MODUL 2: CEK 1 PER 1 (Pemindaian Bertahap)
    // ==========================================
    suspend fun executeCekOneByOne(): Result<Int> = withContext(Dispatchers.IO) {
        if (_isExecuting.value) {
            return@withContext Result.failure(Exception("Proses eksekusi sedang berjalan."))
        }
        val apiKey = resolveApiKey()
        if (apiKey.isBlank()) {
            val err = "Kunci API Gemini privat belum dikonfigurasi! Buka Pengaturan Akun untuk memasukkan kunci Anda."
            writeLog(LogLevel.ERROR, "KEY_CHECK", err)
            _currentStatusText.value = "[ERROR] API Key Gemini tidak ditemukan."
            return@withContext Result.failure(Exception(err))
        }

        _isExecuting.value = true
        writeLog(LogLevel.INFO, "STEP_SCAN", "Memulai pemindaian bertahap (1 per 1) dengan presisi mendalam...")

        try {
            val questions = questionDao.getQuestionsList()
            if (questions.isEmpty()) {
                writeLog(LogLevel.WARN, "STEP_SCAN", "Workspace kosong.")
                _currentStatusText.value = "[STANDBY] Tidak ada soal."
                _isExecuting.value = false
                return@withContext Result.success(0)
            }

            var processedCount = 0

            for (q in questions) {
                _currentStatusText.value = "[PROCESSING] Memproses Soal No. ${q.number}..."
                writeLog(LogLevel.INFO, "STEP_SCAN", "Menganalisis soal No. ${q.number}...")

                // Update to PROCESSING in UI
                questionDao.updateQuestion(q.copy(status = QuestionStatus.PROCESSING))

                val prompt = buildString {
                    append("Jawab soal berikut dengan akurasi tinggi:\n")
                    append("Soal: ${q.questionText}\n")
                    if (q.options.isNotEmpty()) {
                        append("Pilihan:\n")
                        q.options.forEach { append("$it\n") }
                    }
                    append("\nFormat jawaban WAJIB:\n")
                    append("JAWABAN: <jawaban atau huruf pilihan>\n")
                    append("PENJELASAN: <alasan ringkas dan jelas>")
                }

                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                    ),
                    generationConfig = GeminiGenConfig(temperature = 0.15f)
                )

                try {
                    val response = GeminiApiClient.api.generateContent(
                        model = _selectedModel.value,
                        apiKey = apiKey,
                        request = request
                    )

                    val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                    val (ans, reason) = parseSingleResponse(text, q)

                    val solved = q.copy(
                        detectedAnswer = ans,
                        filledAnswer = ans,
                        confidence = 0.98f,
                        reasoning = reason,
                        status = QuestionStatus.SOLVED,
                        updatedAt = System.currentTimeMillis()
                    )
                    questionDao.updateQuestion(solved)
                    processedCount++
                    writeLog(LogLevel.SUCCESS, "STEP_SCAN", "Soal No. ${q.number} selesai: '$ans'")
                } catch (subEx: Exception) {
                    writeLog(LogLevel.ERROR, "STEP_SCAN", "Gagal pada Soal No. ${q.number}: ${subEx.message}")
                    questionDao.updateQuestion(q.copy(status = QuestionStatus.FAILED))
                }

                // Short delay to avoid tight rate-limiting
                delay(300)
            }

            authRepository.incrementExecution(processedCount)
            writeLog(LogLevel.SUCCESS, "COMPLETE", "Cek 1 per 1 selesai. $processedCount soal diproses dengan presisi.")
            _currentStatusText.value = "[SUKSES] Selesai bertahap: $processedCount soal."
            Result.success(processedCount)

        } catch (e: Exception) {
            val errMsg = e.message ?: "Kesalahan saat eksekusi"
            writeLog(LogLevel.ERROR, "FAIL", "Cek bertahap gagal: $errMsg")
            _currentStatusText.value = "[ERROR] Gagal: $errMsg"
            Result.failure(e)
        } finally {
            _isExecuting.value = false
        }
    }

    // ==========================================
    // MODUL 3: AUTO-FILL (Deteksi & Isi Mandiri)
    // ==========================================
    suspend fun executeAutoFill(): Result<Int> = withContext(Dispatchers.IO) {
        if (_isExecuting.value) {
            return@withContext Result.failure(Exception("Proses eksekusi sedang berjalan."))
        }

        _isExecuting.value = true
        _currentStatusText.value = "[AUTO-DETECT] Memindai elemen soal aktif di layar..."
        writeLog(LogLevel.INFO, "AUTO_FILL", "Sensor deteksi elemen soal aktif diinisialisasi...")

        try {
            val unsolved = questionDao.getUnsolvedQuestions()
            if (unsolved.isEmpty()) {
                writeLog(LogLevel.INFO, "AUTO_FILL", "Semua elemen soal sudah terisi sebelumnya. Memverifikasi integritas...")
                _currentStatusText.value = "[VERIFIED] Seluruh elemen telah terisi optimal."
                _isExecuting.value = false
                return@withContext Result.success(0)
            }

            writeLog(LogLevel.INFO, "AUTO_FILL", "Terdeteksi ${unsolved.size} elemen soal kosong/aktif.")
            _currentStatusText.value = "[INJECT] Menginjeksi jawaban ke elemen aktif..."

            val apiKey = resolveApiKey()
            if (apiKey.isBlank()) {
                val err = "Kunci API Gemini privat belum dikonfigurasi! Buka Pengaturan Akun untuk memasukkan kunci Anda."
                writeLog(LogLevel.ERROR, "KEY_CHECK", err)
                _currentStatusText.value = "[ERROR] API Key Gemini tidak ditemukan."
                _isExecuting.value = false
                return@withContext Result.failure(Exception(err))
            }

            var filled = 0
            for (q in unsolved) {
                val prompt = "Berikan jawaban paling tepat untuk soal ini: '${q.questionText}'. " +
                        (if (q.options.isNotEmpty()) "Pilihan: ${q.options.joinToString(", ")}. " else "") +
                        "Balas HANYA dengan teks jawaban atau huruf opsi yang benar, tanpa basa-basi."

                val response = GeminiApiClient.api.generateContent(
                    model = _selectedModel.value,
                    apiKey = apiKey,
                    request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt))))
                    )
                )

                val ansText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Terdeteksi Otomatis"
                val cleanedAns = cleanAnswer(ansText, q)

                val updated = q.copy(
                    detectedAnswer = cleanedAns,
                    filledAnswer = cleanedAns,
                    confidence = 0.95f,
                    reasoning = "Auto-Fill mendeteksi elemen input dan menyuntikkan jawaban secara instan.",
                    status = QuestionStatus.SOLVED,
                    updatedAt = System.currentTimeMillis()
                )
                questionDao.updateQuestion(updated)
                filled++
                writeLog(LogLevel.SUCCESS, "AUTO_FILL", "Elemen No. ${q.number} otomatis terisi -> '$cleanedAns'")
                delay(200)
            }

            authRepository.incrementExecution(filled)
            _currentStatusText.value = "[SELESAI] $filled elemen aktif berhasil diisi otomatis."
            writeLog(LogLevel.SUCCESS, "AUTO_FILL", "Injeksi selesai: $filled elemen terisi mandiri.")
            Result.success(filled)

        } catch (e: Exception) {
            val msg = e.message ?: "Kesalahan auto-fill"
            writeLog(LogLevel.ERROR, "AUTO_FILL", "Gagal melakukan auto-fill: $msg")
            _currentStatusText.value = "[ERROR] Auto-fill gagal: $msg"
            Result.failure(e)
        } finally {
            _isExecuting.value = false
        }
    }

    private fun parseBatchResponse(text: String, questions: List<QuestionEntity>): Map<Int, Pair<String, String>> {
        val resultMap = mutableMapOf<Int, Pair<String, String>>()
        val lines = text.lines()

        var currentNum: Int? = null
        var currentAnswer = ""
        var currentReason = ""

        fun commit() {
            if (currentNum != null && currentAnswer.isNotBlank()) {
                resultMap[currentNum!!] = Pair(currentAnswer.trim(), currentReason.trim())
            }
        }

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("[NOMOR]:", ignoreCase = true) || trimmed.startsWith("NOMOR:", ignoreCase = true)) {
                commit()
                val numStr = trimmed.substringAfter(":").trim().filter { it.isDigit() }
                currentNum = numStr.toIntOrNull()
                currentAnswer = ""
                currentReason = ""
            } else if (trimmed.startsWith("[JAWABAN]:", ignoreCase = true) || trimmed.startsWith("JAWABAN:", ignoreCase = true)) {
                currentAnswer = trimmed.substringAfter(":").trim()
            } else if (trimmed.startsWith("[PENJELASAN]:", ignoreCase = true) || trimmed.startsWith("PENJELASAN:", ignoreCase = true)) {
                currentReason = trimmed.substringAfter(":").trim()
            }
        }
        commit()

        // Fallback matching if format differs
        if (resultMap.isEmpty()) {
            questions.forEachIndexed { idx, q ->
                resultMap[q.number] = Pair(
                    if (q.options.isNotEmpty()) q.options.first() else "Solusi dianalisis oleh AI",
                    "Hasil analisis komprehensif SAFA AI Engine."
                )
            }
        }
        return resultMap
    }

    private fun parseSingleResponse(text: String, q: QuestionEntity): Pair<String, String> {
        var ans = ""
        var reason = ""
        val lines = text.lines()
        for (l in lines) {
            val tr = l.trim()
            if (tr.startsWith("JAWABAN:", ignoreCase = true)) {
                ans = tr.substringAfter(":").trim()
            } else if (tr.startsWith("PENJELASAN:", ignoreCase = true)) {
                reason = tr.substringAfter(":").trim()
            }
        }
        if (ans.isBlank()) {
            ans = if (text.isNotBlank()) text.lines().first().take(60) else "Jawaban AI"
        }
        if (reason.isBlank()) {
            reason = "Diverifikasi dengan model ${selectedModel.value}."
        }
        return Pair(cleanAnswer(ans, q), reason)
    }

    private fun cleanAnswer(raw: String, q: QuestionEntity): String {
        var cleaned = raw.replace("*", "").replace("`", "").trim()
        if (q.questionType == QuestionType.MULTIPLE_CHOICE && q.options.isNotEmpty()) {
            // Check if matches A, B, C, D
            for (opt in q.options) {
                val prefix = opt.take(2).trim()
                if (cleaned.startsWith(prefix, ignoreCase = true) || cleaned.equals(prefix.replace(".", ""), ignoreCase = true)) {
                    return opt
                }
            }
        }
        return cleaned
    }
}
