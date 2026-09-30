package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.AuthRepository
import com.example.data.repository.QuestionRepository
import com.example.service.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SafaApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val authRepository by lazy { AuthRepository(database.userDao(), database.executionLogDao()) }
    val questionRepository by lazy { QuestionRepository(database.questionDao(), database.executionLogDao(), authRepository) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
