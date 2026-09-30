package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatConversation
import com.example.data.model.ChatMessage
import com.example.ui.theme.LocalCustomTheme
import com.example.ui.theme.NeoBorder
import com.example.ui.theme.NeoTeal
import com.example.ui.theme.NeoTextDark
import com.example.ui.theme.NeoYellow

@Composable
fun ChatRoomScreen(
    conversation: ChatConversation?,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val theme = LocalCustomTheme.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
    ) {
        // TOP APP BAR (from Video at 00:03)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.cardBackground)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button & Contact Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Back square button with black border
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = NeoTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Circle Avatar with border
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, NeoBorder, CircleShape)
                            .background(Color(conversation?.avatarColor ?: 0xFF38BDF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation?.avatarInitial ?: "D",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NeoTextDark
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = conversation?.name ?: "Chat",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = theme.textColor,
                            maxLines = 1
                        )
                        Text(
                            text = when {
                                conversation?.isGroup == true -> "${conversation.memberCount} anggota"
                                conversation?.isOnline == true -> "Online"
                                conversation?.isAi == true -> "AI Siaga"
                                else -> "Offline"
                            },
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                // Call & Video & Settings square buttons (from video)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ChatHeaderIconButton(icon = Icons.Default.Call, desc = "Panggilan Suara")
                    ChatHeaderIconButton(icon = Icons.Default.Videocam, desc = "Panggilan Video")
                    ChatHeaderIconButton(icon = Icons.Default.MoreVert, desc = "Pengaturan")
                }
            }
        }

        // CHAT BODY
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            // END-TO-END ENCRYPTION NOTICE (Matching video exactly)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = NeoTextDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pesan dienkripsi end-to-end. Hanya anggota chat ini yang bisa membacanya, server tidak.",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = NeoTextDark
                        )
                    }
                }
            }

            // Message list
            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // BOTTOM INPUT BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.cardBackground)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Plus / Attachment Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Lampiran",
                        tint = NeoTextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Text Field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(21.dp))
                        .background(Color.White)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        singleLine = false,
                        maxLines = 4,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = NeoTextDark
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    onSendMessage(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                        cursorBrush = SolidColor(NeoTextDark),
                        decorationBox = { inner ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = if (conversation?.isAi == true) "Tanya Xyzora AI..." else "Ketik pesan...",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                            inner()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_chat_message")
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                        .background(NeoTeal)
                        .clickable {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        }
                        .testTag("btn_send_chat"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim",
                        tint = NeoTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isOut = message.isOutgoing

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isOut) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, NeoBorder, RoundedCornerShape(12.dp))
                .background(if (isOut) NeoYellow else Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                // Sender name in group chats
                if (!isOut && message.senderName != "Saya" && message.senderName.isNotBlank()) {
                    Text(
                        text = message.senderName,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF7C3AED)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Quoted reply snippet if any
                if (!message.replyQuoteSnippet.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(6.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(6.dp)
                    ) {
                        Column {
                            message.replyQuoteSender?.let {
                                Text(
                                    text = it,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4B5563)
                                )
                            }
                            Text(
                                text = message.replyQuoteSnippet,
                                fontSize = 10.sp,
                                color = Color(0xFF6B7280),
                                maxLines = 1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Message Text
                Text(
                    text = message.text,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    color = NeoTextDark
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Timestamp and Checkmark
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = Color(0xFF6B7280)
                    )

                    if (isOut) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Terkirim",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatHeaderIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable { /* noop */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = NeoTextDark,
            modifier = Modifier.size(18.dp)
        )
    }
}
