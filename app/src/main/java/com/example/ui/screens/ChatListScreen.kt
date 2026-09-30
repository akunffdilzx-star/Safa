package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ChatConversation
import com.example.data.model.ChatFilter
import com.example.ui.theme.LocalCustomTheme
import com.example.ui.theme.NeoBorder
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoOrange
import com.example.ui.theme.NeoPurple
import com.example.ui.theme.NeoRed
import com.example.ui.theme.NeoTeal
import com.example.ui.theme.NeoTextDark
import com.example.ui.theme.NeoYellow

@Composable
fun ChatListScreen(
    conversations: List<ChatConversation>,
    onOpenChat: (ChatConversation) -> Unit,
    onOpenProfile: () -> Unit,
    onStartNewChat: (String) -> Unit,
    onCreateGroup: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalCustomTheme.current

    var selectedFilter by remember { mutableStateOf(ChatFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showNewChatModal by remember { mutableStateOf(false) }
    var showNewGroupModal by remember { mutableStateOf(false) }
    var newGroupNameInput by remember { mutableStateOf("") }

    val filteredConversations = conversations.filter { conv ->
        val matchesFilter = when (selectedFilter) {
            ChatFilter.ALL -> true
            ChatFilter.PRIVATE -> !conv.isGroup
            ChatFilter.GROUP -> conv.isGroup
        }
        val matchesSearch = conv.name.contains(searchQuery, ignoreCase = true) ||
                conv.lastMessage.contains(searchQuery, ignoreCase = true)
        matchesFilter && matchesSearch
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // TOP HEADER BAR (From Video)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Profile Avatar + Name + Online Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onOpenProfile() }
                        .padding(vertical = 4.dp)
                ) {
                    // Avatar circle with purple border
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeoPurple, CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EO",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = NeoPurple
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Exyzo Official",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = theme.textColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Orange verified check badge
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = NeoOrange,
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(NeoGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Online",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeoGreen
                            )
                        }
                    }
                }

                // Top Right Two Square Neobrutalist Action Buttons (from video)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Compose / Note Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                            .background(theme.cardBackground, RoundedCornerShape(8.dp))
                            .clickable { showCreateDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "Pesan Baru",
                            tint = theme.textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Camera / QR Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                            .background(theme.cardBackground, RoundedCornerShape(8.dp))
                            .clickable { onOpenProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Kamera / Profil",
                            tint = theme.textColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SEARCH BAR ("Cari percakapan...")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(1.5.dp, NeoBorder, RoundedCornerShape(22.dp))
                    .background(Color.White, RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = NeoTextDark
                        ),
                        cursorBrush = SolidColor(NeoPurple),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Cari percakapan...",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CATEGORY FILTER PILLS ("Semua", "Pribadi", "Grup")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterPill(
                    title = "Semua",
                    isSelected = selectedFilter == ChatFilter.ALL,
                    onClick = { selectedFilter = ChatFilter.ALL }
                )
                FilterPill(
                    title = "Pribadi",
                    isSelected = selectedFilter == ChatFilter.PRIVATE,
                    onClick = { selectedFilter = ChatFilter.PRIVATE }
                )
                FilterPill(
                    title = "Grup",
                    isSelected = selectedFilter == ChatFilter.GROUP,
                    onClick = { selectedFilter = ChatFilter.GROUP }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "DIARSIPKAN" ROW (From Video at 00:00 & 00:06)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Diarsipkan",
                        tint = NeoPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Diarsipkan",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = theme.textColor
                    )
                }

                Text(
                    text = "1",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = NeoPurple
                )
            }

            // CHAT LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(filteredConversations, key = { it.id }) { conv ->
                    ChatListItem(
                        conversation = conv,
                        textColor = theme.textColor,
                        onClick = { onOpenChat(conv) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // FLOATING ACTION BUTTON (+) -> Opens "+ Buat Baru" Dialog
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = NeoTeal,
            contentColor = NeoTextDark,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .border(2.dp, NeoBorder, CircleShape)
                .testTag("fab_buat_baru")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buat Baru",
                modifier = Modifier.size(28.dp)
            )
        }

        // "+ BUAT BARU" DIALOG (Exactly matching video at 00:01)
        if (showCreateDialog) {
            Dialog(onDismissRequest = { showCreateDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, NeoBorder, RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "+ Buat Baru",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = NeoTextDark,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Button 1: Pesan Baru
                        NeoActionDialogButton(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            text = "Pesan Baru",
                            onClick = {
                                showCreateDialog = false
                                showNewChatModal = true
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Button 2: Buat Grup
                        NeoActionDialogButton(
                            icon = Icons.Default.Group,
                            text = "Buat Grup",
                            onClick = {
                                showCreateDialog = false
                                showNewGroupModal = true
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Button 3: Chat dengan Xyzora AI
                        NeoActionDialogButton(
                            icon = Icons.Default.AutoAwesome,
                            text = "Chat dengan Xyzora AI",
                            onClick = {
                                showCreateDialog = false
                                val aiConv = conversations.firstOrNull { it.isAi }
                                if (aiConv != null) {
                                    onOpenChat(aiConv)
                                } else {
                                    onStartNewChat("Xyzora AI")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Button 4: Batal
                        NeoActionDialogButton(
                            icon = Icons.Default.Close,
                            text = "Batal",
                            onClick = { showCreateDialog = false }
                        )
                    }
                }
            }
        }

        // "MULAI CHAT BARU" MODAL (Matching video at 00:02)
        if (showNewChatModal) {
            Dialog(onDismissRequest = { showNewChatModal = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, NeoBorder, RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = NeoTextDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mulai Chat Baru",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = NeoTextDark
                            )
                        }

                        // Search Contact
                        var contactSearch by remember { mutableStateOf("") }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = contactSearch,
                                onValueChange = { contactSearch = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 13.sp, color = NeoTextDark),
                                decorationBox = { inner ->
                                    if (contactSearch.isEmpty()) {
                                        Text("Cari pengguna...", fontSize = 13.sp, color = Color.Gray)
                                    }
                                    inner()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Contacts from video: Dilz, Rio, Fizzx, exyzo_official, denz
                        val contacts = listOf(
                            Pair("Dilz", "D"),
                            Pair("Rio", "R"),
                            Pair("Fizzx", "F"),
                            Pair("exyzo_official", "E"),
                            Pair("denz", "D")
                        ).filter { it.first.contains(contactSearch, ignoreCase = true) }

                        LazyColumn(modifier = Modifier.height(200.dp)) {
                            items(contacts) { (name, initial) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showNewChatModal = false
                                            onStartNewChat(name)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, NeoBorder, CircleShape)
                                            .background(NeoTeal),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initial,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = NeoTextDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = name,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NeoTextDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tutup Button
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                .background(NeoTeal)
                                .clickable { showNewChatModal = false }
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Tutup",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = NeoTextDark
                            )
                        }
                    }
                }
            }
        }

        // CREATE GROUP MODAL
        if (showNewGroupModal) {
            Dialog(onDismissRequest = { showNewGroupModal = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, NeoBorder, RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Buat Grup Baru",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = NeoTextDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = newGroupNameInput,
                                onValueChange = { newGroupNameInput = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 13.sp, color = NeoTextDark),
                                decorationBox = { inner ->
                                    if (newGroupNameInput.isEmpty()) {
                                        Text("Nama Grup...", fontSize = 13.sp, color = Color.Gray)
                                    }
                                    inner()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .clickable { showNewGroupModal = false }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Batal", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeoTextDark)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                    .background(NeoTeal)
                                    .clickable {
                                        if (newGroupNameInput.isNotBlank()) {
                                            showNewGroupModal = false
                                            onCreateGroup(newGroupNameInput.trim())
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Buat", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NeoTextDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.5.dp, NeoBorder, RoundedCornerShape(20.dp))
            .background(if (isSelected) NeoPurple else Color.White)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else NeoTextDark
        )
    }
}

@Composable
private fun ChatListItem(
    conversation: ChatConversation,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circle Avatar with border
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.5.dp, NeoBorder, CircleShape)
                .background(Color(conversation.avatarColor)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = conversation.avatarInitial,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (conversation.avatarColor == 0xFF18181B) Color.White else NeoTextDark
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = conversation.name,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = textColor,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (conversation.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = NeoOrange,
                        modifier = Modifier.size(14.dp)
                    )
                }

                if (conversation.isSuspended) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeoRed)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Ditangguhkan",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (conversation.hasPhotoAttachment) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Foto",
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }

                Text(
                    text = conversation.lastMessage,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    color = if (conversation.isDeletedMessage) Color.Gray else Color(0xFF6B7280),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Time and Pin/Star Icons
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = conversation.lastMessageTime,
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (conversation.isStarred) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = NeoYellow,
                        modifier = Modifier.size(13.dp)
                    )
                }
                if (conversation.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pin",
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NeoActionDialogButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, NeoBorder, RoundedCornerShape(10.dp))
            .background(NeoTeal)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeoTextDark,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = NeoTextDark
            )
        }
    }
}
