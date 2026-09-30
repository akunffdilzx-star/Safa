package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Filter1
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberShadow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun FloatingHudOverlay(
    isFloatingEnabled: Boolean,
    statusText: String,
    isExecuting: Boolean,
    onCekAll: () -> Unit,
    onCekOneByOne: () -> Unit,
    onAutoFill: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isFloatingEnabled) return

    var offsetX by remember { mutableFloatStateOf(60f) }
    var offsetY by remember { mutableFloatStateOf(400f) }
    var isMenuOpen by remember { mutableStateOf(false) }
    var isTouching by remember { mutableStateOf(false) }

    // Auto idle fade effect after 3.5 seconds
    LaunchedEffect(isTouching, isMenuOpen) {
        if (!isTouching && !isMenuOpen) {
            delay(3500)
            // will animate to idle alpha
        }
    }

    val alphaTarget = if (isTouching || isMenuOpen) 1.0f else 0.45f
    val buttonAlpha by animateFloatAsState(
        targetValue = alphaTarget,
        animationSpec = tween(500),
        label = "floating_alpha"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Draggable floating emblem
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isTouching = true },
                        onDragEnd = { isTouching = false },
                        onDragCancel = { isTouching = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    )
                }
                .alpha(buttonAlpha)
                .testTag("floating_dragon_button")
        ) {
            // Drop shadow
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .offset(x = 3.dp, y = 3.dp)
                    .background(CyberShadow, RoundedCornerShape(14.dp))
            )

            // Button Body
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(2.dp, NeonCyan, RoundedCornerShape(14.dp))
                    .background(Color(0xFF0A0F1D))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { isMenuOpen = true }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.safa_dragon_logo),
                    contentDescription = "SAFA AI Floating HUD",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                // Neon execution pulse badge
                if (isExecuting) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NeonPink)
                    )
                }
            }
        }

        // Quick Shortcut Panel (Modal Speed-Dial HUD)
        if (isMenuOpen) {
            Dialog(onDismissRequest = { isMenuOpen = false }) {
                NeoGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("floating_quick_hud_panel"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
                ) {
                    Column {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.safa_dragon_logo),
                                    contentDescription = "Logo SAFA AI",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "SAFA AI // QUICK HUD",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = NeonCyan,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "PANEL PINTASAN INSTAN",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            IconButton(
                                onClick = { isMenuOpen = false },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup Menu",
                                    tint = TextWhite
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status pill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                .background(Color(0xFF060910), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "MONITOR: $statusText",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = if (isExecuting) NeonYellow else NeonEmerald,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Instant Action 1: Cek All
                        NeoButton(
                            text = "[1] CEK ALL (MASS SCAN)",
                            onClick = {
                                isMenuOpen = false
                                onCekAll()
                            },
                            icon = Icons.Default.DoneAll,
                            backgroundColor = NeonCyan,
                            textColor = TextDark,
                            borderColor = NeonCyan,
                            enabled = !isExecuting,
                            testTag = "hud_btn_cek_all"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Instant Action 2: Cek 1 per 1
                        NeoButton(
                            text = "[2] CEK 1 PER 1 (BERTAHAP)",
                            onClick = {
                                isMenuOpen = false
                                onCekOneByOne()
                            },
                            icon = Icons.Default.Filter1,
                            backgroundColor = NeonYellow,
                            textColor = TextDark,
                            borderColor = NeonYellow,
                            enabled = !isExecuting,
                            testTag = "hud_btn_cek_one"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Instant Action 3: Auto-Fill
                        NeoButton(
                            text = "[3] AUTO-FILL MANDIRI",
                            onClick = {
                                isMenuOpen = false
                                onAutoFill()
                            },
                            icon = Icons.Default.Bolt,
                            backgroundColor = NeonEmerald,
                            textColor = TextDark,
                            borderColor = NeonEmerald,
                            enabled = !isExecuting,
                            testTag = "hud_btn_auto_fill"
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Akses cepat tanpa perlu beralih ke halaman workspace utama.",
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
