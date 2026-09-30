package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.QuestionStatus
import com.example.data.model.QuestionType
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        QuestionEntity::class,
        ExecutionLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun questionDao(): QuestionDao
    abstract fun executionLogDao(): ExecutionLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "safa_ai_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.userDao(), database.questionDao(), database.executionLogDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(
            userDao: UserDao,
            questionDao: QuestionDao,
            logDao: ExecutionLogDao
        ) {
            // Seed Developer Account
            userDao.insertUser(
                UserEntity(
                    username = "developer",
                    passwordHash = "admin123",
                    role = UserRole.DEVELOPER,
                    geminiApiKey = "",
                    isActive = true,
                    notes = "Akun Root Developer SAFA AI"
                )
            )

            // Seed Sample Member Account
            userDao.insertUser(
                UserEntity(
                    username = "member01",
                    passwordHash = "safa2026",
                    role = UserRole.MEMBER,
                    geminiApiKey = "",
                    isActive = true,
                    notes = "Akun Member Aktif - SAFA AI Testing"
                )
            )

            // Seed initial mock exam / quiz questions for instant testing
            val initialQuestions = listOf(
                QuestionEntity(
                    number = 1,
                    questionText = "Manakah dari teknologi berikut yang merupakan arsitektur deep learning fondasi dari Large Language Models modern seperti Gemini?",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    options = listOf(
                        "A. Convolutional Neural Network (CNN)",
                        "B. Transformer Architecture",
                        "C. Recurrent Neural Network (RNN)",
                        "D. Multi-Layer Perceptron (MLP)"
                    )
                ),
                QuestionEntity(
                    number = 2,
                    questionText = "Protokol keamanan komunikasi data yang menggunakan kriptografi simetris dan asimetris untuk mengamankan transmisi HTTP di internet adalah...",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    options = listOf(
                        "A. FTP (File Transfer Protocol)",
                        "B. TLS/SSL (Transport Layer Security)",
                        "C. SMTP (Simple Mail Transfer)",
                        "D. SNMP (Network Management)"
                    )
                ),
                QuestionEntity(
                    number = 3,
                    questionText = "Jika diketahui matriks A berukuran 2x3 dan matriks B berukuran 3x4, maka ordo dari hasil perkalian matriks C = A x B adalah...",
                    questionType = QuestionType.MULTIPLE_CHOICE,
                    options = listOf(
                        "A. 2 x 4",
                        "B. 3 x 3",
                        "C. 2 x 3",
                        "D. 3 x 2"
                    )
                ),
                QuestionEntity(
                    number = 4,
                    questionText = "Jelaskan secara ringkas prinsip kerja mekanisme Self-Attention dalam pemrosesan bahasa alami (NLP)!",
                    questionType = QuestionType.ESSAY,
                    options = emptyList()
                ),
                QuestionEntity(
                    number = 5,
                    questionText = "Nama komponen di Android Jetpack yang bertindak sebagai jembatan reaktif antara UI Compose dan repository data layer adalah...",
                    questionType = QuestionType.SHORT_ANSWER,
                    options = emptyList()
                )
            )
            questionDao.insertAll(initialQuestions)

            // Initial boot log
            logDao.insertLog(
                ExecutionLogEntity(
                    timestamp = "00:00:01",
                    tag = "BOOT",
                    message = "SAFA AI Engine initialized. Database ready. 5 question nodes mounted."
                )
            )
        }
    }
}
