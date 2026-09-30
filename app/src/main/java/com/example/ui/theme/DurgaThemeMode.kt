package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Distinct theme options requested by the user:
 * 1. GREEN (Emerald Nature & Safety)
 * 2. BLACK (AMOLED Midnight Dark Mode)
 * 3. BLUE (Guardian Royal Blue)
 * 4. PINK (Deep Rani Pink / Women Power)
 * 5. ROSE_PINK (Soft Blossom & Lotus Rose Pink)
 * 6. PURPLE (Royal Dignity Violet)
 */
enum class DurgaThemeMode(
    val id: String,
    val titleEn: String,
    val titleGu: String,
    val titleHi: String,
    val feelingEn: String,
    val primaryColor: Color,
    val accentColor: Color,
    val haloColor: Color,
    val isDark: Boolean = false
) {
    BLUE(
        id = "blue",
        titleEn = "Calm Blue",
        titleGu = "શાંત બ્લુ (Calm)",
        titleHi = "शांत ब्लू (Calm)",
        feelingEn = "Calm • Trustworthy • Peaceful",
        primaryColor = Color(0xFF1D4ED8), // Royal Blue
        accentColor = Color(0xFF3B82F6),
        haloColor = Color(0xFF60A5FA),
        isDark = false
    ),
    BLACK(
        id = "black",
        titleEn = "Strength Black",
        titleGu = "સુરક્ષિત બ્લેક (Strength)",
        titleHi = "मजबूत ब्लैक (Strength)",
        feelingEn = "Strength • Privacy • Focused",
        primaryColor = Color(0xFF60A5FA), // Crisp high-contrast highlight on dark
        accentColor = Color(0xFFA1A1AA),
        haloColor = Color(0xFF38BDF8),
        isDark = true
    ),
    GREEN(
        id = "green",
        titleEn = "Safety Green",
        titleGu = "સુરક્ષા ગ્રીન (Safety)",
        titleHi = "सुरक्षा ग्रीन (Safety)",
        feelingEn = "Safety • Balance • Reassurance",
        primaryColor = Color(0xFF15803D), // Emerald Green
        accentColor = Color(0xFF22C55E),
        haloColor = Color(0xFF86EFAC),
        isDark = false
    ),
    PINK(
        id = "pink",
        titleEn = "Courage Pink",
        titleGu = "હિંમત પિંક (Courage)",
        titleHi = "साहस पिंक (Courage)",
        feelingEn = "Courage • Warmth • Women Power",
        primaryColor = Color(0xFFD81B60), // Vibrant Women Empowerment Magenta Pink
        accentColor = Color(0xFFEC4899),
        haloColor = Color(0xFFF9A8D4),
        isDark = false
    ),
    ROSE_PINK(
        id = "rose_pink",
        titleEn = "Rose Pink (Soft Blossom)",
        titleGu = "રોઝ પિંક (કમળ સોફ્ટ ગુલાબી)",
        titleHi = "रोज़ पिंक (कोमल कमल गुलाबी)",
        feelingEn = "Soft Blossom & Gentle Support",
        primaryColor = Color(0xFFF43F5E), // Vibrant Rose Blossom Pink
        accentColor = Color(0xFFFDA4AF),
        haloColor = Color(0xFFFECDD3),
        isDark = false
    ),
    PURPLE(
        id = "purple",
        titleEn = "Purple (Royal Dignity)",
        titleGu = "પર્પલ (શાહી શક્તિ)",
        titleHi = "पर्पल (રॉयल गरिमा)",
        feelingEn = "Royal Dignity & Protection",
        primaryColor = Color(0xFF7C3AED), // Royal Violet
        accentColor = Color(0xFF8B5CF6),
        haloColor = Color(0xFFC4B5FD),
        isDark = false
    )
}

data class DurgaCustomColors(
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val outline: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean,
    val haloColor: Color = Color(0xFF60A5FA),
    val accentRose: Color = Color(0xFFBE185D),
    val navyDeep: Color = Color(0xFF0F172A),
    val lavenderLight: Color = Color(0xFFFDF2F8)
)

val LocalDurgaColors = staticCompositionLocalOf {
    getDurgaCustomColors(DurgaThemeMode.BLUE)
}

val LocalDurgaThemeMode = compositionLocalOf {
    DurgaThemeMode.BLUE
}

