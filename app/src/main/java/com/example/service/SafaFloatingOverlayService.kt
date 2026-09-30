package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.SafaApplication
import com.example.data.model.LogLevel
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SafaFloatingOverlayService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var windowManager: WindowManager? = null
    private var floatingBubbleView: View? = null
    private var floatingHudCardView: View? = null

    companion object {
        const val CHANNEL_ID = "safa_overlay_channel"
        const val NOTIF_ID = 2027

        var isRunning = false
            private set

        fun start(context: Context) {
            if (Settings.canDrawOverlays(context)) {
                val intent = Intent(context, SafaFloatingOverlayService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, SafaFloatingOverlayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        startForegroundNotification()
        setupFloatingBubble()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        removeFloatingViews()
    }

    private fun startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SAFA AI Floating Overlay",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SAFA AI // SYSTEM OVERLAY AKTIF")
            .setContentText("Tombol naga mengambang siap memindai layar eksternal.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIF_ID, notification)
    }

    private fun setupFloatingBubble() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 250
        }

        // Create bubble view programmatically with dragon emblem
        val bubble = ImageView(this).apply {
            setImageResource(R.drawable.safa_dragon_logo)
            layoutParams = LinearLayout.LayoutParams(160, 160)
            setPadding(8, 8, 8, 8)
            alpha = 0.85f
        }
        floatingBubbleView = bubble

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = false

        bubble.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    bubble.alpha = 1.0f
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                        isClick = false
                    }
                    params.x = initialX + dx
                    params.y = initialY + dy
                    windowManager?.updateViewLayout(bubble, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    bubble.alpha = 0.7f
                    if (isClick) {
                        triggerScreenScanAndSolve()
                    }
                    true
                }
                else -> false
            }
        }

        try {
            windowManager?.addView(bubble, params)
        } catch (_: Exception) {
            // Error adding overlay
        }
    }

    private fun triggerScreenScanAndSolve() {
        val app = applicationContext as? SafaApplication ?: return
        val repo = app.questionRepository

        repo.writeLog(LogLevel.INFO, "EXTERNAL_SCAN", "Pemicu pemindaian layar eksternal ditekan via Floating Bubble.")

        val accessService = SafaAccessibilityService.instance
        if (accessService == null) {
            repo.writeLog(LogLevel.WARN, "ACCESSIBILITY", "Layanan Aksesibilitas SAFA AI belum diaktifkan di Pengaturan Android.")
            showHudToast("[PERINGATAN] Aktifkan Aksesibilitas 'SAFA AI' di Pengaturan Android agar bisa membaca aplikasi lain.")
            return
        }

        val screenText = accessService.scanScreenText()
        if (screenText.isBlank()) {
            repo.writeLog(LogLevel.WARN, "EXTERNAL_SCAN", "Tidak ada teks yang terdeteksi di layar aktif.")
            showHudToast("[KOSONG] Tidak ada teks soal terdeteksi di layar.")
            return
        }

        repo.writeLog(LogLevel.INFO, "EXTERNAL_SCAN", "Teks layar terdeteksi (${screenText.length} karakter). Menganalisis via Gemini...")
        showHudToast("[SCANNING] Menganalisis soal di layar...")

        serviceScope.launch(Dispatchers.IO) {
            try {
                val apiKey = app.authRepository.currentUser.value?.geminiApiKey?.ifBlank { null }
                    ?: com.example.BuildConfig.GEMINI_API_KEY

                val prompt = "Berikut adalah teks dari layar ujian / kuis:\n\n$screenText\n\n" +
                        "Tugasmu:\n" +
                        "1. Temukan soal aktif yang ada di teks.\n" +
                        "2. Berikan jawaban yang tepat (pilihan huruf A/B/C/D atau jawaban teks).\n" +
                        "Format balasan WAJIB:\n" +
                        "JAWABAN: <jawaban>\n" +
                        "ALASAN: <alasan singkat>"

                val resp = GeminiApiClient.api.generateContent(
                    model = "gemini-3.5-flash",
                    apiKey = apiKey,
                    request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                        generationConfig = GeminiGenConfig(temperature = 0.1f)
                    )
                )

                val resultText = resp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                var answer = ""
                for (l in resultText.lines()) {
                    if (l.trim().startsWith("JAWABAN:", ignoreCase = true)) {
                        answer = l.substringAfter(":").trim()
                    }
                }
                if (answer.isBlank()) {
                    answer = resultText.take(50).trim()
                }

                repo.writeLog(LogLevel.SUCCESS, "AI_SOLVED", "Jawaban terdeteksi: '$answer'")

                // Auto click matching option in external app!
                val clicked = accessService.autoClickMatchingOption(answer)

                withContext(Dispatchers.Main) {
                    val status = if (clicked) "[AUTO-CLICKED!]" else "[TERDETEKSI]"
                    showHudToast("$status Jawaban: $answer")
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showHudToast("[ERROR] Gagal: ${e.message}")
                }
            }
        }
    }

    private fun showHudToast(message: String) {
        val app = applicationContext as? SafaApplication ?: return
        app.questionRepository.writeLog(LogLevel.INFO, "HUD_OVERLAY", message)
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
    }

    private fun removeFloatingViews() {
        floatingBubbleView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
            floatingBubbleView = null
        }
        floatingHudCardView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {}
            floatingHudCardView = null
        }
    }
}
