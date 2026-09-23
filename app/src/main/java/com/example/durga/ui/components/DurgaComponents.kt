package com.example.durga.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaEmergencyRedLight
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaThemeMode
import com.example.ui.theme.LocalDurgaColors
import com.example.ui.theme.LocalDurgaThemeMode

@Composable
fun DurgaTopBar(
    title: String,
    currentLang: DurgaLanguage,
    onBack: (() -> Unit)? = null,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onToggleAccessibility: (() -> Unit)? = null,
    onOpenThemePicker: (() -> Unit)? = null,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    var langMenuExpanded by remember { mutableStateOf(false) }
    val colors = LocalDurgaColors.current

    Surface(
        color = colors.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = colors.outline),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colors.textPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                } else {
                    // Women Power Logo Emblem
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer)
                            .border(1.dp, colors.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_durga_logo),
                            contentDescription = "Women Power Logo",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = 17.sp
                            ),
                            maxLines = 1
                        )
                    }
                    val displaySubtitle = subtitle ?: DurgaStrings.get("safety_companion", currentLang)
                    Text(
                        text = displaySubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Theme Picker Icon Button
                if (onOpenThemePicker != null) {
                    IconButton(
                        onClick = onOpenThemePicker,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("theme_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Change Theme",
                            tint = colors.primary
                        )
                    }
                }

                // Language Switcher
                Box {
                    IconButton(
                        onClick = { langMenuExpanded = true },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("language_switch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = colors.textPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = langMenuExpanded,
                        onDismissRequest = { langMenuExpanded = false },
                        modifier = Modifier
                            .background(colors.surface)
                            .border(1.dp, colors.outline, RoundedCornerShape(8.dp))
                    ) {
                        DurgaLanguage.entries.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${lang.nativeName} (${lang.englishName})",
                                            color = if (lang == currentLang) colors.primary else colors.textSecondary,
                                            fontWeight = if (lang == currentLang) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                        if (lang == currentLang) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = colors.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onLanguageChange(lang)
                                    langMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Optional Accessibility Quick Toggle
                if (onToggleAccessibility != null) {
                    IconButton(
                        onClick = onToggleAccessibility,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("accessibility_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextIncrease,
                            contentDescription = "Accessibility",
                            tint = colors.textPrimary
                        )
                    }
                }

                // Quick Exit Privacy Shield
                IconButton(
                    onClick = onQuickExit,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("quick_exit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Quick Exit Screen",
                        tint = colors.textSecondary
                    )
                }
            }
        }
    }
}

/**
 * 5-Color Theme Selection Dialog
 * 1. GREEN (Emerald Nature & Safety)
 * 2. BLACK (AMOLED Midnight Dark Mode)
 * 3. BLUE (Guardian Royal Blue)
 * 4. PINK (Women Power / Stree Shakti Magenta Pink)
 * 5. PURPLE (Royal Dignity Violet)
 */
