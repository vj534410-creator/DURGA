package com.example.durga.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.durga.model.CaseNotificationEntity
import com.example.durga.model.IncidentCategory
import com.example.durga.ui.components.DurgaBottomNav
import com.example.durga.ui.components.DurgaTopBar
import com.example.durga.ui.components.EmergencyCallCard
import com.example.durga.ui.components.ThemeSelectionDialog
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
import com.example.ui.theme.DurgaThemeMode
import com.example.ui.theme.DurgaWarning
import com.example.ui.theme.DurgaWarningLight
import com.example.ui.theme.LocalDurgaColors
import com.example.ui.theme.LocalDurgaThemeMode
import kotlinx.coroutines.delay

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
    onNavigateToWebPortals: () -> Unit = onNavigateToAuthorityPortal,
    onNavigateToNotifications: () -> Unit = {},
    unreadNotificationCount: Int = 0,
    latestNotification: CaseNotificationEntity? = null,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onThemeChange: (DurgaThemeMode) -> Unit = {},
    onStartCategoryComplaint: ((IncidentCategory) -> Unit)? = null,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAccessibilityLargeText by remember { mutableStateOf(false) }
    var showSosActiveDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var sosHoldProgress by remember { mutableFloatStateOf(0f) }
    var isHoldingSos by remember { mutableStateOf(false) }
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

    // Subtle gentle pulse animation for the SOS emergency action (calm, reassuring, not frantic)
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "DURGA",
                currentLang = currentLanguage,
                onBack = null,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenNotifications = onNavigateToNotifications,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher,
                unreadNotificationCount = unreadNotificationCount,
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

            // ==============================================================
            // 1. PERSONAL SAFETY STATUS HEADER (Calm, Reassuring, Empowering)
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                colors.primary.copy(alpha = 0.3f),
                                colors.accentRose.copy(alpha = 0.2f),
                                colors.outline.copy(alpha = 0.5f)
                            )
                        )
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "તમારી સુરક્ષા, તમારી શક્તિ."
                                        DurgaLanguage.HINDI -> "आपकी सुरक्षा, आपकी शक्ति।"
                                        else -> "Your Safety, Your Strength."
                                    },
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = if (isAccessibilityLargeText) 22.sp else 19.sp,
                                        letterSpacing = (-0.2).sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(DurgaSuccessGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(7.dp))
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "સુરક્ષા સ્થિતિ સક્રિય (24x7 રક્ષા કવચ)"
                                            DurgaLanguage.HINDI -> "सुरक्षा स्थिति सक्रिय (24x7 रक्षा कवच)"
                                            else -> "Safety Status Active (24x7 Guardian Shield)"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DurgaSuccessGreen,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            // Protective shield icon
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(colors.primaryContainer.copy(alpha = 0.8f))
                                    .border(1.dp, colors.primary.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = colors.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = colors.outline.copy(alpha = 0.4f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Reassuring status pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadgePill(
                                label = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "ઓફલાઇન તૈયાર"
                                    DurgaLanguage.HINDI -> "ऑफ़लाइन तैयार"
                                    else -> "Offline Ready"
                                },
                                icon = Icons.Default.CheckCircle,
                                tint = DurgaSuccessGreen,
                                bg = DurgaSuccessLight,
                                modifier = Modifier.weight(1f)
                            )
                            StatusBadgePill(
                                label = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "GPS લોકેશન સક્રિય"
                                    DurgaLanguage.HINDI -> "GPS लोकेशन सक्रिय"
                                    else -> "GPS Live Ready"
                                },
                                icon = Icons.Default.GpsFixed,
                                tint = colors.primary,
                                bg = colors.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ==============================================================
            // 2. MAIN EMERGENCY ACTION (Visually Dominant, One-Hand Reachable)
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        Color(0xFFFCA5A5).copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(DurgaEmergencyRedLight, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = null,
                                        tint = DurgaEmergencyRed,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "આપાતકાલીન સહાય (SOS)"
                                            DurgaLanguage.HINDI -> "आपातकालीन सहायता (SOS)"
                                            else -> "Emergency Help (SOS)"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaEmergencyRedDark,
                                            fontSize = if (isAccessibilityLargeText) 18.sp else 16.sp
                                        )
                                    )
                                    Text(
                                        text = "National 112 & Location Alert",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DurgaTextSecondaryMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(DurgaEmergencyRedLight, RoundedCornerShape(6.dp))
                                    .border(0.5.dp, DurgaEmergencyRed.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "24x7 ACTIVE",
                                    color = DurgaEmergencyRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Large Circular SOS Button with gentle protective breathing ring
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
                                            onNavigateToEmergency()
                                        }
                                    )
                                }
                                .testTag("sos_button")
                        ) {
                            // Outer subtle breathing halo
                            Box(
                                modifier = Modifier
                                    .size((124 * pulseScale).dp)
                                    .clip(CircleShape)
                                    .background(DurgaEmergencyRed.copy(alpha = pulseAlpha))
                            )

                            // Circular Hold Progress Ring
                            if (sosHoldProgress > 0f) {
                                CircularProgressIndicator(
                                    progress = { sosHoldProgress },
                                    modifier = Modifier.size(132.dp),
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
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                DurgaEmergencyRed,
                                                DurgaEmergencyRedDark
                                            )
                                        )
                                    )
                                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "SOS",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "SOS",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 24.sp,
                                            letterSpacing = 2.sp
                                        )
                                    )
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "આપાતકાલીન"
                                            DurgaLanguage.HINDI -> "आपातकालीन"
                                            else -> "EMERGENCY"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "૩ સેકન્ડ દબાવી રાખો (Hold 3s) • સીધો કોલ કરવા ટેપ કરો"
                                DurgaLanguage.HINDI -> "३ सेकंड दबाकर रखें (Hold 3s) • सीधा कॉल करने के लिए टैप करें"
                                else -> "Hold 3s to Trigger SOS • Tap once to open 112"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DurgaEmergencyRedDark,
                                fontSize = if (isAccessibilityLargeText) 14.sp else 12.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ==============================================================
            // 3. ACTIVE CASE / REQUEST CARD (Compact Premium "Case Status")
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, colors.outline.copy(alpha = 0.8f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToNotifications() }
                        .testTag("home_case_tracker_banner")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Micro-label & Case ID Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CASE STATUS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = colors.primary,
                                        letterSpacing = 1.sp,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "•  ${latestNotification?.caseId ?: "DURGA-2026-784291"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            if (unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE11D48), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$unreadNotificationCount નવી",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = "છેલ્લો અપડેટ: ૧૨ મિનિટ પહેલાં",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Case Title
                        Text(
                            text = latestNotification?.title
                                ?: "ઓનલાઇન પીછો અને ધમકીની ફરિયાદ (Cyber Harassment)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = if (isAccessibilityLargeText) 17.sp else 15.sp
                            ),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Indicator Row
                        val progressPercent = latestNotification?.progressPercent ?: 90
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (progressPercent >= 90) DurgaSuccessGreen else colors.primary,
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (latestNotification?.isActionTaken == true || progressPercent >= 90) {
                                        when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "પોલીસ કાર્યવાહી શરૂ"
                                            DurgaLanguage.HINDI -> "पुलिस कार्रवाई शुरू"
                                            else -> "Police Action In Progress"
                                        }
                                    } else {
                                        when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "તપાસ પ્રગતિમાં"
                                            DurgaLanguage.HINDI -> "जांच जारी"
                                            else -> "Investigation In Progress"
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (progressPercent >= 90) DurgaSuccessGreen else colors.primary,
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Text(
                                text = "$progressPercent% પ્રક્રિયા પૂર્ણ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textSecondary,
                                    fontSize = 11.5.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progressPercent >= 90) DurgaSuccessGreen else colors.primary,
                            trackColor = colors.outline.copy(alpha = 0.3f),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Note Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (progressPercent >= 90) Color(0xFFECFDF5) else colors.primaryContainer.copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    0.5.dp,
                                    if (progressPercent >= 90) Color(0xFFA7F3D0) else colors.outline,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = latestNotification?.actionDetails
                                    ?: "પોલીસ અધિકારી દ્વારા સ્થળ નિરીક્ષણ અને પુરાવાઓની ચકાસણી પૂર્ણ. સુરક્ષા નોટિસ જારી.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (progressPercent >= 90) Color(0xFF065F46) else colors.textPrimary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                ),
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Next Action Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "તપાસ પ્રગતિ અને વિગતો જુઓ →"
                                    DurgaLanguage.HINDI -> "जांच प्रगति एवं विवरण देखें →"
                                    else -> "View Investigation Progress →"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.primary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // ==============================================================
            // 4. COMPLAINT SECTION (Major Action Panel)
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, colors.primary.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hero_complaint_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(colors.primaryContainer, RoundedCornerShape(10.dp)),
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
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "ફરિયાદ નોંધાવો"
                                            DurgaLanguage.HINDI -> "शिकायत दर्ज करें"
                                            else -> "File a Complaint"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary,
                                            fontSize = if (isAccessibilityLargeText) 19.sp else 17.sp
                                        )
                                    )
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "તમારી વાત સરળ રીતે લખો અને સુરક્ષિત રીતે સાચવો."
                                            DurgaLanguage.HINDI -> "अपनी बात सरल तरीके से लिखें और सुरक्षित रखें।"
                                            else -> "Write your statement easily and store securely."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "100% PRIVATE",
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action Button (+ Start New Complaint)
                        Button(
                            onClick = onNavigateToComplaintCategories,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("start_complaint_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "+ નવી ફરિયાદ શરૂ કરો"
                                        DurgaLanguage.HINDI -> "+ नई शिकायत शुरू करें"
                                        else -> "+ Start New Complaint"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Direct Categories Heading
                        Text(
                            text = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "સીધી કેટેગરી પસંદ કરો:"
                                DurgaLanguage.HINDI -> "सीधे श्रेणी चुनें:"
                                else -> "Quick Complaint Categories:"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Chips (Row 1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CleanCategoryChip(
                                label = "Cyber / Online",
                                icon = Icons.Default.PhoneAndroid,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.CYBER_ONLINE)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            CleanCategoryChip(
                                label = "Home / Family",
                                icon = Icons.Default.Home,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.HOME_DOMESTIC)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Chips (Row 2)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CleanCategoryChip(
                                label = "College / School",
                                icon = Icons.Default.School,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.SCHOOL_COLLEGE)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            CleanCategoryChip(
                                label = "Public Place",
                                icon = Icons.Default.LocationCity,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.PUBLIC_CITY)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Chips (Row 3)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CleanCategoryChip(
                                label = "Workplace",
                                icon = Icons.Default.Work,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.WORKPLACE)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            CleanCategoryChip(
                                label = "Child / Family",
                                icon = Icons.Default.WarningAmber,
                                onClick = {
                                    if (onStartCategoryComplaint != null) {
                                        onStartCategoryComplaint(IncidentCategory.CHILD_SAFETY)
                                    } else {
                                        onNavigateToComplaintCategories()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Secondary action: Track My Cases
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToMyCases() }
                                .background(colors.surfaceVariant, RoundedCornerShape(10.dp))
                                .border(1.dp, colors.outline.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 11.dp),
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
                                Spacer(modifier = Modifier.width(10.dp))
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

            // ==============================================================
            // 5. QUICK ACTIONS ("QUICK HELP" AREA - Verified 24x7 Helplines)
            // ==============================================================
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
                            text = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "ઝડપી સહાય નંબરો (Quick Help)"
                                DurgaLanguage.HINDI -> "त्वरित सहायता नंबर (Quick Help)"
                                else -> "Quick Help & Helplines"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = if (isAccessibilityLargeText) 18.sp else 16.sp
                            )
                        )
                        Text(
                            text = DurgaStrings.get("view_all_contacts", currentLanguage),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            modifier = Modifier
                                .clickable(onClick = onNavigateToEmergency)
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Clean Verified Helplines
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmergencyCallCard(
                            number = "112",
                            title = DurgaStrings.get("police_label", currentLanguage),
                            subtitle = "National Emergency & Police Patrol",
                            onCallClick = { dialNumber("112") }
                        )

                        EmergencyCallCard(
                            number = "1091",
                            title = DurgaStrings.get("women_helpline_label", currentLanguage),
                            subtitle = "Women in Distress & Abhayam 181",
                            onCallClick = { dialNumber("1091") },
                            badge = "WOMEN SAFETY"
                        )

                        EmergencyCallCard(
                            number = "108",
                            title = DurgaStrings.get("ambulance_label", currentLanguage),
                            subtitle = "Free Ambulance & Immediate Medical First Aid",
                            onCallClick = { dialNumber("108") }
                        )

                        EmergencyCallCard(
                            number = "1930",
                            title = "Cybercrime Helpline (1930)",
                            subtitle = "National Cyber Fraud & Online Abuse Reporting",
                            onCallClick = { dialNumber("1930") }
                        )
                    }
                }
            }

            // ==============================================================
            // 6. "I NEED HELP" GUIDED ASSISTANT
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToIdkWhatToDo)
                        .testTag("i_need_help_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(colors.primaryContainer, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Help",
                                    tint = colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = DurgaStrings.get("i_need_help_title", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = if (isAccessibilityLargeText) 17.sp else 15.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = DurgaStrings.get("i_need_help_sub", currentLanguage),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Proceed",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ==============================================================
            // 7. QUICK TOOLS GRID (Location, Nearby, Vault)
            // ==============================================================
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = DurgaStrings.get("quick_tools_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = if (isAccessibilityLargeText) 18.sp else 16.sp
                        ),
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val quickActions = listOf(
                        QuickActionItem(
                            id = "share_loc",
                            title = DurgaStrings.get("qa_share_location", currentLanguage),
                            subtitle = DurgaStrings.get("qa_share_location_sub", currentLanguage),
                            icon = Icons.Default.GpsFixed,
                            routeAction = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "URGENT SAFETY: My live location alert via DURGA app. Lat: 23.0225, Lng: 72.5714. Please check on me."
                                    )
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
                        )
                    )

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

            // ==============================================================
            // 8. CHILD FRIENDLY MODE & WITNESS INCIDENT
            // ==============================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline.copy(alpha = 0.7f)),
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
                                        color = colors.textPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Childline 1098",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline.copy(alpha = 0.7f)),
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
                                        color = colors.textPrimary,
                                        fontSize = 13.sp
                                    )
                                )
                                Text(
                                    text = "Safe & Anonymous",
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

            // ==============================================================
            // 9. IMPORTANT INFORMATION & OFFICIAL SAKHI / NALSA SUPPORT
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline.copy(alpha = 0.7f)),
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
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Important Information",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
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
                            color = colors.outline.copy(alpha = 0.5f)
                        )

                        OfficialInfoRow(
                            label = "National Legal Services (NALSA)",
                            desc = "Free legal aid & representation for women and minors in distress.",
                            number = "15100"
                        ) { dialNumber("15100") }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = colors.outline.copy(alpha = 0.5f)
                        )

                        OfficialInfoRow(
                            label = "Railway Child & Women Safety",
                            desc = "RPF 24x7 security helpline across all trains and stations.",
                            number = "139"
                        ) { dialNumber("139") }
                    }
                }
            }

            // ==============================================================
            // 10. OFFICIAL POLICE & CENTRAL ADMIN PORTALS HUB
            // ==============================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(colors.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "🛡️ પોલીસ અને એડમિન લાઈવ વેબ પોર્ટલ"
                                        DurgaLanguage.HINDI -> "🛡️ पुलिस एवं एडमिन लाइव वेब पोर्टल"
                                        DurgaLanguage.ENGLISH -> "🛡️ Police & Admin Live Portals"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                )
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "એપમાંથી સીધું જ ઓપન કરો (Direct in-app access)"
                                        DurgaLanguage.HINDI -> "ऐप से ही सीधा खोलें (Direct in-app access)"
                                        DurgaLanguage.ENGLISH -> "Open directly inside app without browser redirects"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = colors.textSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToWebPortals,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "વેબ પોર્ટલ ખોલો"
                                        DurgaLanguage.HINDI -> "वेब पोर्टल खोलें"
                                        DurgaLanguage.ENGLISH -> "Open Live Portals"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            OutlinedButton(
                                onClick = onNavigateToAuthorityPortal,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "ઓફિસર લૉગિન"
                                        DurgaLanguage.HINDI -> "अधिकारी लॉगिन"
                                        DurgaLanguage.ENGLISH -> "Officer Desk"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
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
                shape = RoundedCornerShape(18.dp),
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
                            .background(DurgaSectionSoft, RoundedCornerShape(10.dp))
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
                            shape = RoundedCornerShape(10.dp),
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
                            shape = RoundedCornerShape(10.dp),
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
private fun StatusBadgePill(
    label: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = tint,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CleanCategoryChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = colors.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline.copy(alpha = 0.6f)),
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    item: QuickActionItem,
    isLarge: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline.copy(alpha = 0.6f)),
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
                        .background(colors.primaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = colors.textSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = if (isLarge) 15.sp else 13.sp
                ),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
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
    val colors = LocalDurgaColors.current
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
                    color = colors.textPrimary,
                    fontSize = 13.sp
                )
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            onClick = onCall,
            colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call $number",
                tint = colors.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            )
        }
    }
}
