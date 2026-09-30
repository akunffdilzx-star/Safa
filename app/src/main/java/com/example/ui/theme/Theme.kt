package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.model.ThemeMode

data class CustomThemeConfig(
    val themeMode: ThemeMode = ThemeMode.NEOBRUTALISM,
    val backgroundColor: Color = NeoBackground,
    val cardBackground: Color = NeoCardBg,
    val borderColor: Color = NeoBorder,
    val accentColor: Color = NeoYellow,
    val pillActiveColor: Color = NeoPurple,
    val actionButtonColor: Color = NeoTeal,
    val textColor: Color = NeoTextDark,
    val textMutedColor: Color = NeoTextMuted,
    val shadowColor: Color = Color(0x33000000),
    val isGlass: Boolean = false,
    val isDark: Boolean = false
)

val LocalCustomTheme = staticCompositionLocalOf { CustomThemeConfig() }

// 1. Neobrutalism (Tebal, berani, bawaan - from video)
private val NeobrutalismScheme = lightColorScheme(
    primary = NeoYellow,
    onPrimary = NeoTextDark,
    primaryContainer = NeoYellow,
    onPrimaryContainer = NeoTextDark,
    secondary = NeoPurple,
    onSecondary = Color.White,
    secondaryContainer = NeoPurpleLight,
    tertiary = NeoTeal,
    onTertiary = NeoTextDark,
    background = NeoBackground,
    onBackground = NeoTextDark,
    surface = NeoCardBg,
    onSurface = NeoTextDark,
    outline = NeoBorder
)

// 2. Liquid Glass (Kaca transparan & blur)
private val LiquidGlassScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = TextDark,
    secondary = NeonPurple,
    tertiary = NeonEmerald,
    background = Color(0xFF0D131F),
    surface = Color(0xCC131C2E),
    onSurface = TextWhite,
    outline = NeonCyan
)

// 3. Minimal (Bersih & tipis)
private val MinimalScheme = lightColorScheme(
    primary = Color(0xFF18181B),
    onPrimary = Color.White,
    secondary = Color(0xFF71717A),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF18181B),
    outline = Color(0xFFE4E4E7)
)

// 4. Gelap (Nyaman di malam hari)
private val GelapScheme = darkColorScheme(
    primary = Color(0xFFF43F5E),
    onPrimary = Color.White,
    secondary = Color(0xFF6366F1),
    background = Color(0xFF0F1115),
    surface = Color(0xFF171A21),
    onSurface = Color(0xFFF1F5F9),
    outline = Color(0xFF2E3440)
)

// 5. Samudra (Biru segar)
private val SamudraScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    secondary = Color(0xFF0EA5E9),
    background = Color(0xFFF0F9FF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0C4A6E),
    outline = Color(0xFF0284C7)
)

// 6. Sakura (Pink lembut)
private val SakuraScheme = lightColorScheme(
    primary = Color(0xFFEC4899),
    onPrimary = Color.White,
    secondary = Color(0xFFF472B6),
    background = Color(0xFFFDF2F8),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF831843),
    outline = Color(0xFFF43F5E)
)

@Composable
fun SafaTheme(
    themeMode: ThemeMode = ThemeMode.NEOBRUTALISM,
    content: @Composable () -> Unit
) {
    val (scheme, config) = when (themeMode) {
        ThemeMode.NEOBRUTALISM -> Pair(
            NeobrutalismScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                backgroundColor = NeoBackground,
                cardBackground = NeoCardBg,
                borderColor = NeoBorder,
                accentColor = NeoYellow,
                pillActiveColor = NeoPurple,
                actionButtonColor = NeoTeal,
                textColor = NeoTextDark,
                textMutedColor = NeoTextMuted,
                shadowColor = Color(0x40000000),
                isGlass = false,
                isDark = false
            )
        )
        ThemeMode.LIQUID_GLASS -> Pair(
            LiquidGlassScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                backgroundColor = Color(0xFF0B101B),
                cardBackground = Color(0xCC131D31),
                borderColor = NeonCyan,
                accentColor = NeonCyan,
                pillActiveColor = NeonPurple,
                actionButtonColor = NeonCyan,
                textColor = TextWhite,
                textMutedColor = TextMuted,
                shadowColor = Color(0xFF000000),
                isGlass = true,
                isDark = true
            )
        )
        ThemeMode.MINIMAL -> Pair(
            MinimalScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                backgroundColor = Color(0xFFFFFFFF),
                cardBackground = Color(0xFFFAFAFA),
                borderColor = Color(0xFF18181B),
                accentColor = Color(0xFF18181B),
                pillActiveColor = Color(0xFF18181B),
                actionButtonColor = Color(0xFFF4F4F5),
                textColor = Color(0xFF18181B),
                textMutedColor = Color(0xFF71717A),
                shadowColor = Color(0x10000000),
                isGlass = false,
                isDark = false
            )
        )
        ThemeMode.GELAP -> Pair(
            GelapScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                backgroundColor = Color(0xFF0B0D11),
                cardBackground = Color(0xFF141720),
                borderColor = Color(0xFF2E3440),
                accentColor = Color(0xFFF43F5E),
                pillActiveColor = Color(0xFF6366F1),
                actionButtonColor = Color(0xFF22C55E),
                textColor = Color(0xFFF1F5F9),
                textMutedColor = Color(0xFF94A3B8),
                shadowColor = Color(0xFF000000),
                isGlass = false,
                isDark = true
            )
        )
        ThemeMode.SAMUDRA -> Pair(
            SamudraScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                backgroundColor = Color(0xFFF0F9FF),
                cardBackground = Color(0xFFFFFFFF),
                borderColor = Color(0xFF0369A1),
                accentColor = Color(0xFF0284C7),
                pillActiveColor = Color(0xFF0284C7),
                actionButtonColor = Color(0xFF38BDF8),
                textColor = Color(0xFF0C4A6E),
                textMutedColor = Color(0xFF64748B),
                shadowColor = Color(0x200369A1),
                isGlass = false,
                isDark = false
            )
        )
        ThemeMode.SAKURA -> Pair(
            SakuraScheme,
            CustomThemeConfig(
                themeMode = themeMode,
                backgroundColor = Color(0xFFFDF2F8),
                cardBackground = Color(0xFFFFFFFF),
                borderColor = Color(0xFFBE185D),
                accentColor = Color(0xFFEC4899),
                pillActiveColor = Color(0xFFEC4899),
                actionButtonColor = Color(0xFFF472B6),
                textColor = Color(0xFF831843),
                textMutedColor = Color(0xFF9D174D),
                shadowColor = Color(0x20EC4899),
                isGlass = false,
                isDark = false
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
