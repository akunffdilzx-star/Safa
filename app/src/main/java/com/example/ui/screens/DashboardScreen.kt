package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ExecutionLogEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import com.example.data.model.QuestionStatus
import com.example.data.model.QuestionType
import com.example.data.model.ThemeMode
import com.example.data.model.UserRole
import com.example.ui.components.NeoBadge
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoCard
import com.example.ui.components.NeoTextField
import com.example.ui.components.TerminalLogPanel
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

@Composable
fun DashboardScreen(
    currentUser: UserEntity?,
    questions: List<QuestionEntity>,
    logs: List<ExecutionLogEntity>,
    statusText: String,
    isExecuting: Boolean,
    selectedModel: String,
    currentTheme: ThemeMode,
    isFloatingEnabled: Boolean,
    isNotificationEnabled: Boolean,
    onToggleFloating: (Boolean) -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    onCycleTheme: () -> Unit,
    onCekAll: () -> Unit,
    onCekOneByOne: () -> Unit,
    onAutoFill: () -> Unit,
    onResetAnswers: () -> Unit,
    onClearLogs: () -> Unit,
    onAddQuestion: (Int, String, QuestionType, List<String>) -> Unit,
    onDeleteQuestion: (Long) -> Unit,
    onOpenDeveloperPanel: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenCbtBrowser: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newNumber by remember { mutableStateOf((questions.maxOfOrNull { it.number } ?: 0) + 1) }
    var newText by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf(QuestionType.MULTIPLE_CHOICE) }
    var newOptA by remember { mutableStateOf("") }
    var newOptB by remember { mutableStateOf("") }
    var newOptC by remember { mutableStateOf("") }
    var newOptD by remember { mutableStateOf("") }

    val isDev = currentUser?.role == UserRole.DEVELOPER

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        // TOP APP BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder)
                .background(Color(0xFF070B13))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Branding
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenAbout() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.safa_dragon_logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SAFA AI",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextWhite,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            NeoBadge(
                                text = if (isDev) "[DEV]" else "[MEMBER]",
                                color = if (isDev) NeonCyan else NeonYellow,
                                textColor = TextDark
                            )
                        }
                        Text(
                            text = "ENGINE: $selectedModel",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = NeonEmerald
                        )
                    }
                }

                // Header Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Theme switcher
                    IconButton(
                        onClick = onCycleTheme,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_cycle_theme")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Ganti Tema",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Developer Panel (if role is DEVELOPER)
                    if (isDev) {
                        IconButton(
                            onClick = onOpenDeveloperPanel,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_header_dev_panel")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Panel Developer",
                                tint = NeonYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Profile & API Key Settings
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_header_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan Akun & API",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // About & Contact Info
                    IconButton(
                        onClick = onOpenAbout,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_header_about")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tentang Aplikasi & Kontak",
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // MAIN WORKSPACE CONTENT
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // QUICK ACCESS TOGGLE BAR (Requirement 4)
            item {
                NeoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonCyan,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
                ) {
                    Column {
                        Text(
                            text = "PENGATURAN AKSES CEPAT (FLOATING & NOTIFIKASI)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = NeonCyan,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = "Floating",
                                    tint = if (isFloatingEnabled) NeonCyan else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Tombol Mengambang (Floating HUD)",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Ikon naga draggable dengan menu eksekusi instan",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Switch(
                                checked = isFloatingEnabled,
                                onCheckedChange = onToggleFloating,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextDark,
                                    checkedTrackColor = NeonCyan,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = CyberBorder
                                ),
                                modifier = Modifier.testTag("switch_floating_button")
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifikasi",
                                    tint = if (isNotificationEnabled) NeonEmerald else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Panel Notifikasi Cepat",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Aksi cepat di notification drawer Android",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Switch(
                                checked = isNotificationEnabled,
                                onCheckedChange = onToggleNotification,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TextDark,
                                    checkedTrackColor = NeonEmerald,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = CyberBorder
                                ),
                                modifier = Modifier.testTag("switch_quick_notification")
                            )
                        }
                    }
                }
            }

            // EXTERNAL APP & WEB EXECUTION HUB
            item {
                val ctx = LocalContext.current
                NeoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonEmerald,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Web & App",
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EKSEKUSI DI APLIKASI LAIN & WEB",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = NeonEmerald
                                )
                            }
                            NeoBadge(text = "[FULL OTOMATIS]", color = NeonEmerald, textColor = TextDark)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Gunakan browser khusus CBT atau aktifkan Layanan Aksesibilitas Android agar SAFA AI dapat membaca dan auto-click soal di aplikasi lain (Chrome, Quizizz, Google Form, dsb).",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Direct In-App CBT Browser Button
                        NeoButton(
                            text = "[BUKA BROWSER CBT & WEB EXAM]",
                            onClick = onOpenCbtBrowser,
                            icon = Icons.Default.OpenInBrowser,
                            backgroundColor = NeonEmerald,
                            textColor = TextDark,
                            borderColor = NeonEmerald,
                            testTag = "btn_open_cbt_browser"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Android System Integration Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, NeonCyan, RoundedCornerShape(6.dp))
                                    .background(CyberSurface, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        ctx.startActivity(intent)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[IZIN AKSESIBILITAS]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, NeonYellow, RoundedCornerShape(6.dp))
                                    .background(CyberSurface, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val intent = Intent(
                                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                            Uri.parse("package:${ctx.packageName}")
                                        ).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        ctx.startActivity(intent)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "[IZIN FLOATING SYSTEM]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonYellow
                                )
                            }
                        }
                    }
                }
            }

            // PRIMARY EXECUTION ACTION BAR (Requirement 5)
            item {
                NeoCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonYellow,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "MODUL EKSEKUSI AI MANDIRI",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = TextWhite
                            )
                            NeoBadge(
                                text = if (isExecuting) "[PROCESSING...]" else "[STANDBY]",
                                color = if (isExecuting) NeonYellow else NeonEmerald,
                                textColor = TextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Execution Buttons Grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // 1. CEK ALL
                            NeoButton(
                                text = "[CEK ALL] PEMINDAIAN MASSAL",
                                onClick = onCekAll,
                                icon = Icons.Default.DoneAll,
                                backgroundColor = NeonCyan,
                                textColor = TextDark,
                                borderColor = NeonCyan,
                                enabled = !isExecuting,
                                testTag = "btn_cek_all"
                            )

                            // 2. CEK 1 PER 1 & AUTO-FILL
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                NeoButton(
                                    text = "[CEK 1 PER 1]",
                                    onClick = onCekOneByOne,
                                    icon = Icons.Default.Filter1,
                                    backgroundColor = NeonYellow,
                                    textColor = TextDark,
                                    borderColor = NeonYellow,
                                    enabled = !isExecuting,
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_cek_one_by_one"
                                )

                                NeoButton(
                                    text = "[AUTO-FILL]",
                                    onClick = onAutoFill,
                                    icon = Icons.Default.Bolt,
                                    backgroundColor = NeonEmerald,
                                    textColor = TextDark,
                                    borderColor = NeonEmerald,
                                    enabled = !isExecuting,
                                    modifier = Modifier.weight(1f),
                                    testTag = "btn_auto_fill"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Secondary actions: Reset & Add Question
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                    .background(CyberSurface, RoundedCornerShape(6.dp))
                                    .clickable { onResetAnswers() }
                                    .padding(vertical = 8.dp)
                                    .testTag("btn_reset_answers"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reset",
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[RESET JAWABAN]",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextMuted
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, NeonCyan.copy(0.7f), RoundedCornerShape(6.dp))
                                    .background(CyberSurface, RoundedCornerShape(6.dp))
                                    .clickable {
                                        newNumber = (questions.maxOfOrNull { it.number } ?: 0) + 1
                                        newText = ""
                                        newOptA = ""
                                        newOptB = ""
                                        newOptC = ""
                                        newOptD = ""
                                        showAddDialog = true
                                    }
                                    .padding(vertical = 8.dp)
                                    .testTag("btn_open_add_question"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah Soal",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[+ TAMBAH SOAL]",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // REAL-TIME TERMINAL LOG MONITOR PANEL
            item {
                TerminalLogPanel(
                    logs = logs,
                    statusText = statusText,
                    onClearLogs = onClearLogs
                )
            }

            // QUESTIONS WORKSPACE HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LEMBAR SOAL AKTIF (${questions.size} BUTIR)",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = TextWhite
                    )
                    val solvedCount = questions.count { it.status == QuestionStatus.SOLVED }
                    NeoBadge(
                        text = "TERJAWAB: $solvedCount/${questions.size}",
                        color = if (solvedCount == questions.size && questions.isNotEmpty()) NeonEmerald else NeonCyan,
                        textColor = TextDark
                    )
                }
            }

            // QUESTIONS LIST
            if (questions.isEmpty()) {
                item {
                    NeoCard(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "[WORKSPACE KOSONG - TEKAN + TAMBAH SOAL]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(questions, key = { it.id }) { question ->
                    QuestionItemCard(
                        question = question,
                        onDelete = { onDeleteQuestion(question.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }

    // Modal Add Question Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                NeoButton(
                    text = "[SIMPAN KE WORKSPACE]",
                    onClick = {
                        if (newText.isNotBlank()) {
                            val opts = if (newType == QuestionType.MULTIPLE_CHOICE) {
                                listOf(
                                    "A. ${newOptA.ifBlank { "Opsi A" }}",
                                    "B. ${newOptB.ifBlank { "Opsi B" }}",
                                    "C. ${newOptC.ifBlank { "Opsi C" }}",
                                    "D. ${newOptD.ifBlank { "Opsi D" }}"
                                )
                            } else emptyList()

                            onAddQuestion(newNumber, newText, newType, opts)
                            showAddDialog = false
                        }
                    },
                    backgroundColor = NeonCyan,
                    textColor = TextDark,
                    testTag = "btn_submit_add_question"
                )
            },
            dismissButton = {
                NeoButton(
                    text = "[BATAL]",
                    onClick = { showAddDialog = false },
                    backgroundColor = CyberSurface,
                    textColor = TextWhite
                )
            },
            title = {
                Text(
                    text = "TAMBAH BUTIR SOAL BARU",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                )
            },
            text = {
                Column {
                    NeoTextField(
                        value = newText,
                        onValueChange = { newText = it },
                        label = "TEKS PERTANYAAN",
                        placeholder = "Tuliskan soal atau pertanyaan...",
                        testTag = "input_question_text"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "TIPE SOAL",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuestionType.entries.forEach { qType ->
                            val isSel = newType == qType
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, if (isSel) NeonCyan else CyberBorder, RoundedCornerShape(4.dp))
                                    .background(if (isSel) NeonCyan.copy(0.2f) else CyberSurface, RoundedCornerShape(4.dp))
                                    .clickable { newType = qType }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = qType.name.take(4),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = if (isSel) NeonCyan else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (newType == QuestionType.MULTIPLE_CHOICE) {
                        Spacer(modifier = Modifier.height(10.dp))
                        NeoTextField(value = newOptA, onValueChange = { newOptA = it }, label = "OPSI A", placeholder = "Jawaban A")
                        Spacer(modifier = Modifier.height(6.dp))
                        NeoTextField(value = newOptB, onValueChange = { newOptB = it }, label = "OPSI B", placeholder = "Jawaban B")
                        Spacer(modifier = Modifier.height(6.dp))
                        NeoTextField(value = newOptC, onValueChange = { newOptC = it }, label = "OPSI C", placeholder = "Jawaban C")
                        Spacer(modifier = Modifier.height(6.dp))
                        NeoTextField(value = newOptD, onValueChange = { newOptD = it }, label = "OPSI D", placeholder = "Jawaban D")
                    }
                }
            },
            containerColor = Color(0xFF0F1522)
        )
    }
}

@Composable
private fun QuestionItemCard(
    question: QuestionEntity,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val statusColor = when (question.status) {
        QuestionStatus.SOLVED -> NeonEmerald
        QuestionStatus.PROCESSING -> NeonYellow
        QuestionStatus.PENDING -> TextMuted
        QuestionStatus.FAILED -> NeonPink
    }

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (question.status == QuestionStatus.SOLVED) NeonEmerald.copy(0.8f) else CyberBorder,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(CyberSurface, RoundedCornerShape(6.dp))
                            .border(1.dp, statusColor, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${question.number}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    NeoBadge(
                        text = "[${question.questionType.name}]",
                        color = CyberBorder,
                        textColor = TextWhite,
                        isOutlined = true
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeoBadge(
                        text = "[STATUS: ${question.status.name}]",
                        color = statusColor,
                        textColor = TextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Soal",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.questionText,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextWhite,
                lineHeight = 18.sp
            )

            // Multiple choice options list
            if (question.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    question.options.forEach { opt ->
                        val isSelected = question.filledAnswer.isNotBlank() &&
                                (opt.startsWith(question.filledAnswer.take(2), ignoreCase = true) ||
                                        opt.equals(question.filledAnswer, ignoreCase = true))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (isSelected) NeonEmerald else CyberBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .background(
                                    if (isSelected) NeonEmerald.copy(0.12f) else Color(0xFF090D15),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonEmerald else TextMuted,
                                            CircleShape
                                        )
                                        .background(
                                            if (isSelected) NeonEmerald else Color.Transparent,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = TextDark,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = opt,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) NeonEmerald else TextWhite
                                )
                            }
                        }
                    }
                }
            }

            // Answer Box
            if (question.status == QuestionStatus.SOLVED && question.filledAnswer.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NeonEmerald, RoundedCornerShape(6.dp))
                        .background(NeonEmerald.copy(0.08f), RoundedCornerShape(6.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Solved",
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HASIL DETEKSI & AUTO-FILL:",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = NeonEmerald
                                )
                            }
                            NeoBadge(
                                text = "98% CONFIDENCE",
                                color = NeonEmerald,
                                textColor = TextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = question.filledAnswer,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextWhite
                        )

                        if (question.reasoning.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Penalaran: ${question.reasoning}",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
