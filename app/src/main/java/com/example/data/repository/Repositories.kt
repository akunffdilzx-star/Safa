package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.ExecutionLogDao
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.QuestionDao
import com.example.data.local.QuestionEntity
import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.model.LogLevel
import com.example.data.model.QuestionStatus
import com.example.data.model.QuestionType
import com.example.data.model.UserRole
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuthRepository(
    private val userDao: UserDao,
    private val logDao: ExecutionLogDao
) {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun getAllMembers(): Flow<List<UserEntity>> = userDao.getAllMembers()
    fun getMemberCount(): Flow<Int> = userDao.getMemberCount()

    suspend fun login(username: String, pass: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByUsername(username.trim())
        if (user == null) {
            log(LogLevel.ERROR, "AUTH", "Login gagal: Username '$username' tidak terdaftar.")
            return@withContext Result.failure(Exception("Akun tidak ditemukan. Hubungi Developer untuk pendaftaran."))
        }
        if (!user.isActive) {
            log(LogLevel.WARN, "AUTH", "Login ditolak: Akun '${user.username}' nonaktif/disuspend.")
            return@withContext Result.failure(Exception("Akun dinonaktifkan oleh Developer. Hubungi @dablezx."))
        }
        if (user.passwordHash != pass.trim()) {
            log(LogLevel.ERROR, "AUTH", "Login gagal: Password salah untuk '${user.username}'.")
            return@withContext Result.failure(Exception("Password salah. Silakan coba lagi."))
        }

        _currentUser.value = user
        log(LogLevel.SUCCESS, "AUTH", "Login berhasil. User: ${user.username} [${user.role}]")
        Result.success(user)
    }

    fun logout() {
        val u = _currentUser.value?.username ?: "Guest"
        _currentUser.value = null
        log(LogLevel.INFO, "AUTH", "User $u telah keluar sistem.")
    }

    suspend fun createMember(
        username: String,
        password: String,
        notes: String = ""
    ): Result<Long> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByUsername(username.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("Username '${username.trim()}' sudah digunakan."))
        }
        val newMember = UserEntity(
            username = username.trim(),
            passwordHash = password.trim(),
            role = UserRole.MEMBER,
            geminiApiKey = "",
            isActive = true,
            notes = notes.trim()
        )
        val id = userDao.insertUser(newMember)
        log(LogLevel.SUCCESS, "DEV_PANEL", "Member baru didaftarkan: ${newMember.username} (ID: $id)")
        Result.success(id)
    }

    suspend fun toggleMemberStatus(member: UserEntity) = withContext(Dispatchers.IO) {
        val updated = member.copy(isActive = !member.isActive)
        userDao.updateUser(updated)
        val statusText = if (updated.isActive) "DIAKTIFKAN" else "DINONAKTIFKAN"
        log(LogLevel.WARN, "DEV_PANEL", "Status akun ${member.username} diubah: $statusText")
    }

    suspend fun updateMemberPassword(userId: Long, newPass: String) = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext
        userDao.updateUser(user.copy(passwordHash = newPass.trim()))
        log(LogLevel.INFO, "DEV_PANEL", "Password member ${user.username} berhasil di-reset.")
    }

    suspend fun deleteMember(userId: Long) = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
        userDao.deleteUserById(userId)
        log(LogLevel.WARN, "DEV_PANEL", "Member ${user?.username ?: userId} telah dihapus dari database.")
    }

    suspend fun updateApiKey(apiKey: String): Result<Unit> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Sesi login berakhir."))
        val trimmedKey = apiKey.trim()
        userDao.updateApiKey(user.id, trimmedKey)
        _currentUser.value = user.copy(geminiApiKey = trimmedKey)
        log(LogLevel.SUCCESS, "CONFIG", "Kunci API Gemini privat tersimpan untuk ${user.username}.")
        Result.success(Unit)
    }

    suspend fun incrementExecution(count: Int = 1) = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext
        userDao.incrementExecution(user.id, count)
        val updated = userDao.getUserById(user.id)
        if (updated != null) {
            _currentUser.value = updated
        }
    }

    private fun log(level: LogLevel, tag: String, message: String) {
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
}
