package com.example.data.repository

import com.example.BuildConfig
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRepository(
    private val scope: CoroutineScope
) {
    private val timeFormat = SimpleDateFormat("HH.mm", Locale.getDefault())

    // Initial conversations exactly from the user's video!
    private val _conversations = MutableStateFlow<List<ChatConversation>>(
        listOf(
            ChatConversation(
                id = "chat_info",
                name = "Information | update fitur | sar...",
                isGroup = true,
                memberCount = 102,
                isVerified = true,
                isPinned = true,
                isStarred = true,
                lastMessage = "Foto",
                lastMessageTime = "22.54",
                hasPhotoAttachment = true,
                avatarInitial = "I",
                avatarColor = 0xFF18181B
            ),
            ChatConversation(
                id = "chat_store",
                name = "GB' Exyzo Store",
                isGroup = true,
                memberCount = 54,
                isPinned = true,
                lastMessage = "Penangguhan grup ini telah dicabut.",
                lastMessageTime = "11.23",
                avatarInitial = "GB",
                avatarColor = 0xFFFFFFFF
            ),
            ChatConversation(
                id = "chat_exyzo_02",
                name = "Exyzo Official 02",
                isGroup = false,
                isVerified = true,
                isOnline = true,
                lastMessage = "!",
                lastMessageTime = "10.57",
                avatarInitial = "EO",
                avatarColor = 0xFF10B981
            ),
            ChatConversation(
                id = "chat_basori",
                name = "Basori Asor",
                isGroup = false,
                isSuspended = true,
                lastMessage = "Belum ada pesan",
                lastMessageTime = "",
                avatarInitial = "BA",
                avatarColor = 0xFF3B82F6
            ),
            ChatConversation(
                id = "chat_jus_buah",
                name = "Jus Buah",
                isGroup = false,
                isDeletedMessage = true,
                lastMessage = "Pesan ini telah dihapus",
                lastMessageTime = "21.54",
                avatarInitial = "JB",
                avatarColor = 0xFF059669
            ),
            ChatConversation(
                id = "chat_vinn",
                name = "Vinn_Xyz",
                isGroup = false,
                isDeletedMessage = true,
                lastMessage = "Pesan ini telah dihapus",
                lastMessageTime = "20.23",
                avatarInitial = "V",
                avatarColor = 0xFF4B5563
            ),
            ChatConversation(
                id = "chat_dilz",
                name = "Dilz",
                username = "@dilz",
                isGroup = false,
                lastMessage = "Kontol",
                lastMessageTime = "18.24",
                avatarInitial = "D",
                avatarColor = 0xFF38BDF8
            ),
            ChatConversation(
                id = "chat_xyzora_ai",
                name = "Xyzora AI / SAFA AI",
                username = "@xyzora_ai",
                isGroup = false,
                isAi = true,
                isVerified = true,
                isOnline = true,
                lastMessage = "Halo! Tanya saya tentang soal ujian, kode, atau obrolan apapun.",
                lastMessageTime = "Sekarang",
                avatarInitial = "AI",
                avatarColor = 0xFF8B5CF6
            )
        )
    )
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _activeConversation = MutableStateFlow<ChatConversation?>(null)
    val activeConversation: StateFlow<ChatConversation?> = _activeConversation.asStateFlow()

    // Store messages per conversation
    private val messagesMap = mutableMapOf<String, MutableList<ChatMessage>>()

    private val _currentMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessage>> = _currentMessages.asStateFlow()

    init {
        // Pre-populate Dilz chat messages exactly from the video (at 00:03 - 00:05)
        messagesMap["chat_dilz"] = mutableListOf(
            ChatMessage(
                conversationId = "chat_dilz",
                senderName = "Dilz",
                text = "Woi",
                timestamp = "18.22",
                isOutgoing = false
            ),
            ChatMessage(
                conversationId = "chat_dilz",
                senderName = "Dilz",
                text = "Bakso kontol bakso kontol",
                timestamp = "18.22",
                isOutgoing = false
            ),
            ChatMessage(
                conversationId = "chat_dilz",
                senderName = "Dilz",
                text = "P",
                timestamp = "18.22",
                isOutgoing = false
            ),
            ChatMessage(
                conversationId = "chat_dilz",
                senderName = "Dilz",
                text = "P",
                timestamp = "18.22",
                isOutgoing = false
            ),
            ChatMessage(
                conversationId = "chat_dilz",
                senderName = "Dilz",
                text = "P",
                timestamp = "18.22",
                isOutgoing = false
            ),
            ChatMessage(
                conversationId = "chat_dilz",
                senderName = "Saya",
                text = "Kontol",
                timestamp = "18.24",
                isOutgoing = true,
                isRead = true
            )
        )

        // Pre-populate Group Chat messages from video (at 00:19 - 00:20)
        messagesMap["chat_info"] = mutableListOf(
            ChatMessage(
                conversationId = "chat_info",
                senderName = "U",
                senderInitial = "U",
                text = "web spam otp 1k aja",
                timestamp = "22.48",
                isOutgoing = false,
                hasPhoto = true,
                photoCaption = "target_phone_number.png"
            ),
            ChatMessage(
                conversationId = "chat_info",
                senderName = "Saya",
                text = "yang suruh promosi siapa su?",
                timestamp = "22.53",
                isOutgoing = true,
                replyQuoteSnippet = "Foto",
                replyQuoteSender = "Unknown"
            ),
            ChatMessage(
                conversationId = "chat_info",
                senderName = "Zyyreccz",
                senderInitial = "Z",
                text = "Belom rilis aja udah rame",
                timestamp = "18.36",
                isOutgoing = false,
                replyQuoteSnippet = "wkwkwk iya lagi",
                replyQuoteSender = "rtfystore"
            ),
            ChatMessage(
                conversationId = "chat_info",
                senderName = "V5",
                senderInitial = "V",
                text = "ha alah",
                timestamp = "18.36",
                isOutgoing = false
            ),
            ChatMessage(
                conversationId = "chat_info",
                senderName = "CielLAzure",
                senderInitial = "C",
                text = "optimasi masih kurang tapi",
                timestamp = "19.14",
                isOutgoing = false,
                replyQuoteSnippet = "@exyzo_official rilisin aja dulu post TT and yt pasti rame",
                replyQuoteSender = "Zyyreccz"
            )
        )

        // Pre-populate AI Chat
        messagesMap["chat_xyzora_ai"] = mutableListOf(
            ChatMessage(
                conversationId = "chat_xyzora_ai",
                senderName = "Xyzora AI",
                text = "Halo! Saya asisten cerdas Xyzora / SAFA AI. Ketik pertanyaan, soal ujian, kuis, atau teks apapun di sini!",
                timestamp = "12.00",
                isOutgoing = false
            )
        )
    }

    fun openConversation(conv: ChatConversation) {
        _activeConversation.value = conv
        val list = messagesMap.getOrPut(conv.id) { mutableListOf() }
        _currentMessages.value = list.toList()
    }

    fun closeConversation() {
        _activeConversation.value = null
        _currentMessages.value = emptyList()
    }

    fun sendMessage(text: String, customApiKey: String? = null) {
        val conv = _activeConversation.value ?: return
        if (text.isBlank()) return

        val now = timeFormat.format(Date())
        val userMsg = ChatMessage(
            conversationId = conv.id,
            senderName = "Saya",
            text = text.trim(),
            timestamp = now,
            isOutgoing = true,
            isRead = true
        )

        val list = messagesMap.getOrPut(conv.id) { mutableListOf() }
        list.add(userMsg)
        _currentMessages.value = list.toList()

        // Update conversation last message
        updateLastMessage(conv.id, text.trim(), now)

        // If chatting with AI, respond via Gemini API
        if (conv.isAi) {
            scope.launch(Dispatchers.IO) {
                try {
                    val key = customApiKey?.ifBlank { null }
                        ?: (if (BuildConfig.GEMINI_API_KEY != "DEFAULT_API_KEY") BuildConfig.GEMINI_API_KEY else "")
                    val prompt = "User mengirim pesan di aplikasi chat Xyzora:\n\"${text.trim()}\"\n" +
                            "Balaslah secara natural, cerdas, ramah, dan solutif. Jika ini pertanyaan ujian atau soal, berikan jawaban tepat beserta alasan singkatnya."

                    val response = GeminiApiClient.api.generateContent(
                        model = "gemini-3.5-flash",
                        apiKey = key,
                        request = GeminiRequest(
                            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                            generationConfig = GeminiGenConfig(temperature = 0.5f)
                        )
                    )

                    val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "Maaf, tidak dapat menghasilkan balasan saat ini."

                    val replyTime = timeFormat.format(Date())
                    val aiMsg = ChatMessage(
                        conversationId = conv.id,
                        senderName = conv.name,
                        text = replyText.trim(),
                        timestamp = replyTime,
                        isOutgoing = false
                    )

                    list.add(aiMsg)
                    _currentMessages.value = list.toList()
                    updateLastMessage(conv.id, replyText.take(40).trim(), replyTime)

                } catch (e: Exception) {
                    val errorTime = timeFormat.format(Date())
                    val errorMsg = ChatMessage(
                        conversationId = conv.id,
                        senderName = conv.name,
                        text = "Gagal memproses pesan: ${e.message}",
                        timestamp = errorTime,
                        isOutgoing = false
                    )
                    list.add(errorMsg)
                    _currentMessages.value = list.toList()
                }
            }
        }
    }

    fun startNewChatWith(name: String) {
        val id = "chat_${System.currentTimeMillis()}"
        val initial = name.take(2).uppercase()
        val newConv = ChatConversation(
            id = id,
            name = name,
            username = "@${name.lowercase().replace(" ", "_")}",
            isGroup = false,
            lastMessage = "Percakapan baru dimulai",
            lastMessageTime = timeFormat.format(Date()),
            avatarInitial = initial,
            avatarColor = 0xFF2DD4BF
        )
        val currentList = _conversations.value.toMutableList()
        currentList.add(0, newConv)
        _conversations.value = currentList
        openConversation(newConv)
    }

    fun createNewGroup(name: String) {
        val id = "group_${System.currentTimeMillis()}"
        val initial = name.take(2).uppercase()
        val newGroup = ChatConversation(
            id = id,
            name = name,
            isGroup = true,
            memberCount = 1,
            lastMessage = "Grup dibuat",
            lastMessageTime = timeFormat.format(Date()),
            avatarInitial = initial,
            avatarColor = 0xFF8B5CF6
        )
        val currentList = _conversations.value.toMutableList()
        currentList.add(0, newGroup)
        _conversations.value = currentList
        openConversation(newGroup)
    }

    private fun updateLastMessage(convId: String, lastMsg: String, time: String) {
        val updated = _conversations.value.map {
            if (it.id == convId) {
                it.copy(lastMessage = lastMsg, lastMessageTime = time)
            } else it
        }
        _conversations.value = updated
    }
}