fun getDurgaCustomColors(mode: DurgaThemeMode): DurgaCustomColors {
    return when (mode) {
        DurgaThemeMode.GREEN -> DurgaCustomColors(
            primary = Color(0xFF15803D),
            primaryContainer = Color(0xFFDCFCE7),
            secondary = Color(0xFF16A34A),
            background = Color(0xFFF7FAF7),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFEFF8F0),
            outline = Color(0xFFD1E7D3),
            textPrimary = Color(0xFF14532D),
            textSecondary = Color(0xFF4B6351),
            textMuted = Color(0xFF869E8C),
            isDark = false,
            haloColor = mode.haloColor
        )
        DurgaThemeMode.BLACK -> DurgaCustomColors(
            primary = Color(0xFF60A5FA),
            primaryContainer = Color(0xFF27272A),
            secondary = Color(0xFF94A3B8),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            surfaceVariant = Color(0xFF27272A),
            outline = Color(0xFF3F3F46),
            textPrimary = Color(0xFFF4F4F5),
            textSecondary = Color(0xFFA1A1AA),
            textMuted = Color(0xFF71717A),
            isDark = true,
            haloColor = mode.haloColor
        )
        DurgaThemeMode.BLUE -> DurgaCustomColors(
            primary = Color(0xFF1D4ED8),
            primaryContainer = Color(0xFFEFF6FF),
            secondary = Color(0xFF2563EB),
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            outline = Color(0xFFE2E8F0),
            textPrimary = Color(0xFF172033),
            textSecondary = Color(0xFF64748B),
            textMuted = Color(0xFF94A3B8),
            isDark = false,
            haloColor = mode.haloColor
        )
        DurgaThemeMode.PINK -> DurgaCustomColors(
            primary = Color(0xFFD81B60),
            primaryContainer = Color(0xFFFCE7F3),
            secondary = Color(0xFFE91E63),
            background = Color(0xFFFFF5F8),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFFDF2F8),
            outline = Color(0xFFFBCFE8),
            textPrimary = Color(0xFF4A0E2E),
            textSecondary = Color(0xFF833857),
            textMuted = Color(0xFFA8637F),
            isDark = false,
            haloColor = mode.haloColor
        )
        DurgaThemeMode.ROSE_PINK -> DurgaCustomColors(
            primary = Color(0xFFE11D48),
            primaryContainer = Color(0xFFFFE4E6),
            secondary = Color(0xFFF43F5E),
            background = Color(0xFFFFF1F2),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFFFF0F3),
            outline = Color(0xFFFECDD3),
            textPrimary = Color(0xFF881337),
            textSecondary = Color(0xFF9F1239),
            textMuted = Color(0xFFBE123C),
            isDark = false,
            haloColor = mode.haloColor
        )
        DurgaThemeMode.PURPLE -> DurgaCustomColors(
            primary = Color(0xFF7C3AED),
            primaryContainer = Color(0xFFF3E8FF),
            secondary = Color(0xFF8B5CF6),
            background = Color(0xFFFAF7FF),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF5F3FF),
            outline = Color(0xFFE9D5FF),
            textPrimary = Color(0xFF2E1065),
            textSecondary = Color(0xFF6D28D9),
            textMuted = Color(0xFF9F75E8),
            isDark = false,
            haloColor = mode.haloColor
        )
    }
}

fun getDurgaColorScheme(mode: DurgaThemeMode): ColorScheme {
    val custom = getDurgaCustomColors(mode)
    return if (mode.isDark) {
        darkColorScheme(
            primary = custom.primary,
            onPrimary = Color.Black,
            primaryContainer = custom.primaryContainer,
            onPrimaryContainer = Color.White,
            secondary = custom.secondary,
            onSecondary = Color.Black,
            background = custom.background,
            onBackground = custom.textPrimary,
            surface = custom.surface,
            onSurface = custom.textPrimary,
            surfaceVariant = custom.surfaceVariant,
            onSurfaceVariant = custom.textSecondary,
            outline = custom.outline,
            error = Color(0xFFF87171),
            onError = Color.Black
        )
    } else {
        lightColorScheme(
            primary = custom.primary,
            onPrimary = Color.White,
            primaryContainer = custom.primaryContainer,
            onPrimaryContainer = custom.textPrimary,
            secondary = custom.secondary,
            onSecondary = Color.White,
            background = custom.background,
            onBackground = custom.textPrimary,
            surface = custom.surface,
            onSurface = custom.textPrimary,
            surfaceVariant = custom.surfaceVariant,
            onSurfaceVariant = custom.textSecondary,
            outline = custom.outline,
            error = Color(0xFFD92D20),
            onError = Color.White
        )
    }
}
