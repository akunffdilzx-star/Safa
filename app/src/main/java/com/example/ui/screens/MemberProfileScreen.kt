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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MemberProfileScreen(
    currentUser: UserEntity?,
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    onSaveApiKey: suspend (String) -> Result<Unit>,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var apiKeyInput by remember(currentUser?.geminiApiKey) {
        mutableStateOf(currentUser?.geminiApiKey ?: "")
    }
    var isKeyVisible by remember { mutableStateOf(false) }
    var saveStatusMsg by remember { mutableStateOf<String?>(null) }
    var isSuccessStatus by remember { mutableStateOf(false) }
    var isTestingKey by remember { mutableStateOf(false) }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // App Bar
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
                    .testTag("btn_member_profile_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = NeonCyan
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "PENGATURAN AKUN & API",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "KONFIGURASI KUNCI GEMINI PRIVAT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // User Identity Card
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonCyan
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(2.dp, NeonCyan, RoundedCornerShape(10.dp))
                            .background(Color(0xFF090E18), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentUser?.username ?: "Guest",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextWhite
                        )
                        Text(
                            text = "Total Eksekusi: ${currentUser?.executionCount ?: 0}x",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                NeoBadge(
                    text = "[ROLE: ${currentUser?.role?.name ?: "MEMBER"}]",
                    color = NeonCyan,
                    textColor = TextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Gemini API Configuration Box (Mandatory Requirement)
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonYellow
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Key",
                            tint = NeonYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "KUNCI GEMINI API PRIVAT",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = TextWhite
                        )
                    }
                    NeoBadge(
                        text = if (apiKeyInput.isNotBlank()) "[TERPASANG]" else "[WAJIB DIISI]",
                        color = if (apiKeyInput.isNotBlank()) NeonEmerald else NeonPink,
                        textColor = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sesuai regulasi sistem SAFA AI, setiap akun Member diwajibkan menggunakan kunci Gemini API pribadi masing-masing untuk menjalankan pemindaian soal mandiri.",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                NeoTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        saveStatusMsg = null
                    },
                    label = "INPUT API KEY GOOGLE GEMINI",
                    placeholder = "AIzaSy...",
                    isPassword = !isKeyVisible,
                    visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                            Icon(
                                imageVector = if (isKeyVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Tampilkan Kunci",
                                tint = TextMuted
                            )
                        }
                    },
                    testTag = "input_gemini_api_key"
                )

                if (saveStatusMsg != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isSuccessStatus) NeonEmerald else NeonPink, RoundedCornerShape(6.dp))
                            .background(if (isSuccessStatus) NeonEmerald.copy(0.15f) else NeonPink.copy(0.15f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = saveStatusMsg!!,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = if (isSuccessStatus) NeonEmerald else NeonPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save Button
                    NeoButton(
                        text = "[SIMPAN KUNCI]",
                        onClick = {
                            if (apiKeyInput.isBlank()) {
                                saveStatusMsg = "[GAGAL] API Key tidak boleh kosong."
                                isSuccessStatus = false
                                return@NeoButton
                            }
                            coroutineScope.launch {
                                val res = onSaveApiKey(apiKeyInput)
                                if (res.isSuccess) {
                                    saveStatusMsg = "[SUKSES] Kunci API privat berhasil disimpan!"
                                    isSuccessStatus = true
                                } else {
                                    saveStatusMsg = "[GAGAL] ${res.exceptionOrNull()?.message}"
                                    isSuccessStatus = false
                                }
                            }
                        },
                        icon = Icons.Default.Check,
                        backgroundColor = NeonEmerald,
                        textColor = TextDark,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_save_api_key"
                    )

                    // Test Button
                    NeoButton(
                        text = if (isTestingKey) "[MENGUJI...]" else "[UJI KONEKSI]",
                        onClick = {
                            if (apiKeyInput.isBlank()) {
                                saveStatusMsg = "[GAGAL] Masukkan API Key terlebih dahulu."
                                isSuccessStatus = false
                                return@NeoButton
                            }
                            isTestingKey = true
                            saveStatusMsg = "[TEST] Menghubungi Google Generative Language..."
                            isSuccessStatus = true

                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val testResp = GeminiApiClient.api.generateContent(
                                        model = selectedModel,
                                        apiKey = apiKeyInput.trim(),
                                        request = GeminiRequest(
                                            contents = listOf(
                                                GeminiContent(
                                                    parts = listOf(GeminiPart(text = "Katakan 'ONLINE'."))
                                                )
                                            )
                                        )
                                    )
                                        val text = testResp.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                                        withContext(Dispatchers.Main) {
                                            isTestingKey = false
                                            if (!text.isNullOrBlank()) {
                                                saveStatusMsg = "[TERHUBUNG] Kunci valid! Respon: ${text.trim()}"
                                                isSuccessStatus = true
                                            } else {
                                                saveStatusMsg = "[GAGAL] Respon kosong dari server Gemini."
                                                isSuccessStatus = false
                                            }
                                        }
                                    } catch (ex: Exception) {
                                        withContext(Dispatchers.Main) {
                                            isTestingKey = false
                                            saveStatusMsg = "[KONEKSI GAGAL] ${ex.message}"
                                            isSuccessStatus = false
                                        }
                                    }
                                }
                        },
                        icon = Icons.Default.Refresh,
                        backgroundColor = NeonYellow,
                        textColor = TextDark,
                        enabled = !isTestingKey,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_test_api_key"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI Model Selection
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = CyberBorder
        ) {
            Column {
                Text(
                    text = "PILIHAN MODEL GEMINI AI",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 1: gemini-3.5-flash
                ModelOptionItem(
                    modelId = "gemini-3.5-flash",
                    displayName = "Gemini 3.5 Flash",
                    tagline = "Kecepatan ultra-tinggi untuk Q&A dan pemindaian massal instan",
                    isSelected = selectedModel == "gemini-3.5-flash",
                    onSelect = { onModelSelected("gemini-3.5-flash") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: gemini-3.1-pro-preview
                ModelOptionItem(
                    modelId = "gemini-3.1-pro-preview",
                    displayName = "Gemini 3.1 Pro Preview",
                    tagline = "Penalaran mendalam untuk soal matematika, STEM, dan esai kompleks",
                    isSelected = selectedModel == "gemini-3.1-pro-preview",
                    onSelect = { onModelSelected("gemini-3.1-pro-preview") }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Logout Action
        NeoButton(
            text = "[KELUAR DARI AKUN]",
            onClick = onLogout,
            icon = Icons.Default.Logout,
            backgroundColor = CyberSurface,
            textColor = NeonPink,
            borderColor = NeonPink,
            testTag = "btn_logout"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ModelOptionItem(
    modelId: String,
    displayName: String,
    tagline: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                if (isSelected) NeonCyan else CyberBorder,
                RoundedCornerShape(8.dp)
            )
            .background(
                if (isSelected) NeonCyan.copy(0.08f) else CyberSurface,
                RoundedCornerShape(8.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayName,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (isSelected) NeonCyan else TextWhite
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        NeoBadge(text = "[AKTIF]", color = NeonCyan, textColor = TextDark)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tagline,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
