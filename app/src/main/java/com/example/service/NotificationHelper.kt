package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID = "safa_quick_channel"
    const val NOTIFICATION_ID = 2026

    const val ACTION_CEK_ALL = "com.aistudio.safaai.ACTION_CEK_ALL"
    const val ACTION_CEK_ONE = "com.aistudio.safaai.ACTION_CEK_ONE"
    const val ACTION_AUTO_FILL = "com.aistudio.safaai.ACTION_AUTO_FILL"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SAFA AI Akses Cepat"
            val descriptionText = "Notifikasi panel eksekusi instan SAFA AI"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showQuickNotification(context: Context, statusMessage: String = "Sistem Aktif - Siap Eksekusi") {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cekAllIntent = Intent(context, QuickActionReceiver::class.java).apply {
            action = ACTION_CEK_ALL
        }
        val cekAllPending = PendingIntent.getBroadcast(
            context,
            1,
            cekAllIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cekOneIntent = Intent(context, QuickActionReceiver::class.java).apply {
            action = ACTION_CEK_ONE
        }
        val cekOnePending = PendingIntent.getBroadcast(
            context,
            2,
            cekOneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val autoFillIntent = Intent(context, QuickActionReceiver::class.java).apply {
            action = ACTION_AUTO_FILL
        }
        val autoFillPending = PendingIntent.getBroadcast(
            context,
            3,
            autoFillIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("SAFA AI // QUICK EXECUTION")
            .setContentText(statusMessage)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(R.mipmap.ic_launcher, "CEK ALL", cekAllPending)
            .addAction(R.mipmap.ic_launcher, "CEK 1/1", cekOnePending)
            .addAction(R.mipmap.ic_launcher, "AUTO-FILL", autoFillPending)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Notification permission might not be granted yet
        }
    }

    fun cancelQuickNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }
}
