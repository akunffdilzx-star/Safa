package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.data.model.LogLevel
import com.example.data.model.QuestionStatus
import com.example.data.model.QuestionType
import com.example.data.model.UserRole
import org.json.JSONArray

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val role: UserRole,
    val geminiApiKey: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = "",
    val executionCount: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: Int,
    val questionText: String,
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val options: List<String> = emptyList(),
    val detectedAnswer: String = "",
    val filledAnswer: String = "",
    val confidence: Float = 0f,
    val reasoning: String = "",
    val status: QuestionStatus = QuestionStatus.PENDING,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "execution_logs")
data class ExecutionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: String,
    val tag: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO,
    val createdAt: Long = System.currentTimeMillis()
)

class DatabaseConverters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = try {
        UserRole.valueOf(value)
    } catch (_: Exception) {
        UserRole.MEMBER
    }

    @TypeConverter
    fun fromQuestionStatus(value: QuestionStatus): String = value.name

    @TypeConverter
    fun toQuestionStatus(value: String): QuestionStatus = try {
        QuestionStatus.valueOf(value)
    } catch (_: Exception) {
        QuestionStatus.PENDING
    }

    @TypeConverter
    fun fromQuestionType(value: QuestionType): String = value.name

    @TypeConverter
    fun toQuestionType(value: String): QuestionType = try {
        QuestionType.valueOf(value)
    } catch (_: Exception) {
        QuestionType.MULTIPLE_CHOICE
    }

    @TypeConverter
    fun fromLogLevel(value: LogLevel): String = value.name

    @TypeConverter
    fun toLogLevel(value: String): LogLevel = try {
        LogLevel.valueOf(value)
    } catch (_: Exception) {
        LogLevel.INFO
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(data)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {
            // fallback
        }
        return list
    }
}
