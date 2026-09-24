package com.example.durga.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import com.example.durga.model.IncidentCategory
import com.example.durga.ui.components.ThemeSelectionDialog
import com.example.ui.theme.DurgaThemeMode
import com.example.ui.theme.LocalDurgaColors
import com.example.ui.theme.LocalDurgaThemeMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.ui.components.DurgaBottomNav
import com.example.durga.ui.components.DurgaCard
import com.example.durga.ui.components.DurgaTopBar
import com.example.durga.ui.components.EmergencyCallCard
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBlueLightBg
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaBorderSubtle
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaEmergencyRedDark
import com.example.ui.theme.DurgaEmergencyRedLight
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSectionSoft
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSuccessLight
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextLightMuted
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted
import com.example.ui.theme.DurgaWarning
import com.example.ui.theme.DurgaWarningLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class QuickActionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val routeAction: () -> Unit
)

@Composable
fun HomeScreen(
    currentLanguage: DurgaLanguage,
    onNavigateToEmergency: () -> Unit,
    onNavigateToComplaintCategories: () -> Unit,
    onNavigateToWitness: () -> Unit,
    onNavigateToHelpNearMe: () -> Unit,
    onNavigateToIdkWhatToDo: () -> Unit,
    onNavigateToPrivateData: () -> Unit,
    onNavigateToMyCases: () -> Unit,
    onNavigateToChildFriendly: () -> Unit,
    onNavigateToPrivacyCenter: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAuthorityPortal: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onThemeChange: (DurgaThemeMode) -> Unit = {},
    onStartCategoryComplaint: ((IncidentCategory) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAccessibilityLargeText by remember { mutableStateOf(false) }
    var showSosActiveDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var sosHoldProgress by remember { mutableFloatStateOf(0f) }
    var isHoldingSos by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val colors = LocalDurgaColors.current
    val currentTheme = LocalDurgaThemeMode.current

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = currentTheme,
            currentLang = currentLanguage,
            onSelectTheme = { selected ->
                onThemeChange(selected)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // 3-second hold detection for SOS
    LaunchedEffect(isHoldingSos) {
        if (isHoldingSos) {
            val startTime = System.currentTimeMillis()
            while (isHoldingSos && sosHoldProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                sosHoldProgress = (elapsed / 3000f).coerceIn(0f, 1f)
                if (sosHoldProgress >= 1f) {
                    showSosActiveDialog = true
                    isHoldingSos = false
                    sosHoldProgress = 0f
                    break
                }
                delay(30)
            }
        } else {
            sosHoldProgress = 0f
        }
    }

    // Direct phone dialer helper
    fun dialNumber(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "DURGA",
                currentLang = currentLanguage,
                onBack = null,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onToggleAccessibility = { isAccessibilityLargeText = !isAccessibilityLargeText }
            )
        },
        bottomBar = {
            DurgaBottomNav(
                currentRoute = "home",
                currentLanguage = currentLanguage,
                onNavigate = { route ->
                    when (route) {
                        "home" -> { /* Already on Home */ }
                        "complaint" -> onNavigateToComplaintCategories()
                        "vault" -> onNavigateToPrivateData()
                        "cases" -> onNavigateToMyCases()
                        "help" -> onNavigateToHelpNearMe()
                        "theme" -> { showThemeDialog = true }
                        "settings" -> onNavigateToSettings()
                    }
                }
            )
        },
        containerColor = colors.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Simple Greeting & Preparedness Status
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = DurgaStrings.get("greeting_headline", currentLanguage),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = if (isAccessibilityLargeText) 22.sp else 19.sp
                        )
                    )
                    Text(
                        text = DurgaStrings.get("greeting_sub", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = if (isAccessibilityLargeText) 15.sp else 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Safety Status Summary Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Safe",
                                    tint = DurgaSuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = DurgaStrings.get("status_emergency_ready", currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextNavy,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(DurgaSuccessGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = DurgaStrings.get("status_offline_ready", currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // HERO INCIDENT & COMPLAINT REGISTRATION SECTION (Prominent Official Complaint Feature)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, colors.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_complaint_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(colors.primaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = "File Complaint",
                                        tint = colors.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = DurgaStrings.get("file_complaint_now", currentLanguage),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Text(
                                        text = DurgaStrings.get("complaint_feature_headline", currentLanguage),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = DurgaStrings.get("private_badge", currentLanguage),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DurgaSuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier
                                    .background(DurgaSuccessLight, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = DurgaStrings.get("complaint_feature_desc", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.textSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Start Complaint Button
                        Button(
                            onClick = onNavigateToComplaintCategories,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("start_complaint_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DurgaStrings.get("start_complaint_btn", currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick incident category pills
                        Text(
                            text = DurgaStrings.get("direct_categories", currentLanguage),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val pillModifier = Modifier.weight(1f)
                            CategoryQuickChip(
                                label = DurgaStrings.get("category_cyber", currentLanguage),
                                icon = Icons.Default.PhoneAndroid,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.CYBER_ONLINE)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = pillModifier
                            )
                            CategoryQuickChip(
                                label = DurgaStrings.get("category_domestic", currentLanguage),
                                icon = Icons.Default.Home,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.HOME_DOMESTIC)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = pillModifier
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val pillModifier = Modifier.weight(1f)
                            CategoryQuickChip(
                                label = DurgaStrings.get("category_workplace", currentLanguage),
                                icon = Icons.Default.Work,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.WORKPLACE)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = pillModifier
                            )
                            CategoryQuickChip(
                                label = DurgaStrings.get("category_public", currentLanguage),
                                icon = Icons.Default.LocationCity,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.PUBLIC_CITY)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = pillModifier
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secondary action: Track My Cases
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToMyCases() }
                                .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                .border(1.dp, colors.outline, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FolderShared,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DurgaStrings.get("track_existing_cases", currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = colors.textPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 2. PRIMARY EMERGENCY SECTION - Large Prominent SOS Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DurgaEmergencyRed.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = DurgaStrings.get("sos_card_title", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaEmergencyRedDark,
                                        fontSize = if (isAccessibilityLargeText) 20.sp else 17.sp
                                    )
                                )
                                Text(
                                    text = "National Emergency 112 & Location Alert",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(DurgaEmergencyRedLight, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = DurgaStrings.get("sos_active_badge", currentLanguage),
                                    color = DurgaEmergencyRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Large Circular SOS Button (Clean, Non-gaming, Professional)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(136.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isHoldingSos = true
                                            tryAwaitRelease()
                                            isHoldingSos = false
                                        },
                                        onTap = {
                                            // Fallback single tap opens emergency screen directly
                                            onNavigateToEmergency()
                                        }
                                    )
                                }
                                .testTag("sos_button")
                        ) {
                            // Circular Hold Progress Indicator
                            if (sosHoldProgress > 0f) {
                                CircularProgressIndicator(
                                    progress = { sosHoldProgress },
                                    modifier = Modifier.size(136.dp),
                                    color = DurgaEmergencyRedDark,
                                    strokeWidth = 6.dp,
                                    trackColor = DurgaEmergencyRedLight
                                )
                            }

                            // Main SOS Circle
                            Box(
                                modifier = Modifier
                                    .size(118.dp)
                                    .clip(CircleShape)
                                    .background(DurgaEmergencyRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "SOS",
                                        tint = Color.White,
                                        modifier = Modifier.size(34.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = DurgaStrings.get("sos_button_label", currentLanguage),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 24.sp,
                                            letterSpacing = 2.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = DurgaStrings.get("sos_instruction", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DurgaEmergencyRed,
                                fontSize = if (isAccessibilityLargeText) 15.sp else 13.sp
                            )
                        )
                        Text(
                            text = DurgaStrings.get("tap_once_directory", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextLightMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // 3. QUICK EMERGENCY CONTACTS (Police, Ambulance, Women Helpline, Cybercrime)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = DurgaStrings.get("emergency_contacts_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = if (isAccessibilityLargeText) 18.sp else 16.sp
                            )
                        )
                        Text(
                            text = DurgaStrings.get("view_all_contacts", currentLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = DurgaBlueAction,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier
                                .clickable(onClick = onNavigateToEmergency)
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmergencyCallCard(
                            number = "112",
                            title = DurgaStrings.get("police_label", currentLanguage),
                            subtitle = "National Emergency & Police Patrol",
                            onCallClick = { dialNumber("112") }
                        )

                        EmergencyCallCard(
                            number = "108",
                            title = DurgaStrings.get("ambulance_label", currentLanguage),
                            subtitle = "Free Ambulance & Immediate First Aid",
                            onCallClick = { dialNumber("108") }
                        )

                        EmergencyCallCard(
                            number = "1091",
                            title = DurgaStrings.get("women_helpline_label", currentLanguage),
                            subtitle = "Women in Distress & Abhayam 181",
                            onCallClick = { dialNumber("1091") }
                        )

                        EmergencyCallCard(
                            number = "1930",
                            title = "Cyber Helpline (1930)",
                            subtitle = "National Cybercrime & Digital Fraud Reporting",
                            onCallClick = { dialNumber("1930") }
                        )
                    }
                }
            }

            // 4. "I NEED HELP" GUIDED SAFETY ASSISTANT CARD
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBlueAction.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToIdkWhatToDo)
                        .testTag("i_need_help_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(DurgaBlueLightBg, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Help",
                                    tint = DurgaBlueAction,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = DurgaStrings.get("i_need_help_title", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy,
                                        fontSize = if (isAccessibilityLargeText) 17.sp else 15.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = DurgaStrings.get("i_need_help_sub", currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Proceed",
                            tint = DurgaBlueAction,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 5. QUICK ACTION GRID - 6 Clean Functional Cards (2-column layout)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = DurgaStrings.get("quick_tools_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = if (isAccessibilityLargeText) 18.sp else 16.sp
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val quickActions = listOf(
                        QuickActionItem(
                            id = "share_loc",
                            title = DurgaStrings.get("qa_share_location", currentLanguage),
                            subtitle = DurgaStrings.get("qa_share_location_sub", currentLanguage),
                            icon = Icons.Default.GpsFixed,
                            routeAction = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "URGENT SAFETY: My live location alert via DURGA app. Lat: 23.0225, Lng: 72.5714. Please check on me.")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Location"))
                            }
                        ),
                        QuickActionItem(
                            id = "nearby_help",
                            title = DurgaStrings.get("qa_nearby_help", currentLanguage),
                            subtitle = DurgaStrings.get("qa_nearby_help_sub", currentLanguage),
                            icon = Icons.Default.NearMe,
                            routeAction = onNavigateToHelpNearMe
                        ),
                        QuickActionItem(
                            id = "safety_checklist",
                            title = DurgaStrings.get("qa_safety_checklist", currentLanguage),
                            subtitle = DurgaStrings.get("qa_safety_checklist_sub", currentLanguage),
                            icon = Icons.Default.Checklist,
                            routeAction = onNavigateToHelpNearMe
                        ),
                        QuickActionItem(
                            id = "evidence_vault",
                            title = DurgaStrings.get("qa_evidence_vault", currentLanguage),
                            subtitle = DurgaStrings.get("qa_evidence_vault_sub", currentLanguage),
                            icon = Icons.Default.Lock,
                            routeAction = onNavigateToPrivateData
                        ),
                        QuickActionItem(
                            id = "evidence_lock",
                            title = DurgaStrings.get("qa_evidence_lock", currentLanguage),
                            subtitle = DurgaStrings.get("qa_evidence_lock_sub", currentLanguage),
                            icon = Icons.Default.Security,
                            routeAction = onNavigateToPrivateData
                        ),
                        QuickActionItem(
                            id = "complaint_draft",
                            title = DurgaStrings.get("qa_complaint_draft", currentLanguage),
                            subtitle = DurgaStrings.get("qa_complaint_draft_sub", currentLanguage),
                            icon = Icons.Default.EditNote,
                            routeAction = onNavigateToComplaintCategories
                        )
                    )

                    // 2-column layout with clean card items
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (i in quickActions.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val item1 = quickActions[i]
                                QuickActionCard(
                                    item = item1,
                                    isLarge = isAccessibilityLargeText,
                                    modifier = Modifier.weight(1f)
                                )

                                if (i + 1 < quickActions.size) {
                                    val item2 = quickActions[i + 1]
                                    QuickActionCard(
                                        item = item2,
                                        isLarge = isAccessibilityLargeText,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // 6. Child Friendly Mode & Witness Incident
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onNavigateToChildFriendly)
                            .testTag("child_friendly_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DurgaWarningLight, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChildCare,
                                    contentDescription = "Child",
                                    tint = DurgaWarning,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = DurgaStrings.get("child_friendly_mode", currentLanguage),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Childline 1098",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onNavigateToWitness)
                            .testTag("witness_report_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DurgaSuccessLight, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Witness",
                                    tint = DurgaSuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Witness Report",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Anonymous bystander",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 7. Important Information & Official Help
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Official",
                                    tint = DurgaPrimaryNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Important Information",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy,
                                        fontSize = 15.sp
                                    )
                                )
                            }

                            Text(
                                text = "GOVT SERVICES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DurgaSuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier
                                    .background(DurgaSuccessLight, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OfficialInfoRow(
                            label = "One-Stop Centers (Sakhi)",
                            desc = "Integrated medical, legal, psychological and shelter support for women.",
                            number = "181"
                        ) { dialNumber("181") }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = DurgaBorderSubtle
                        )

                        OfficialInfoRow(
                            label = "National Legal Services (NALSA)",
                            desc = "Free legal aid & representation for women and minors in distress.",
                            number = "15100"
                        ) { dialNumber("15100") }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = DurgaBorderSubtle
                        )

                        OfficialInfoRow(
                            label = "Railway Child & Women Safety",
                            desc = "RPF 24x7 security helpline across all trains and stations.",
                            number = "139"
                        ) { dialNumber("139") }
                    }
                }
            }

            // 8. Official Authority / Auditor Login Link
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToAuthorityPortal)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Authority Portal",
                        tint = DurgaTextLightMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Authorized Official & Auditor Portal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Confirmation Dialog when SOS is triggered
    if (showSosActiveDialog) {
        Dialog(onDismissRequest = { showSosActiveDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(2.dp, DurgaEmergencyRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(DurgaEmergencyRedLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = DurgaEmergencyRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = DurgaStrings.get("sos_active_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaEmergencyRed,
                            fontSize = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = DurgaStrings.get("sos_active_desc", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 13.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Checklist
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DurgaSectionSoft, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DurgaSuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live GPS Coordinates Acquired",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DurgaSuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency Dispatch Connected (112)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showSosActiveDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaBorderLight),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                        ) {
                            Text(
                                text = DurgaStrings.get("cancel_alert", currentLanguage),
                                color = DurgaTextNavy,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                showSosActiveDialog = false
                                dialNumber("112")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                        ) {
                            Text(
                                text = "Call 112",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    item: QuickActionItem,
    isLarge: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .clickable(onClick = item.routeAction)
            .testTag("qa_card_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(DurgaSectionSoft, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = DurgaPrimaryNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DurgaTextLightMuted,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DurgaTextNavy,
                    fontSize = if (isLarge) 15.sp else 13.sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DurgaTextSecondaryMuted,
                    fontSize = 11.sp
                ),
                maxLines = 2
            )
        }
    }
}

@Composable
private fun OfficialInfoRow(
    label: String,
    desc: String,
    number: String,
    onCall: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DurgaTextNavy,
                    fontSize = 13.sp
                )
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DurgaTextSecondaryMuted,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            onClick = onCall,
            colors = ButtonDefaults.buttonColors(containerColor = DurgaSectionSoft),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call $number",
                tint = DurgaPrimaryNavy,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = DurgaPrimaryNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun CategoryQuickChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