@Composable
fun ThemeSelectionDialog(
    currentTheme: DurgaThemeMode,
    currentLang: DurgaLanguage,
    onSelectTheme: (DurgaThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalDurgaColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = DurgaStrings.get("theme_5_colors", currentLang),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "1: Green • 2: Black • 3: Blue • 4: Pink • 5: Purple",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DurgaThemeMode.entries.forEach { mode ->
                    val isSelected = mode == currentTheme
                    val localizedTitle = when (currentLang) {
                        DurgaLanguage.GUJARATI -> mode.titleGu
                        DurgaLanguage.HINDI -> mode.titleHi
                        else -> mode.titleEn
                    }

                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) colors.primaryContainer else colors.surfaceVariant
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) colors.primary else colors.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectTheme(mode)
                                onDismiss()
                            }
                            .testTag("theme_option_${mode.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Double color swatch circle
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(mode.primaryColor, CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(mode.accentColor, CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = localizedTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = colors.textPrimary,
                                            fontSize = 14.sp
                                        )
                                    )
                                    Text(
                                        text = when (mode) {
                                            DurgaThemeMode.GREEN -> "1. Green / પ્રકૃતિ અને શાંતિ"
                                            DurgaThemeMode.BLACK -> "2. Black / ડાર્ક નાઈટ મોડ"
                                            DurgaThemeMode.BLUE -> "3. Blue / રક્ષક બ્લુ (Default)"
                                            DurgaThemeMode.PINK -> "4. Pink / નારી શક્તિ (Women Power)"
                                            DurgaThemeMode.PURPLE -> "5. Purple / શાહી ગરિમા"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_theme_dialog")
            ) {
                Text(
                    text = DurgaStrings.get("continue_btn", currentLang),
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
fun DurgaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalDurgaColors.current
    val containerBg = backgroundColor ?: colors.surface
    val borderStrokeColor = borderColor ?: colors.outline

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderStrokeColor),
        modifier = if (onClick != null) {
            modifier.clickable { onClick() }
        } else {
            modifier
        }
    ) {
        content()
    }
}

@Composable
fun DurgaQuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    testTag: String = "quick_action"
) {
    val colors = LocalDurgaColors.current

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(colors.primaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (badgeText != null) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier
                            .background(colors.primaryContainer, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun EmergencyContactRow(
    title: String,
    subtitle: String,
    number: String,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    val colors = LocalDurgaColors.current

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(DurgaEmergencyRedLight, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = DurgaEmergencyRed,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    if (badge != null) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .background(colors.primaryContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Button(
                onClick = onCallClick,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("call_button_$number")
            ) {
                Text(
                    text = "CALL",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

@Composable
fun EmergencyCallCard(
    number: String,
    title: String,
    subtitle: String,
    onCallClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null
) {
    EmergencyContactRow(
        title = title,
        subtitle = subtitle,
        number = number,
        onCallClick = onCallClick,
        modifier = modifier,
        badge = badge
    )
}

/**
 * Bottom Navigation with dedicated "Complaint" tab
 */
@Composable
fun DurgaBottomNav(
    currentRoute: String,
    currentLanguage: DurgaLanguage,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = colors.outline),
        containerColor = colors.surface,
        tonalElevation = 2.dp
    ) {
        val items = listOf(
            Triple("home", DurgaStrings.get("nav_home", currentLanguage), Icons.Outlined.Home),
            Triple("complaint", DurgaStrings.get("file_complaint_now", currentLanguage), Icons.Filled.EditNote),
            Triple("cases", DurgaStrings.get("nav_cases", currentLanguage), Icons.Outlined.FolderShared),
            Triple("help", DurgaStrings.get("nav_help", currentLanguage), Icons.Outlined.HelpOutline),
            Triple("theme", DurgaStrings.get("theme_selection", currentLanguage), Icons.Outlined.Palette)
        )

        items.forEach { (route, label, icon) ->
            val isSelected = currentRoute == route
            val isComplaint = route == "complaint"

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = if (isComplaint) {
                            Modifier
                                .background(colors.primaryContainer, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        } else {
                            Modifier
                        }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected || isComplaint) colors.primary else colors.textMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected || isComplaint) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.primary,
                    indicatorColor = colors.primaryContainer,
                    unselectedIconColor = colors.textMuted,
                    unselectedTextColor = colors.textMuted
                )
            )
        }
    }
}

@Composable
fun QuickExitOverlay(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Privacy Shield",
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "This feature helps protect your privacy. A neutral screen is displayed.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Personal Grocery List",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("• Whole wheat flour (5 kg)", color = colors.textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Milk, ghee and curd", color = colors.textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Green vegetables and lemons", color = colors.textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Laundry detergent and sponges", color = colors.textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Schedule doctor consultation", color = colors.textSecondary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
            ) {
                Text("Return to DURGA Safe Session", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun AuthorityWatermark(
    officerId: String,
    roleTitle: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .rotate(-20f)
                .background(Color(0x0C172033), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0x1A172033), RoundedCornerShape(8.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "OFFICIAL RECORD • CONFIDENTIAL",
                color = colors.primary.copy(alpha = 0.4f),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 13.sp
            )
            Text(
                text = "$roleTitle | Officer: $officerId",
                color = colors.textSecondary.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
