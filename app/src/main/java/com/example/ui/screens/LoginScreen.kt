package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
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
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.components.NeoBadge
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoCard
import com.example.ui.components.NeoTextField
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onOpenAbout: () -> Unit,
    onPerformLogin: suspend (String, String) -> Result<Unit>,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("developer") }
    var password by remember { mutableStateOf("admin123") }
    var selectedRoleTab by remember { mutableStateOf(UserRole.DEVELOPER) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Branding Emblem Header
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.5.dp, NeonCyan, RoundedCornerShape(20.dp))
                .background(CyberBorder, RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.safa_dragon_logo),
                contentDescription = "Logo SAFA AI",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "SAFA AI",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            color = TextWhite,
            letterSpacing = 1.sp
        )

        Text(
            text = "ARTIFICIAL INTELLIGENCE EXECUTION ENGINE",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = NeonCyan,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Role Selector Tabs
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Developer Tab
                val isDev = selectedRoleTab == UserRole.DEVELOPER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDev) NeonCyan else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable {
                            selectedRoleTab = UserRole.DEVELOPER
                            username = "developer"
                            password = "admin123"
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_developer"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ROLE: DEVELOPER]",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (isDev) TextDark else TextMuted
                    )
                }

                // Member Tab
                val isMember = selectedRoleTab == UserRole.MEMBER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isMember) NeonYellow else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable {
                            selectedRoleTab = UserRole.MEMBER
                            username = "member01"
                            password = "safa2026"
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_member"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "[ROLE: MEMBER]",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = if (isMember) TextDark else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Form Card
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (selectedRoleTab == UserRole.DEVELOPER) NeonCyan else NeonYellow
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "AUTENTIKASI SISTEM",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextWhite
                    )
                    NeoBadge(
                        text = selectedRoleTab.name,
                        color = if (selectedRoleTab == UserRole.DEVELOPER) NeonCyan else NeonYellow,
                        textColor = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                NeoTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    label = "USERNAME AKUN",
                    placeholder = "Masukkan username terdaftar",
                    leadingIcon = Icons.Default.Person,
                    testTag = "input_username"
                )

                Spacer(modifier = Modifier.height(14.dp))

                NeoTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = "KATA SANDI",
                    placeholder = "Masukkan password",
                    isPassword = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = Icons.Default.Lock,
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Lihat Password",
                                tint = TextMuted
                            )
                        }
                    },
                    testTag = "input_password"
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonPink, RoundedCornerShape(6.dp))
                            .background(NeonPink.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "[ERROR] $errorMessage",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = NeonPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                NeoButton(
                    text = if (isSubmitting) "[MEMPROSES...]" else "[MASUK KE SISTEM]",
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Username dan password tidak boleh kosong."
                            return@NeoButton
                        }
                        isSubmitting = true
                        errorMessage = null
                        coroutineScope.launch {
                            val result = onPerformLogin(username, password)
                            isSubmitting = false
                            if (result.isSuccess) {
                                onLoginSuccess()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Gagal login"
                            }
                        }
                    },
                    backgroundColor = if (selectedRoleTab == UserRole.DEVELOPER) NeonCyan else NeonYellow,
                    textColor = TextDark,
                    enabled = !isSubmitting,
                    testTag = "btn_submit_login"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Demo Preset Switcher Info
                Text(
                    text = if (selectedRoleTab == UserRole.DEVELOPER)
                        "Mode Developer: Akses panel pembuatan member & manajemen sistem."
                    else
                        "Mode Member: Masuk akun yang dibuat Developer. Wajib konfigurasi API key privat.",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Official Contact & Info Button
        NeoButton(
            text = "[INFORMASI PENGEMBANG & KONTAK]",
            onClick = onOpenAbout,
            icon = Icons.Default.Info,
            backgroundColor = CyberBorder,
            textColor = TextWhite,
            borderColor = CyberBorder,
            testTag = "btn_open_about"
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
