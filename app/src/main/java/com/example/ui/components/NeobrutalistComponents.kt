package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberShadow
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceGlass
import com.example.ui.theme.LocalCustomTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Neobrutalist Hard-Shadow Card with optional liquid glass translucent fill
 */
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    borderColor: Color = LocalCustomTheme.current.borderColor,
    backgroundColor: Color = LocalCustomTheme.current.cardBackground,
    shadowColor: Color = LocalCustomTheme.current.shadowColor,
    shadowOffset: Dp = 4.dp,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 10.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Physical hard-shadow layer behind
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, RoundedCornerShape(cornerRadius))
        )
        // Foreground interactive surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .background(backgroundColor, RoundedCornerShape(cornerRadius))
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Liquid Glass Neobrutalist Card with gradient outline
 */
@Composable
fun NeoGlassCard(
    modifier: Modifier = Modifier,
    borderBrush: Brush = Brush.linearGradient(
        listOf(NeonCyan, Color(0xFF7C4DFF))
    ),
    backgroundColor: Color = CyberSurfaceGlass,
    shadowOffset: Dp = 4.dp,
    cornerRadius: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        // Shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(CyberShadow, RoundedCornerShape(cornerRadius))
        )
        // Main glass card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, borderBrush), RoundedCornerShape(cornerRadius))
                .background(backgroundColor, RoundedCornerShape(cornerRadius))
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/**
 * Tactile Neobrutalist Action Button with press feedback and custom colorways
 */
@Composable
fun NeoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    backgroundColor: Color = LocalCustomTheme.current.accentColor,
    textColor: Color = TextDark,
    borderColor: Color = LocalCustomTheme.current.borderColor,
    shadowColor: Color = LocalCustomTheme.current.shadowColor,
    enabled: Boolean = true,
    height: Dp = 48.dp,
    testTag: String = "neo_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 4.dp,
        label = "btn_offset"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(
                    if (enabled) shadowColor else Color.Transparent,
                    RoundedCornerShape(8.dp)
                )
        )

        // Front Face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(
                    x = if (isPressed) 3.dp else 0.dp,
                    y = if (isPressed) 3.dp else 0.dp
                )
                .border(
                    2.dp,
                    if (enabled) borderColor else CyberBorder,
                    RoundedCornerShape(8.dp)
                )
                .background(
                    if (enabled) backgroundColor else Color(0xFF1E293B),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = if (enabled) textColor else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) textColor else TextMuted,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * Neobrutalist Clean Input Field with custom border and label
 */
@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    borderColor: Color = LocalCustomTheme.current.borderColor,
    testTag: String = "neo_text_field"
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(CyberShadow, RoundedCornerShape(8.dp))
            )

            // Input surface
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
                    .background(CyberSurface, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = label,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextMuted,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        visualTransformation = visualTransformation,
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = TextWhite,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(NeonCyan),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (trailingIcon != null) {
                    trailingIcon()
                }
            }
        }
    }
}

/**
 * Cyber Badges for Status, Roles, and Tags (Zero Emojis)
 */
@Composable
fun NeoBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    textColor: Color = TextDark,
    isOutlined: Boolean = false
) {
    if (isOutlined) {
        Box(
            modifier = modifier
                .border(1.dp, color, RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = color,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    } else {
        Box(
            modifier = modifier
                .background(color, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}
