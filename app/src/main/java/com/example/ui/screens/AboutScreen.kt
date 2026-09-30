package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.NeoBadge
import com.example.ui.components.NeoButton
import com.example.ui.components.NeoCard
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
fun AboutScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Top App Bar
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
                    .testTag("btn_about_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = NeonCyan
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "INFORMASI SISTEM",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PROFIL RESMI & KONTAK PENGEMBANG",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Hero Card
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonCyan
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, NeonCyan, RoundedCornerShape(16.dp))
                        .background(CyberBorder, RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.safa_dragon_logo),
                        contentDescription = "Logo SAFA AI",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SAFA AI // CORE v2.5",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextWhite
                )

                Text(
                    text = "ARTIFICIAL INTELLIGENCE AUTONOMOUS SOLVER",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = NeonCyan,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoBadge(text = "[NEOBRUTALISM]", color = NeonCyan, textColor = TextDark)
                    NeoBadge(text = "[LIQUID GLASS]", color = NeonEmerald, textColor = TextDark)
                    NeoBadge(text = "[GEMINI 3.5]", color = NeonYellow, textColor = TextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Official Contact Section (Mandatory Requirement)
        Text(
            text = "KONTAK RESMI PENGEMBANG (DEVELOPER)",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = NeonYellow,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // TikTok Contact Card
        NeoCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@dablezx"))
                    context.startActivity(intent)
                }
                .testTag("contact_tiktok"),
            borderColor = NeonPink,
            backgroundColor = CyberSurface
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .border(1.5.dp, NeonPink, RoundedCornerShape(8.dp))
                            .background(NeonPink.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "TikTok",
                            tint = NeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AKUN TIKTOK RESMI",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "@dablezx",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = NeonPink
                        )
                    }
                }
                NeoBadge(text = "[BUKA TIKTOK]", color = NeonPink, textColor = TextWhite)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Email Contact Card
        NeoCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:safaaudioofficial@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Inquiry SAFA AI System")
                    }
                    context.startActivity(Intent.createChooser(intent, "Kirim Email"))
                }
                .testTag("contact_email"),
            borderColor = NeonCyan,
            backgroundColor = CyberSurface
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .border(1.5.dp, NeonCyan, RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ALAMAT EMAIL RESMI",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "safaaudioofficial@gmail.com",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NeonCyan
                        )
                    }
                }
                NeoBadge(text = "[KIRIM EMAIL]", color = NeonCyan, textColor = TextDark)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Technical Specs Neobrutalist Box
        Text(
            text = "SPESIFIKASI ARSITEKTUR APLIKASI",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = TextWhite,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = CyberBorder
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecRow(label = "FRAMEWORK", value = "Kotlin + Jetpack Compose M3")
                SpecRow(label = "PERSISTENSI", value = "Room Database + SQLite Local")
                SpecRow(label = "INTEGRASI AI", value = "Google Gemini REST API Engine")
                SpecRow(label = "GAYA DESAIN", value = "Neobrutalism & Liquid Glass Shader")
                SpecRow(label = "SISTEM AKSES", value = "Floating Draggable HUD + Quick Action")
                SpecRow(label = "ROLE SECURITY", value = "Dual Authority: DEVELOPER & MEMBER")
                SpecRow(label = "EMOJI COMPLIANCE", value = "Zero Emojis - Full Graphic Symbols")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NeoButton(
            text = "[KEMBALI KE WORKSPACE]",
            onClick = onNavigateBack,
            backgroundColor = NeonCyan,
            textColor = TextDark,
            testTag = "btn_about_done"
        )
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )
    }
}
