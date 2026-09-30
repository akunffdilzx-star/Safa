package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

data class CustomThemeConfig(
    val themeMode: ThemeMode = ThemeMode.LIQUID_GLASS,
    val cardBackground: Color = CyberSurfaceGlass,
    val borderColor: Color = NeonCyan,
    val accentColor: Color = NeonCyan,
    val shadowColor: Color = CyberShadow,
    val isGlass: Boolean = true
)

val LocalCustomTheme = staticCompositionLocalOf { CustomThemeConfig() }

private val SafaLiquidGlassScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = TextDark,
    primaryContainer = Color(0xFF0F2B36),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFF281E48),
    tertiary = NeonEmerald,
    onTertiary = TextDark,
    background = CyberBackground,
    onBackground = TextWhite,
    surface = CyberSurface,
    onSurface = TextWhite,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = NeonCyan
)

private val SafaNeobrutalismDarkScheme = darkColorScheme(
    primary = NeonYellow,
    onPrimary = TextDark,
    primaryContainer = Color(0xFF383205),
    onPrimaryContainer = NeonYellow,
    secondary = NeonPink,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFF480F21),
    tertiary = NeonCyan,
    onTertiary = TextDark,
    background = Color(0xFF040608),
    onBackground = TextWhite,
    surface = Color(0xFF0B0E14),
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF141923),
    onSurfaceVariant = TextMuted,
    outline = NeonYellow
)

private val SafaMinimalistObsidianScheme = darkColorScheme(
    primary = Color(0xFFE2E8F0),
    onPrimary = TextDark,
    primaryContainer = Color(0xFF334155),
    onPrimaryContainer = Color(0xFFF8FAFC),
    secondary = Color(0xFF94A3B8),
    onSecondary = TextDark,
    secondaryContainer = Color(0xFF1E293B),
    tertiary = NeonEmerald,
    onTertiary = TextDark,
    background = Color(0xFF020305),
    onBackground = TextWhite,
    surface = Color(0xFF0A0C10),
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF151821),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun SafaTheme(
    themeMode: ThemeMode = ThemeMode.LIQUID_GLASS,
    content: @Composable () -> Unit
) {
    val (scheme, config) = when (themeMode) {
        ThemeMode.LIQUID_GLASS -> Pair(
            SafaLiquidGlassScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                cardBackground = CyberSurfaceGlass,
                borderColor = NeonCyan,
                accentColor = NeonCyan,
                shadowColor = CyberShadow,
                isGlass = true
            )
        )
        ThemeMode.NEOBRUTALISM_DARK -> Pair(
            SafaNeobrutalismDarkScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                cardBackground = Color(0xFF0E131C),
                borderColor = NeonYellow,
                accentColor = NeonYellow,
                shadowColor = Color(0xFF000000),
                isGlass = false
            )
        )
        ThemeMode.MINIMALIST_OBSIDIAN -> Pair(
            SafaMinimalistObsidianScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                cardBackground = Color(0xFF0D1017),
                borderColor = Color(0xFF64748B),
                accentColor = Color(0xFFE2E8F0),
                shadowColor = Color(0xFF000000),
                isGlass = false
            )
        )
    }

    CompositionLocalProvider(LocalCustomTheme provides config) {
        MaterialTheme(
            colorScheme = scheme,
            typography = Typography,
            content = content
        )
    }
}
