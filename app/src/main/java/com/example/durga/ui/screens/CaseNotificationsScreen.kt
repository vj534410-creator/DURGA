package com.example.durga.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.CaseNotificationEntity
import com.example.durga.ui.components.DurgaBottomNav
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.LocalDurgaColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaseNotificationsScreen(
    currentLanguage: DurgaLanguage,
    notifications: List<CaseNotificationEntity>,
    unreadCount: Int,
    activeFilter: String,
    onFilterChange: (String) -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDeleteNotification: (String) -> Unit,
    onDeleteAll: () -> Unit,
    onSelectCase: (String) -> Unit,
    onNavigateToRoute: (String) -> Unit = {},
    onSimulateNextUpdate: () -> Unit,
    onSimulateFeatureUpdate: () -> Unit = {},
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onNavigate: ((String) -> Unit)? = null,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalDurgaColors.current

    // Permission state check for Android 13+
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(
                context,
                when (currentLanguage) {
                    DurgaLanguage.GUJARATI -> "સૂચનાઓ સક્ષમ થઈ! હવે તમને કેસના તમામ અપડેટ્સ અને નવા ફીચર્સ મળશે."
                    DurgaLanguage.HINDI -> "सूचनाएं सक्षम की गईं! अब आपको केस के अपडेट और नए फीचर्स मिलेंगे।"
                    else -> "Notifications enabled! You will now receive all case updates and new features."
                },
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Filtered list
    val filteredNotifications = remember(notifications, activeFilter) {
        when (activeFilter) {
            "APP_FEATURES" -> notifications.filter { it.notificationType == "APP_FEATURE" }
            "ACTION_TAKEN" -> notifications.filter { it.isActionTaken && it.notificationType != "APP_FEATURE" }
            "IN_PROGRESS" -> notifications.filter { !it.isActionTaken && it.notificationType != "APP_FEATURE" }
            "UNREAD" -> notifications.filter { !it.isRead }
            else -> notifications
        }
    }

    val titleText = when (currentLanguage) {
        DurgaLanguage.GUJARATI -> "સૂચનાઓ & અપડેટ્સ (Notifications)"
        DurgaLanguage.HINDI -> "सूचनाएं एवं अपडेट्स (Notifications)"
        else -> "Notifications & App Updates"
    }

    val subtitleText = when (currentLanguage) {
        DurgaLanguage.GUJARATI -> "પોલીસ કાર્યવાહી, તપાસ પ્રગતિ અને નવા એપ ફીચર્સ"
        DurgaLanguage.HINDI -> "पुलिस कार्रवाई, जांच प्रगति और नए ऐप फीचर्स"
        else -> "Police actions, investigation tracking & new app features"
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = titleText,
                subtitle = subtitleText,
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        bottomBar = {
            if (onNavigate != null) {
                DurgaBottomNav(
                    currentRoute = "cases",
                    currentLanguage = currentLanguage,
                    onNavigate = onNavigate
                )
            }
        },
        containerColor = colors.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Notification Permission Banner if not granted
            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.primaryContainer.copy(alpha = 0.6f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(colors.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "સિસ્ટમ સૂચનાઓ ચાલુ કરો"
                                        DurgaLanguage.HINDI -> "सिस्टम सूचनाएं चालू करें"
                                        else -> "Enable System Notifications"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                )
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "પોલીસ દ્વારા લેવાયેલા પગલાં અને કેસ કેટલો આગળ વધ્યો તેની તાત્કાલિક સૂચનાઓ મેળવો."
                                        DurgaLanguage.HINDI -> "पुलिस द्वारा की गई कार्रवाई और जांच प्रगति की तत्काल सूचनाएं प्राप्त करें।"
                                        else -> "Get real-time status bar updates as police take action on your case."
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("enable_notifications_button")
                                ) {
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "પરવાનગી આપો (Allow)"
                                            DurgaLanguage.HINDI -> "अनुमति दें (Allow)"
                                            else -> "Allow Notifications"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Summary & Live Simulation Trigger Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(colors.primaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "લાઈવ તપાસ મોનિટર"
                                            DurgaLanguage.HINDI -> "लाइव जांच मॉनिटर"
                                            else -> "Live Investigation Monitor"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                    )
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "${notifications.size} કુલ અપડેટ્સ • $unreadCount નવી સૂચના"
                                            DurgaLanguage.HINDI -> "${notifications.size} कुल अपडेट • $unreadCount नई सूचना"
                                            else -> "${notifications.size} Total updates • $unreadCount unread"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            if (unreadCount > 0) {
                                TextButton(
                                    onClick = onMarkAllAsRead,
                                    modifier = Modifier.testTag("mark_all_read_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DoneAll,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "બધા વાંચ્યા"
                                            DurgaLanguage.HINDI -> "सभी पढ़े"
                                            else -> "Read All"
                                        },
                                        fontSize = 12.sp,
                                        color = colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Simulation button 1: Case progression and police action
                        Button(
                            onClick = {
                                onSimulateNextUpdate()
                                Toast.makeText(
                                    context,
                                    when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "નવી પોલીસ કાર્યવાહી સૂચના મોકલાઈ! તમારા ફોનનું નોટિફિકેશન બાર ચેક કરો."
                                        DurgaLanguage.HINDI -> "नई पुलिस कार्रवाई सूचना भेजी गई! अपने फोन का नोटिफिकेशन बार देखें।"
                                        else -> "New police action notification posted! Check your system notification tray."
                                    },
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulate_police_update_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "પોલીસ કાર્યવાહી & કેસ પ્રગતિ ટેસ્ટ (Police Action)"
                                        DurgaLanguage.HINDI -> "पुलिस कार्रवाई एवं केस प्रगति टेस्ट (Police Action)"
                                        else -> "Simulate Police Action & Case Progress"
                                    },
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Simulation button 2: App Feature Update Notification
                        OutlinedButton(
                            onClick = {
                                onSimulateFeatureUpdate()
                                Toast.makeText(
                                    context,
                                    when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "નવા ફીચરની નોટિફિકેશન મોકલાઈ! નોટિફિકેશન બાર ચેક કરો."
                                        DurgaLanguage.HINDI -> "नए ऐप फीचर की सूचना भेजी गई! नोटिफिकेशन बार देखें।"
                                        else -> "New app feature release notification posted! Check notification bar."
                                    },
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("simulate_feature_update_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "નવું એપ ફીચર નોટિફિકેશન ટેસ્ટ (New App Feature)"
                                        DurgaLanguage.HINDI -> "नया ऐप फीचर नोटिफिकेशन टेस्ट (New App Feature)"
                                        else -> "Simulate New App Feature Notification"
                                    },
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        Triple(
                            "ALL",
                            when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "બધી (${notifications.size})"
                                DurgaLanguage.HINDI -> "सभी (${notifications.size})"
                                else -> "All (${notifications.size})"
                            },
                            Icons.Default.NotificationsActive
                        ),
                        Triple(
                            "APP_FEATURES",
                            when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "નવા ફીચર્સ (${notifications.count { it.notificationType == "APP_FEATURE" }})"
                                DurgaLanguage.HINDI -> "नए फीचर्स (${notifications.count { it.notificationType == "APP_FEATURE" }})"
                                else -> "Features (${notifications.count { it.notificationType == "APP_FEATURE" }})"
                            },
                            Icons.Default.AutoAwesome
                        ),
                        Triple(
                            "ACTION_TAKEN",
                            when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "કાર્યવાહી થઈ (${notifications.count { it.isActionTaken && it.notificationType != "APP_FEATURE" }})"
                                DurgaLanguage.HINDI -> "कार्रवाई हुई (${notifications.count { it.isActionTaken && it.notificationType != "APP_FEATURE" }})"
                                else -> "Action Taken (${notifications.count { it.isActionTaken && it.notificationType != "APP_FEATURE" }})"
                            },
                            Icons.Default.CheckCircle
                        ),
                        Triple(
                            "IN_PROGRESS",
                            when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "તપાસ પ્રગતિમાં (${notifications.count { !it.isActionTaken && it.notificationType != "APP_FEATURE" }})"
                                DurgaLanguage.HINDI -> "जांच जारी (${notifications.count { !it.isActionTaken && it.notificationType != "APP_FEATURE" }})"
                                else -> "In Progress (${notifications.count { !it.isActionTaken && it.notificationType != "APP_FEATURE" }})"
                            },
                            Icons.Default.HourglassTop
                        ),
                        Triple(
                            "UNREAD",
                            when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "નવી / વણવાંચેલી ($unreadCount)"
                                DurgaLanguage.HINDI -> "नई / अपठित ($unreadCount)"
                                else -> "Unread ($unreadCount)"
                            },
                            Icons.Default.MarkEmailRead
                        )
                    )

                    items(filters) { (key, label, icon) ->
                        val isSelected = activeFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(key) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) colors.primary else colors.textSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primaryContainer,
                                selectedLabelColor = colors.primary
                            ),
                            modifier = Modifier.testTag("filter_chip_$key")
                        )
                    }
                }
            }

            // 4. Empty State
            if (filteredNotifications.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = colors.textSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "કોઈ સૂચનાઓ મળેલ નથી"
                                    DurgaLanguage.HINDI -> "कोई सूचना नहीं मिली"
                                    else -> "No Notifications in this filter"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "જ્યારે પણ કોઈ ફરિયાદ પર પોલીસ દ્વારા કાર્યવાહી કરવામાં આવશે, ત્યારે તમને અહીં વિગતવાર સૂચના મળશે."
                                    DurgaLanguage.HINDI -> "जब भी किसी शिकायत पर पुलिस द्वारा कार्रवाई की जाएगी, आपको यहाँ विस्तृत सूचना मिलेगी।"
                                    else -> "When action is taken on a complaint, all stage progress and official actions will appear here."
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // 5. Notification Cards List
            items(filteredNotifications, key = { it.id }) { item ->
                CaseNotificationCard(
                    notification = item,
                    currentLanguage = currentLanguage,
                    onMarkAsRead = { onMarkAsRead(item.id) },
                    onDelete = { onDeleteNotification(item.id) },
                    onOpenCase = { onSelectCase(item.caseId) },
                    onNavigateToRoute = onNavigateToRoute
                )
            }

            // Bottom Clear All button if list is not empty
            if (notifications.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onDeleteAll,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_all_notifications_button")
                    ) {
                        Icon(imageVector = Icons.Default.ClearAll, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "તમામ સૂચનાઓ સાફ કરો (Clear History)"
                                DurgaLanguage.HINDI -> "सभी सूचनाएं हटाएं (Clear History)"
                                else -> "Clear Notification History"
                            },
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CaseNotificationCard(
    notification: CaseNotificationEntity,
    currentLanguage: DurgaLanguage,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    onOpenCase: () -> Unit,
    onNavigateToRoute: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    val formattedTime = remember(notification.timestamp) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(notification.timestamp))
    }

    val isFeatureNotification = notification.notificationType == "APP_FEATURE"

    if (isFeatureNotification) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (notification.isRead) colors.surface else colors.primaryContainer.copy(alpha = 0.35f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                if (notification.isRead) 1.dp else 1.5.dp,
                if (notification.isRead) colors.outline else colors.primary
            ),
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    if (!notification.isRead) onMarkAsRead()
                    if (notification.targetRoute.isNotBlank()) onNavigateToRoute(notification.targetRoute)
                }
                .testTag("feature_notification_${notification.id}")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(colors.primaryContainer, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = notification.stageTitle.ifBlank { "નવું ફીચર / NEW FEATURE" },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!notification.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(colors.primary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("delete_notification_${notification.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = colors.textSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Feature Title
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Feature Details Box
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.background),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, colors.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "ફેરફાર & નવી સુવિધાઓ (What's New):"
                                    DurgaLanguage.HINDI -> "परिवर्तन एवं नई सुविधाएं (What's New):"
                                    else -> "App Feature Details & Release Notes:"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = notification.actionDetails,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.textPrimary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = colors.outline, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DURGA Core • Feature Release",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "સુરક્ષા અપડેટ / Security Update",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (notification.targetRoute.isNotBlank()) {
                        Button(
                            onClick = {
                                if (!notification.isRead) onMarkAsRead()
                                onNavigateToRoute(notification.targetRoute)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("open_feature_${notification.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Launch,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "આ ફીચર ખોલો (Try Feature Now)"
                                        DurgaLanguage.HINDI -> "यह फीचर खोलें (Try Feature Now)"
                                        else -> "Explore Feature Now"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (!notification.isRead) {
                        TextButton(onClick = onMarkAsRead) {
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "વાંચ્યું (Read)"
                                    DurgaLanguage.HINDI -> "पढ़ा (Read)"
                                    else -> "Mark Read"
                                },
                                fontSize = 11.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }
        }
        return
    }

    val actionBadgeBg = if (notification.isActionTaken) {
        Color(0xFFE8F5E9)
    } else {
        Color(0xFFFFF8E1)
    }

    val actionBadgeBorder = if (notification.isActionTaken) {
        Color(0xFF2E7D32)
    } else {
        Color(0xFFF57F17)
    }

    val actionTextColor = if (notification.isActionTaken) {
        Color(0xFF1B5E20)
    } else {
        Color(0xFFE65100)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) colors.surface else colors.primaryContainer.copy(alpha = 0.25f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (notification.isRead) 1.dp else 1.5.dp,
            if (notification.isRead) colors.outline else colors.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (!notification.isRead) onMarkAsRead()
                onOpenCase()
            }
            .testTag("notification_card_${notification.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Case ID, Category, Time, Unread Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(colors.primaryContainer, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = notification.caseId,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colors.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("delete_notification_${notification.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = colors.textSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Complaint Title
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 15.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // WORK PROGRESS SECTION ("enu kam ketlu ketlu thayu")
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.background),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "કામની પ્રગતિ (Work Progress):"
                                    DurgaLanguage.HINDI -> "कार्य प्रगति (Work Progress):"
                                    else -> "Investigation Progress:"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Text(
                            text = "${notification.progressPercent}%",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { notification.progressPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (notification.progressPercent >= 90) Color(0xFF2E7D32) else colors.primary,
                        trackColor = colors.outline.copy(alpha = 0.3f),
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "તબક્કો (Stage): ${notification.stageTitle}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // POLICE ACTION TAKEN SECTION ("action lidhu ke nahi a enu aave")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, actionBadgeBorder, RoundedCornerShape(8.dp))
                    .background(actionBadgeBg, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (notification.isActionTaken) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = actionTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (notification.isActionTaken) {
                                when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "✅ પોલીસ દ્વારા કાર્યવાહી લેવાઈ (ACTION TAKEN)"
                                    DurgaLanguage.HINDI -> "✅ पुलिस द्वारा कार्रवाई की गई (ACTION TAKEN)"
                                    else -> "✅ POLICE ACTION TAKEN"
                                }
                            } else {
                                when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "⏳ તપાસ પ્રગતિ હેઠળ (ACTION PENDING / IN PROGRESS)"
                                    DurgaLanguage.HINDI -> "⏳ जांच प्रगति पर है (ACTION PENDING / IN PROGRESS)"
                                    else -> "⏳ INVESTIGATION IN PROGRESS (ACTION PENDING)"
                                }
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = actionTextColor,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = notification.actionStatusText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = actionTextColor.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ALL INFORMATION / ACTION DETAILS ("and aall information notification ma batave")
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = when (currentLanguage) {
                            DurgaLanguage.GUJARATI -> "🚨 લેવાયેલા પગલાં અને વિગતો (Action Details):"
                            DurgaLanguage.HINDI -> "🚨 की गई कार्रवाई और विवरण (Action Details):"
                            else -> "🚨 Action Taken & Investigation Narrative:"
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.actionDetails,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = colors.outline, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Officer and Police Station Details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalPolice,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "અધિકારી: ${notification.officerName}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Text(
                            text = notification.policeStation,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = colors.textSecondary,
                                fontSize = 10.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: View Case Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onOpenCase,
                    modifier = Modifier.testTag("open_case_${notification.caseId}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderShared,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (currentLanguage) {
                            DurgaLanguage.GUJARATI -> "સંપૂર્ણ કેસ જુઓ (View Case)"
                            DurgaLanguage.HINDI -> "पूरा केस देखें (View Case)"
                            else -> "View Full Case Record"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }

                if (!notification.isRead) {
                    TextButton(onClick = onMarkAsRead) {
                        Text(
                            text = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "વાંચ્યું"
                                DurgaLanguage.HINDI -> "पढ़ा"
                                else -> "Mark Read"
                            },
                            fontSize = 11.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
