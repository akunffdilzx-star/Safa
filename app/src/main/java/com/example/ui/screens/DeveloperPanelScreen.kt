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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.ui.components.NeoBadge
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoCard
import com.example.ui.components.NeoTextField
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeveloperPanelScreen(
    members: List<UserEntity>,
    onNavigateBack: () -> Unit,
    onCreateMember: suspend (String, String, String) -> Result<Long>,
    onToggleMemberStatus: (UserEntity) -> Unit,
    onResetPassword: (Long, String) -> Unit,
    onDeleteMember: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newNotes by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var createMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessMessage by remember { mutableStateOf(false) }

    var resetDialogUser by remember { mutableStateOf<UserEntity?>(null) }
    var resetNewPassword by remember { mutableStateOf("") }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    val filteredMembers = members.filter {
        it.username.contains(searchQuery.trim(), ignoreCase = true) ||
                it.notes.contains(searchQuery.trim(), ignoreCase = true)
    }

    val activeCount = members.count { it.isActive }
    val totalExecutions = members.sumOf { it.executionCount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .border(1.5.dp, NeonCyan, RoundedCornerShape(8.dp))
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .testTag("btn_dev_panel_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = NeonCyan
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PANEL DEVELOPER",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextWhite,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NeoBadge(text = "[ROOT]", color = NeonCyan, textColor = TextDark)
                }
                Text(
                    text = "MANAJEMEN PENDAFTARAN & HAK AKSES MEMBER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NeonYellow
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DevStatCard(
                title = "TOTAL MEMBER",
                value = "${members.size}",
                accentColor = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            DevStatCard(
                title = "MEMBER AKTIF",
                value = "$activeCount",
                accentColor = NeonEmerald,
                modifier = Modifier.weight(1f)
            )
            DevStatCard(
                title = "EKSEKUSI AI",
                value = "$totalExecutions",
                accentColor = NeonYellow,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: Create New Member Card
            item {
                NeoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DAFTARKAN MEMBER BARU",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextWhite
                            )
                            NeoBadge(text = "[INPUT DATA]", color = NeonCyan, textColor = TextDark)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        NeoTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = "USERNAME MEMBER",
                            placeholder = "Contoh: member02",
                            leadingIcon = Icons.Default.Person,
                            testTag = "input_new_member_user"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeoTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "PASSWORD MEMBER",
                            placeholder = "Minimal 6 karakter",
                            leadingIcon = Icons.Default.Key,
                            testTag = "input_new_member_pass"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        NeoTextField(
                            value = newNotes,
                            onValueChange = { newNotes = it },
                            label = "CATATAN / KETERANGAN",
                            placeholder = "Contoh: Siswa Kelas 12 IPA - Aktif",
                            testTag = "input_new_member_notes"
                        )

                        if (createMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, if (isSuccessMessage) NeonEmerald else NeonPink, RoundedCornerShape(6.dp))
                                    .background(if (isSuccessMessage) NeonEmerald.copy(0.15f) else NeonPink.copy(0.15f), RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = createMessage!!,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = if (isSuccessMessage) NeonEmerald else NeonPink,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        NeoButton(
                            text = "[SIMPAN & REGISTRASIKAN MEMBER]",
                            onClick = {
                                if (newUsername.isBlank() || newPassword.isBlank()) {
                                    createMessage = "[ERROR] Username dan password wajib diisi."
                                    isSuccessMessage = false
                                    return@NeoButton
                                }
                                coroutineScope.launch {
                                    val res = onCreateMember(newUsername, newPassword, newNotes)
                                    if (res.isSuccess) {
                                        createMessage = "[SUKSES] Akun '${newUsername.trim()}' berhasil didaftarkan!"
                                        isSuccessMessage = true
                                        newUsername = ""
                                        newPassword = ""
                                        newNotes = ""
                                    } else {
                                        createMessage = "[GAGAL] ${res.exceptionOrNull()?.message}"
                                        isSuccessMessage = false
                                    }
                                }
                            },
                            icon = Icons.Default.Add,
                            backgroundColor = NeonCyan,
                            textColor = TextDark,
                            testTag = "btn_create_member_submit"
                        )
                    }
                }
            }

            // Section 2: Member Management Search & List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DAFTAR AKUN MEMBER TERDAFTAR",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = NeonYellow
                    )
                    Text(
                        text = "${filteredMembers.size} Akun",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            item {
                NeoTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "CARI MEMBER",
                    placeholder = "Cari berdasarkan nama atau catatan...",
                    leadingIcon = Icons.Default.Search,
                    testTag = "input_search_member"
                )
            }

            if (filteredMembers.isEmpty()) {
                item {
                    NeoCard(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "[TIDAK ADA DATA MEMBER YANG COCOK]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(filteredMembers, key = { it.id }) { member ->
                    MemberRowCard(
                        member = member,
                        onToggleStatus = { onToggleMemberStatus(member) },
                        onOpenReset = {
                            resetDialogUser = member
                            resetNewPassword = ""
                        },
                        onDelete = { onDeleteMember(member.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Reset Password Modal
    if (resetDialogUser != null) {
        AlertDialog(
            onDismissRequest = { resetDialogUser = null },
            confirmButton = {
                NeoButton(
                    text = "[SIMPAN PASSWORD BARU]",
                    onClick = {
                        if (resetNewPassword.isNotBlank()) {
                            onResetPassword(resetDialogUser!!.id, resetNewPassword)
                            resetDialogUser = null
                        }
                    },
                    backgroundColor = NeonCyan,
                    textColor = TextDark,
                    testTag = "btn_confirm_reset_pass"
                )
            },
            dismissButton = {
                NeoButton(
                    text = "[BATAL]",
                    onClick = { resetDialogUser = null },
                    backgroundColor = CyberSurface,
                    textColor = TextWhite,
                    testTag = "btn_cancel_reset_pass"
                )
            },
            title = {
                Text(
                    text = "RESET KATA SANDI",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                )
            },
            text = {
                Column {
                    Text(
                        text = "Reset password untuk member: ${resetDialogUser?.username}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NeoTextField(
                        value = resetNewPassword,
                        onValueChange = { resetNewPassword = it },
                        label = "PASSWORD BARU",
                        placeholder = "Masukkan password baru",
                        testTag = "input_dialog_reset_pass"
                    )
                }
            },
            containerColor = Color(0xFF0F1626)
        )
    }
}

@Composable
private fun DevStatCard(
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    NeoCard(
        modifier = modifier,
        borderColor = accentColor,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = accentColor
            )
        }
    }
}

@Composable
private fun MemberRowCard(
    member: UserEntity,
    onToggleStatus: () -> Unit,
    onOpenReset: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(member.createdAt))
    val hasApiKey = member.geminiApiKey.isNotBlank()

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (member.isActive) NeonCyan.copy(0.7f) else NeonPink.copy(0.7f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, if (member.isActive) NeonCyan else NeonPink, RoundedCornerShape(8.dp))
                            .background(Color(0xFF070B14), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Member",
                            tint = if (member.isActive) NeonCyan else NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = member.username,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Dibuat: $dateStr",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NeoBadge(
                        text = if (member.isActive) "[AKTIF]" else "[SUSPEND]",
                        color = if (member.isActive) NeonEmerald else NeonPink,
                        textColor = TextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (hasApiKey) "[API KEY: TERPASANG]" else "[API KEY: KOSONG]",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hasApiKey) NeonEmerald else NeonYellow
                )
                Text(
                    text = "Eksekusi: ${member.executionCount}x",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TextWhite
                )
            }

            if (member.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan: ${member.notes}",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Toggle status
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, if (member.isActive) NeonPink else NeonEmerald, RoundedCornerShape(6.dp))
                        .background(CyberSurface, RoundedCornerShape(6.dp))
                        .clickable { onToggleStatus() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (member.isActive) "[NONAKTIFKAN]" else "[AKTIFKAN]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (member.isActive) NeonPink else NeonEmerald
                    )
                }

                // Reset pass
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NeonYellow, RoundedCornerShape(6.dp))
                        .background(CyberSurface, RoundedCornerShape(6.dp))
                        .clickable { onOpenReset() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[RESET PASS]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonYellow
                    )
                }

                // Delete
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                        .background(CyberSurface, RoundedCornerShape(6.dp))
                        .clickable { onDelete() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[HAPUS]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
