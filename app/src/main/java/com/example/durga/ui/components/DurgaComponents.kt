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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
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
    onOpenSettings: (() -> Unit)? = null,
    onOpenNotifications: (() -> Unit)? = null,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    unreadNotificationCount: Int = 0,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    var langMenuExpanded by remember { mutableStateOf(false) }
    val colors = LocalDurgaColors.current

    Surface(
        color = colors.surface,
        modifier = modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = colors.outline.copy(alpha = 0.6f)),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
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
                    // Refined Women Power Shield Logo Emblem (Clickable to open feature switcher)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        colors.primary.copy(alpha = 0.12f),
                                        colors.accentRose.copy(alpha = 0.12f)
                                    )
                                )
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        colors.primary,
                                        colors.accentRose
                                    )
                                ),
                                CircleShape
                            )
                            .clickable(enabled = onOpenFeatureSwitcher != null) {
                                onOpenFeatureSwitcher?.invoke()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_durga_logo),
                            contentDescription = "DURGA Emblem",
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
                                fontWeight = FontWeight.Black,
                                color = colors.textPrimary,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            ),
                            maxLines = 1
                        )
                    }
                    val defaultTagline = when (currentLang) {
                        DurgaLanguage.GUJARATI -> "તમારી સુરક્ષા, અમારી પ્રાથમિકતા"
                        DurgaLanguage.HINDI -> "आपकी सुरक्षा, हमारी प्राथमिकता"
                        else -> "Your Safety, Our Priority"
                    }
                    val displaySubtitle = subtitle ?: defaultTagline
                    Text(
                        text = displaySubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Language Switcher
                Box {
                    IconButton(
                        onClick = { langMenuExpanded = true },
                        modifier = Modifier
                            .size(40.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("language_switch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = langMenuExpanded,
                        onDismissRequest = { langMenuExpanded = false },
                        modifier = Modifier
                            .background(colors.surface)
                            .border(1.dp, colors.outline, RoundedCornerShape(10.dp))
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

                // Notifications Bell
                if (onOpenNotifications != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onOpenNotifications,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("top_bar_notifications_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Case Notifications",
                                tint = if (unreadNotificationCount > 0) colors.primary else colors.textPrimary,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                        if (unreadNotificationCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(16.dp)
                                    .background(Color(0xFFE11D48), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Accessibility Text Size Quick Toggle
                if (onToggleAccessibility != null) {
                    IconButton(
                        onClick = onToggleAccessibility,
                        modifier = Modifier
                            .size(40.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("accessibility_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextIncrease,
                            contentDescription = "Accessibility",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Quick Feature Switcher (Jump to any feature easily)
                if (onOpenFeatureSwitcher != null) {
                    IconButton(
                        onClick = onOpenFeatureSwitcher,
                        modifier = Modifier
                            .size(40.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("top_bar_feature_switcher_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Quick Feature Switcher",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Quick Exit Privacy Shield
                IconButton(
                    onClick = onQuickExit,
                    modifier = Modifier
                        .size(40.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("quick_exit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Quick Exit Screen",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
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
                                            DurgaThemeMode.PINK -> "4. Pink / રાણી ગુલાબી (Rani Pink)"
                                            DurgaThemeMode.ROSE_PINK -> "5. Rose Pink / સોફ્ટ કમળ પિંક (Rose Pink)"
                                            DurgaThemeMode.PURPLE -> "6. Purple / શાહી ગરિમા"
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
    val borderStrokeColor = borderColor ?: colors.outline.copy(alpha = 0.6f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderStrokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
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
            .border(width = 0.5.dp, color = colors.outline.copy(alpha = 0.5f)),
        containerColor = colors.surface,
        tonalElevation = 2.dp
    ) {
        val items = listOf(
            Triple(
                "home",
                when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "હોમ (Home)"
                    DurgaLanguage.HINDI -> "होम (Home)"
                    else -> "Home"
                },
                Icons.Outlined.Home
            ),
            Triple(
                "complaint",
                when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "ફરિયાદ (Complaint)"
                    DurgaLanguage.HINDI -> "शिकायत (Complaint)"
                    else -> "Complaints"
                },
                Icons.Filled.EditNote
            ),
            Triple(
                "vault",
                when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "સુરક્ષા / મદદ"
                    DurgaLanguage.HINDI -> "सुरक्षा / मदद"
                    else -> "Safety / Help"
                },
                Icons.Outlined.Lock
            ),
            Triple(
                "cases",
                when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "મારા કેસ"
                    DurgaLanguage.HINDI -> "मेरे केस"
                    else -> "Cases"
                },
                Icons.Outlined.FolderShared
            ),
            Triple(
                "settings",
                when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "સેટિંગ્સ"
                    DurgaLanguage.HINDI -> "सेटिंग्स"
                    else -> "Settings"
                },
                Icons.Outlined.Settings
            )
        )

        items.forEach { (route, label, icon) ->
            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = if (isSelected) {
                            Modifier
                                .background(colors.primaryContainer, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        } else {
                            Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) colors.primary else colors.textMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = if (isSelected) 11.sp else 10.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.primary,
                    indicatorColor = Color.Transparent,
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

data class QuickFeatureItem(
    val route: String,
    val titleGu: String,
    val titleHi: String,
    val titleEn: String,
    val descGu: String,
    val descHi: String,
    val descEn: String,
    val icon: ImageVector,
    val accentColor: Color,
    val isPrimary: Boolean = false
)

/**
 * Universal Quick Feature Switcher Dialog
 * Allows users to effortlessly jump between ANY feature of DURGA with 1 tap.
 */
@Composable
fun QuickFeatureSwitcherDialog(
    currentLanguage: DurgaLanguage,
    onDismiss: () -> Unit,
    onNavigateToFeature: (String) -> Unit
) {
    val colors = LocalDurgaColors.current

    val features = listOf(
        QuickFeatureItem(
            route = "home",
            titleGu = "હોમ ડેશબોર્ડ",
            titleHi = "होम डैशबोर्ड",
            titleEn = "Home Dashboard",
            descGu = "મુખ્ય સ્ક્રીન અને સુરક્ષા સ્થિતિ",
            descHi = "मुख्य स्क्रीन और सुरक्षा स्थिति",
            descEn = "Main safety center & active status",
            icon = Icons.Default.Home,
            accentColor = colors.primary,
            isPrimary = true
        ),
        QuickFeatureItem(
            route = "emergency",
            titleGu = "ઈમરજન્સી 112 અને SOS",
            titleHi = "इमरजेंसी 112 और SOS",
            titleEn = "Emergency 112 & SOS",
            descGu = "24/7 હેલ્પલાઇન અને તાત્કાલિક પોલીસ",
            descHi = "24/7 हेल्पलाइन और त्वरित पुलिस",
            descEn = "Verified official 24/7 police & dispatch",
            icon = Icons.Default.Shield,
            accentColor = DurgaEmergencyRed,
            isPrimary = true
        ),
        QuickFeatureItem(
            route = "complaint",
            titleGu = "નવી ફરિયાદ નોંધાવો",
            titleHi = "नई शिकायत दर्ज करें",
            titleEn = "Register Complaint",
            descGu = "AI સહાયથી સુરક્ષિત ફરિયાદ બનાવો",
            descHi = "AI सहायता से सुरक्षित शिकायत बनाएं",
            descEn = "AI-structured safe complaint drafting",
            icon = Icons.Default.EditNote,
            accentColor = Color(0xFF2563EB),
            isPrimary = true
        ),
        QuickFeatureItem(
            route = "cases",
            titleGu = "મારા કેસ & પોલીસ ટ્રેકિંગ",
            titleHi = "मेरे केस & पुलिस ट्रैकिंग",
            titleEn = "My Cases & Tracking",
            descGu = "લાઇવ પોલીસ કાર્યવાહી & % પ્રગતિ",
            descHi = "लाइव पुलिस कार्रवाई और % प्रगति",
            descEn = "Live investigation milestones & status",
            icon = Icons.Default.FolderShared,
            accentColor = Color(0xFF4F46E5),
            isPrimary = true
        ),
        QuickFeatureItem(
            route = "notifications",
            titleGu = "સૂચનાઓ & અપડેટ્સ",
            titleHi = "सूचनाएं और अपडेट्स",
            titleEn = "Notifications Center",
            descGu = "પોલીસ કાર્યવાહી અને નવા એપ ફીચર્સ",
            descHi = "पुलिस कार्रवाई और नए ऐप फीचर्स",
            descEn = "Real-time case alerts & new features",
            icon = Icons.Default.Notifications,
            accentColor = Color(0xFFD97706)
        ),
        QuickFeatureItem(
            route = "help_near_me",
            titleGu = "નજીકની મદદ (હોસ્પિટલ/પોલીસ)",
            titleHi = "नजदीकी मदद (अस्पताल/पुलिस)",
            titleEn = "Help Near Me",
            descGu = "સુરક્ષિત સ્થાનો અને સીધો કૉલ",
            descHi = "सुरक्षित स्थान और डायरेक्ट कॉल",
            descEn = "Nearby verified police desks & hospitals",
            icon = Icons.Default.NearMe,
            accentColor = Color(0xFF0D9488)
        ),
        QuickFeatureItem(
            route = "vault",
            titleGu = "ખાનગી પુરાવા વૉલ્ટ",
            titleHi = "प्राइवेट सबूत वॉल्ट",
            titleEn = "Private Evidence Vault",
            descGu = "PIN-સુરક્ષિત ડ્રાફ્ટ, ફોટો અને ઑડિયો",
            descHi = "PIN-सुरक्षित ड्राफ्ट, फोटो और ऑडियो",
            descEn = "Hardware encrypted evidence & drafts",
            icon = Icons.Default.Lock,
            accentColor = Color(0xFF7C3AED)
        ),
        QuickFeatureItem(
            route = "web_portals",
            titleGu = "સરકારી વેબ પોર્ટલ્સ",
            titleHi = "सरकारी वेब पोर्टल्स",
            titleEn = "Official Web Portals",
            descGu = "નેશનલ ક્રાઈમ અને સાયબર પોર્ટલ",
            descHi = "राष्ट्रीय अपराध और साइबर पोर्टल",
            descEn = "National cybercrime & helpline portals",
            icon = Icons.Default.Public,
            accentColor = Color(0xFF0284C7)
        ),
        QuickFeatureItem(
            route = "idk_what_to_do",
            titleGu = "શું કરવું તે ખબર નથી?",
            titleHi = "क्या करें समझ नहीं आ रहा?",
            titleEn = "Need Calm Guidance?",
            descGu = "તબક્કાવાર સરળ માર્ગદર્શન",
            descHi = "कदम-दर-कदम आसान मार्गदर्शन",
            descEn = "Step-by-step reassuring assistance",
            icon = Icons.Default.HelpOutline,
            accentColor = Color(0xFFEA580C)
        ),
        QuickFeatureItem(
            route = "child_friendly",
            titleGu = "બાળકો માટે સુરક્ષિત મોડ",
            titleHi = "बच्चों के लिए सुरक्षित मोड",
            titleEn = "Child Friendly Mode",
            descGu = "ચાઈલ્ડલાઈન 1098 & સરળ ચિત્રો",
            descHi = "चाइल्डलाइन 1098 और सरल चित्र",
            descEn = "Childline 1098 & intuitive icons",
            icon = Icons.Default.ChildCare,
            accentColor = Color(0xFFDB2777)
        ),
        QuickFeatureItem(
            route = "witness_report",
            titleGu = "સાક્ષી ગુપ્ત રિપોર્ટ",
            titleHi = "साक्षी गुप्त रिपोर्ट",
            titleEn = "Anonymous Witness Report",
            descGu = "ઓળખ છુપાવીને ઘટનાની માહિતી આપો",
            descHi = "पहचान छुपाकर घटना की जानकारी दें",
            descEn = "Zero identity bystander reporting",
            icon = Icons.Default.Visibility,
            accentColor = Color(0xFF0891B2)
        ),
        QuickFeatureItem(
            route = "privacy_center",
            titleGu = "ગોપનીયતા કેન્દ્ર",
            titleHi = "गोपनीयता केंद्र",
            titleEn = "Privacy Center",
            descGu = "ઝીરો-ટ્રેકિંગ & ડેટા સાફ કરો",
            descHi = "जीरो-ट्रैकिंग और डेटा डिलीट करें",
            descEn = "Zero tracking & 1-tap local data wipe",
            icon = Icons.Default.Security,
            accentColor = Color(0xFF16A34A)
        ),
        QuickFeatureItem(
            route = "settings",
            titleGu = "સેટિંગ્સ & રંગ થીમ",
            titleHi = "सेटिंग्स & रंग थीम",
            titleEn = "Settings & Color Themes",
            descGu = "ભાષા, 5 થીમ્સ, વૉલ્ટ PIN",
            descHi = "भाषा, 5 थीम्स, वॉल्ट पिन",
            descEn = "Language, 5 themes, vault PIN",
            icon = Icons.Default.Settings,
            accentColor = Color(0xFF475569)
        ),
        QuickFeatureItem(
            route = "authority_portal",
            titleGu = "પોલીસ / એડમિન પોર્ટલ",
            titleHi = "पुलिस / एडमिन पोर्टल",
            titleEn = "Authority Desk Portal",
            descGu = "અધિકારી લૉગિન અને ઑડિટ લૉગ્સ",
            descHi = "अधिकारी लॉगिन और ऑडिट लॉग्स",
            descEn = "Official login & investigation verification",
            icon = Icons.Default.AdminPanelSettings,
            accentColor = Color(0xFF1E293B)
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(colors.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "બધા ફીચર્સ - ઝડપી સ્વિચર"
                                    DurgaLanguage.HINDI -> "सभी फीचर्स - क्विक स्विचर"
                                    else -> "All Features - Quick Switcher"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "કોઈપણ ફીચર પર એક ટૅપમાં જાઓ"
                                    DurgaLanguage.HINDI -> "किसी भी फीचर पर एक टैप में जाएं"
                                    else -> "Instant 1-tap jump to any feature"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("feature_switcher_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))

                // Scrollable Feature Items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(features) { item ->
                        val itemTitle = when (currentLanguage) {
                            DurgaLanguage.GUJARATI -> item.titleGu
                            DurgaLanguage.HINDI -> item.titleHi
                            else -> item.titleEn
                        }
                        val itemDesc = when (currentLanguage) {
                            DurgaLanguage.GUJARATI -> item.descGu
                            DurgaLanguage.HINDI -> item.descHi
                            else -> item.descEn
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (item.isPrimary) colors.surfaceVariant.copy(alpha = 0.5f) else colors.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (item.isPrimary) item.accentColor.copy(alpha = 0.4f) else colors.outline.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    onNavigateToFeature(item.route)
                                }
                                .testTag("feature_item_${item.route}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(item.accentColor.copy(alpha = 0.14f), RoundedCornerShape(10.dp))
                                        .border(1.dp, item.accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = itemTitle,
                                        tint = item.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = itemTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Text(
                                        text = itemDesc,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        ),
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = item.accentColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
