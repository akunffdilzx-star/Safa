package com.example.data.model

enum class UserRole {
    DEVELOPER,
    MEMBER
}

enum class QuestionStatus {
    PENDING,
    PROCESSING,
    SOLVED,
    FAILED
}

enum class QuestionType {
    MULTIPLE_CHOICE,
    ESSAY,
    SHORT_ANSWER
}

enum class ThemeMode {
    LIQUID_GLASS,
    NEOBRUTALISM_DARK,
    MINIMALIST_OBSIDIAN
}

enum class LogLevel {
    INFO,
    SUCCESS,
    WARN,
    ERROR
}

data class ExecutionLog(
    val id: Long = 0,
    val timestamp: String,
    val tag: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO
)
