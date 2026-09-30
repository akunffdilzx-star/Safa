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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ThemeMode
import com.example.ui.theme.LocalCustomTheme
import com.example.ui.theme.NeoBorder
import com.example.ui.theme.NeoGreen
import com.example.ui.theme.NeoPurple
import com.example.ui.theme.NeoTextDark
import com.example.ui.theme.NeoYellow

@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    isOnlineStatusEnabled: Boolean,
    isReadReceiptEnabled: Boolean,
    groupAddPermission: String,
    isNotificationEnabled: Boolean,
    onSelectTheme: (ThemeMode) -> Unit,
    onToggleOnlineStatus: (Boolean) -> Unit,
    onToggleReadReceipt: (Boolean) -> Unit,
    onSetGroupPermission: (String) -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    onOpenSolverWorkspace: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val theme = LocalCustomTheme.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // HEADER: Back button + "⚙ Pengaturan" (from video at 00:11)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
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

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Pengaturan",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = theme.textColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // USER ACCOUNT HEADER CARD
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
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

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Exyzo Official",
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NeoTextDark
                            )
                            Text(
                                text = "@exyzo_official",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }
            }

            // SECTION AKUN
            item {
                SectionLabel(title = "AKUN")

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { /* Ubah kata sandi */ }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF3B82F6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ubah kata sandi",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Ganti password akun Anda",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // SECTION PRIVASI (Matching video at 00:11 - 00:13)
            item {
                SectionLabel(title = "PRIVASI")

                Spacer(modifier = Modifier.height(6.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Item 1: Status online & terakhir dilihat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF6366F1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Status online & terakhir dilihat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Jika dimatikan, orang lain tidak melihat status online dan waktu terakhir dilihat Anda.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Switch(
                            checked = isOnlineStatusEnabled,
                            onCheckedChange = onToggleOnlineStatus,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeoGreen
                            )
                        )
                    }

                    // Item 2: Tanda dibaca
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF10B981)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Tanda dibaca",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Jika dimatikan, centang oranye tidak muncul di pesan orang lain saat Anda membacanya.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Switch(
                            checked = isReadReceiptEnabled,
                            onCheckedChange = onToggleReadReceipt,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeoGreen
                            )
                        )
                    }

                    // Item 3: Izinkan menambahkan ke grup (Segmented Button: Izinkan / Perlu Izin)
                    Column {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF8B5CF6)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Izinkan menambahkan ke grup",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Atur siapa yang bisa langsung memasukkan Anda ke grup. Jika 'Perlu Izin', Anda akan menerima undangan grup...",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Segmented buttons from video at 00:13
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                    .background(if (groupAddPermission == "Izinkan") NeoPurple else Color.White)
                                    .clickable { onSetGroupPermission("Izinkan") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Izinkan",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (groupAddPermission == "Izinkan") Color.White else NeoTextDark
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.5.dp, NeoBorder, RoundedCornerShape(8.dp))
                                    .background(if (groupAddPermission == "Perlu Izin") NeoPurple else Color.White)
                                    .clickable { onSetGroupPermission("Perlu Izin") },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Perlu Izin",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (groupAddPermission == "Perlu Izin") Color.White else NeoTextDark
                                )
                            }
                        }
                    }

                    // Item 4: Kebijakan Privasi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Policy */ }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF6B7280)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Kebijakan Privasi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Data apa yang kami simpan dan bagaimana digunakan",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // SECTION NOTIFIKASI
            item {
                SectionLabel(title = "NOTIFIKASI")

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Notifikasi pesan baru",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Tampilkan notifikasi saat ada pesan masuk dan app sedang tidak dibuka.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Switch(
                            checked = isNotificationEnabled,
                            onCheckedChange = onToggleNotification,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeoGreen
                            )
                        )
                    }
                }
            }

            // SECTION TEMA (The 6 exact tiles from video at 00:12, 00:23 - 00:26)
            item {
                SectionLabel(title = "TEMA")

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pilih tampilan app. Tema tersimpan di akun Anda dan berlaku di semua perangkat.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2-Column Grid of the 6 Themes
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Row 1: Neobrutalism & Liquid Glass
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeCard(
                            title = "Neobrutalism",
                            subtitle = "Tebal, berani, bawaan",
                            previewColor = NeoYellow,
                            borderColor = NeoBorder,
                            isSelected = currentTheme == ThemeMode.NEOBRUTALISM,
                            onClick = { onSelectTheme(ThemeMode.NEOBRUTALISM) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeCard(
                            title = "Liquid Glass",
                            subtitle = "Kaca transparan & blur",
                            previewColor = Color(0xFF818CF8),
                            borderColor = Color(0xFF818CF8),
                            isSelected = currentTheme == ThemeMode.LIQUID_GLASS,
                            onClick = { onSelectTheme(ThemeMode.LIQUID_GLASS) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: Minimal & Gelap
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeCard(
                            title = "Minimal",
                            subtitle = "Bersih & tipis",
                            previewColor = Color(0xFFE4E4E7),
                            borderColor = Color(0xFF71717A),
                            isSelected = currentTheme == ThemeMode.MINIMAL,
                            onClick = { onSelectTheme(ThemeMode.MINIMAL) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeCard(
                            title = "Gelap",
                            subtitle = "Nyaman di malam hari",
                            previewColor = Color(0xFF1E232E),
                            borderColor = Color(0xFF334155),
                            isSelected = currentTheme == ThemeMode.GELAP,
                            onClick = { onSelectTheme(ThemeMode.GELAP) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 3: Samudra & Sakura
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeCard(
                            title = "Samudra",
                            subtitle = "Biru segar",
                            previewColor = Color(0xFF38BDF8),
                            borderColor = Color(0xFF0284C7),
                            isSelected = currentTheme == ThemeMode.SAMUDRA,
                            onClick = { onSelectTheme(ThemeMode.SAMUDRA) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeCard(
                            title = "Sakura",
                            subtitle = "Pink lembut",
                            previewColor = Color(0xFFF472B6),
                            borderColor = Color(0xFFEC4899),
                            isSelected = currentTheme == ThemeMode.SAKURA,
                            onClick = { onSelectTheme(ThemeMode.SAKURA) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // SECTION ALAT AI & CBT SOLVER
            item {
                SectionLabel(title = "MODUL KHUSUS SAFA AI")

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, NeoBorder, RoundedCornerShape(12.dp))
                        .background(Color(0xFFFEF3C7))
                        .clickable { onOpenSolverWorkspace() }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeoTextDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Buka Workspace SAFA AI Solver",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NeoTextDark
                                )
                                Text(
                                    text = "Auto-click, Browser Web Exam, & Manajemen Soal",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = NeoTextDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color(0xFF6B7280),
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun ThemeCard(
    title: String,
    subtitle: String,
    previewColor: Color,
    borderColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.5.dp else 1.5.dp,
                color = if (isSelected) NeoPurple else NeoBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White)
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column {
            // Visual Preview Box inside theme card (matching video)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(previewColor.copy(alpha = 0.3f))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(previewColor)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeoTextDark
            )

            Text(
                text = subtitle,
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                lineHeight = 12.sp,
                maxLines = 1
            )
        }
    }
}
