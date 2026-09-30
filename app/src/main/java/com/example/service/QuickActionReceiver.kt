package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.SafaApplication
import com.example.data.model.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuickActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as? SafaApplication ?: return
        val questionRepo = app.questionRepository

        when (intent.action) {
            NotificationHelper.ACTION_CEK_ALL -> {
                questionRepo.writeLog(LogLevel.INFO, "NOTIFICATION", "Trigger aksi cepat 'CEK ALL' via notifikasi.")
                NotificationHelper.showQuickNotification(context, "Menjalankan Cek All...")
                CoroutineScope(Dispatchers.IO).launch {
                    val res = questionRepo.executeCekAll()
                    val msg = if (res.isSuccess) "Cek All Selesai (${res.getOrNull()} soal)" else "Cek All Gagal"
                    NotificationHelper.showQuickNotification(context, msg)
                }
            }
            NotificationHelper.ACTION_CEK_ONE -> {
                questionRepo.writeLog(LogLevel.INFO, "NOTIFICATION", "Trigger aksi cepat 'CEK 1/1' via notifikasi.")
                NotificationHelper.showQuickNotification(context, "Menjalankan Cek 1 per 1...")
                CoroutineScope(Dispatchers.IO).launch {
                    val res = questionRepo.executeCekOneByOne()
                    val msg = if (res.isSuccess) "Cek 1/1 Selesai (${res.getOrNull()} soal)" else "Cek 1/1 Gagal"
                    NotificationHelper.showQuickNotification(context, msg)
                }
            }
            NotificationHelper.ACTION_AUTO_FILL -> {
                questionRepo.writeLog(LogLevel.INFO, "NOTIFICATION", "Trigger aksi cepat 'AUTO-FILL' via notifikasi.")
                NotificationHelper.showQuickNotification(context, "Menjalankan Auto-Fill...")
                CoroutineScope(Dispatchers.IO).launch {
                    val res = questionRepo.executeAutoFill()
                    val msg = if (res.isSuccess) "Auto-Fill Selesai (${res.getOrNull()} elemen)" else "Auto-Fill Gagal"
                    NotificationHelper.showQuickNotification(context, msg)
                }
            }
        }
    }
}
