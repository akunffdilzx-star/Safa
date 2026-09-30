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
    NEOBRUTALISM,
    LIQUID_GLASS,
    MINIMAL,
    GELAP,
    SAMUDRA,
    SAKURA;

    companion object {
        val NEOBRUTALISM_DARK = GELAP
        val MINIMALIST_OBSIDIAN = MINIMAL
    }
}

enum class ChatFilter {
    ALL,
    PRIVATE,
    GROUP
}

data class ChatConversation(
    val id: String,
    val name: String,
    val username: String = "",
    val isGroup: Boolean = false,
    val isAi: Boolean = false,
    val memberCount: Int = 1,
    val isVerified: Boolean = false,
    val isPinned: Boolean = false,
    val isStarred: Boolean = false,
    val isArchived: Boolean = false,
    val isSuspended: Boolean = false,
    val avatarInitial: String = "",
    val avatarColor: Long = 0xFF2DD4BF,
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val isDeletedMessage: Boolean = false,
    val hasPhotoAttachment: Boolean = false,
    val isOnline: Boolean = false,
    val unreadCount: Int = 0
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val conversationId: String,
    val senderName: String,
    val senderInitial: String = "",
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
    val isRead: Boolean = true,
    val replyQuoteSnippet: String? = null,
    val replyQuoteSender: String? = null,
    val hasPhoto: Boolean = false,
    val photoCaption: String? = null
)

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
