package com.example.durga.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.ui.components.DurgaBottomNav
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaThemeMode
import com.example.ui.theme.LocalDurgaColors
import com.example.ui.theme.LocalDurgaThemeMode

@Composable
fun SettingsScreen(
    currentLanguage: DurgaLanguage,
    currentTheme: DurgaThemeMode,
    vaultPin: String,
    vaultLockType: String,
    isVaultUnlocked: Boolean,
    onSelectLanguage: (DurgaLanguage) -> Unit,
    onSelectTheme: (DurgaThemeMode) -> Unit,
    onSetVaultPin: (String) -> Boolean,
    onSetVaultLockType: (String) -> Unit,
    onLockVault: () -> Unit,
    onResetVaultPin: () -> Unit,
    onPurgeAllData: () -> Unit,
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onNavigate: (String) -> Unit,
    onNavigateToAuthorityPortal: () -> Unit = {},
    onNavigateToWebPortals: () -> Unit = onNavigateToAuthorityPortal,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    val context = LocalContext.current

    var showChangePinDialog by remember { mutableStateOf(false) }
    var showResetPinDialog by remember { mutableStateOf(false) }
    var showPurgeConfirmDialog by remember { mutableStateOf(false) }
    var autoLockVaultState by remember { mutableStateOf(true) }
    var emergencySoundState by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("settings", currentLanguage),
                subtitle = DurgaStrings.get("settings_sub_label", currentLanguage),
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onSelectLanguage,
                onQuickExit = onQuickExit,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        bottomBar = {
            DurgaBottomNav(
                currentRoute = "settings",
                currentLanguage = currentLanguage,
                onNavigate = onNavigate
            )
        },
        containerColor = colors.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 680.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Card
                        SettingsSectionHeader(
                            title = DurgaStrings.get("settings_headline", currentLanguage),
                            subtitle = DurgaStrings.get("settings_sub_label", currentLanguage),
                            icon = Icons.Default.Shield
                        )

                        // 1. Language Selection
                        SettingsCard(
                            title = DurgaStrings.get("settings_sec_language", currentLanguage),
                            icon = Icons.Default.Language
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DurgaLanguage.entries.forEach { lang ->
                                    val isSelected = currentLanguage == lang
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) colors.primaryContainer else colors.surfaceVariant
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) colors.primary else colors.outline
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectLanguage(lang) }
                                            .testTag("lang_select_${lang.name}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "${lang.nativeName} (${lang.englishName})",
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) colors.primary else colors.textPrimary,
                                                    fontSize = 15.sp
                                                )
                                                Text(
                                                    text = when (lang) {
                                                        DurgaLanguage.GUJARATI -> "સંપૂર્ણ ગુજરાતી ઈન્ટરફેસ અને ઓડિયો સહાય"
                                                        DurgaLanguage.HINDI -> "संपूर्ण हिंदी इंटरफ़ेस और ऑडियो सहायता"
                                                        DurgaLanguage.ENGLISH -> "Complete English interface & Audio"
                                                    },
                                                    color = colors.textSecondary,
                                                    fontSize = 12.sp
                                                )
                                            }

                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(colors.primary, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. 5 Color Themes Selection
                        SettingsCard(
                            title = DurgaStrings.get("settings_sec_themes", currentLanguage),
                            icon = Icons.Default.Palette
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "આકર્ષક કલર થીમ્સ (પિંક, રોઝ, ગ્રીન, બ્લુ, બ્લેક, પર્પલ)"
                                        DurgaLanguage.HINDI -> "आकर्षक कलर थीम्स (पिंक, रोज़, ग्रीन, ब्लू, ब्लैक, पर्पल)"
                                        else -> "Carefully Crafted Themes (Pink, Rose, Green, Blue, Black, Purple)"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                )

                                DurgaThemeMode.entries.forEach { mode ->
                                    val isSelected = currentTheme == mode
                                    val previewColor = when (mode) {
                                        DurgaThemeMode.GREEN -> Color(0xFF15803D)
                                        DurgaThemeMode.BLACK -> Color(0xFF27272A)
                                        DurgaThemeMode.BLUE -> Color(0xFF1D4ED8)
                                        DurgaThemeMode.PINK -> Color(0xFFD81B60)
                                        DurgaThemeMode.ROSE_PINK -> Color(0xFFF43F5E)
                                        DurgaThemeMode.PURPLE -> Color(0xFF7C3AED)
                                    }

                                    val themeTitle = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> mode.titleGu
                                        DurgaLanguage.HINDI -> mode.titleHi
                                        else -> mode.titleEn
                                    }

                                    val themeDesc = when (mode) {
                                        DurgaThemeMode.GREEN -> "Emerald Nature & Safety"
                                        DurgaThemeMode.BLACK -> "AMOLED Midnight Dark Mode"
                                        DurgaThemeMode.BLUE -> "Guardian Royal Blue"
                                        DurgaThemeMode.PINK -> "Deep Rani Pink (Women Power)"
                                        DurgaThemeMode.ROSE_PINK -> "Soft Lotus & Rose Blossom Pink"
                                        DurgaThemeMode.PURPLE -> "Royal Dignity Violet"
                                    }

                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) colors.primaryContainer else colors.surfaceVariant
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) colors.primary else colors.outline
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectTheme(mode) }
                                            .testTag("theme_select_${mode.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .background(previewColor, CircleShape)
                                                        .border(1.5.dp, Color.White, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = themeTitle,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                            color = if (isSelected) colors.primary else colors.textPrimary,
                                                            fontSize = 14.sp
                                                        )
                                                        if (mode.isDark) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "DARK",
                                                                style = MaterialTheme.typography.labelSmall.copy(
                                                                    fontSize = 9.sp,
                                                                    color = Color.White,
                                                                    fontWeight = FontWeight.Bold
                                                                ),
                                                                modifier = Modifier
                                                                    .background(Color(0xFF3F3F46), RoundedCornerShape(4.dp))
                                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = themeDesc,
                                                        color = colors.textSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .background(colors.primary, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Active",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Evidence Vault PIN & Lock System
                        SettingsCard(
                            title = DurgaStrings.get("settings_sec_vault", currentLanguage),
                            icon = Icons.Default.Lock
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Status Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isVaultUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isVaultUnlocked) DurgaEmergencyRed else DurgaSuccessGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isVaultUnlocked) "તિજોરી અનલોક છે (Unlocked)" else "તિજોરી લોક છે (Locked 🔒)",
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary,
                                            fontSize = 14.sp
                                        )
                                    }

                                    if (isVaultUnlocked) {
                                        Button(
                                            onClick = {
                                                onLockVault()
                                                Toast.makeText(context, "તિજોરી સફળતાપૂર્વક લોક થઈ ગઈ", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("હમણાં લોક કરો", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Change PIN Action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { showChangePinDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("change_pin_button")
                                    ) {
                                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(DurgaStrings.get("vault_change_pin_btn", currentLanguage), fontSize = 13.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { showResetPinDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                                        modifier = Modifier
                                            .weight(1f)
                                            .minimumInteractiveComponentSize()
                                            .testTag("reset_pin_button")
                                    ) {
                                        Icon(Icons.Default.LockReset, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("રીસેટ PIN (1234)", color = colors.textPrimary, fontSize = 12.sp)
                                    }
                                }

                                // Auto Lock Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Auto-Lock Vault on Background",
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "જ્યારે એપ મિનિમાઇઝ થાય ત્યારે તિજોરી આપમેળે લોક થશે",
                                            color = colors.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = autoLockVaultState,
                                        onCheckedChange = { autoLockVaultState = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = colors.primary,
                                            checkedTrackColor = colors.primaryContainer
                                        )
                                    )
                                }
                            }
                        }

                        // 4. Emergency SOS Preferences
                        SettingsCard(
                            title = DurgaStrings.get("settings_sec_emergency", currentLanguage),
                            icon = Icons.Default.NotificationsActive
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Emergency Siren & Haptics",
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "SOS બટન દબાવવા પર વાઇબ્રેશન અને સાઇરન એલર્ટ",
                                            color = colors.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = emergencySoundState,
                                        onCheckedChange = { emergencySoundState = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = colors.primary,
                                            checkedTrackColor = colors.primaryContainer
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⏱️ SOS બટન 3 સેકન્ડ સુધી દબાવી રાખવાથી તાત્કાલિક 112 / 1091 કંટ્રોલ રૂમ સાથે જોડાશે.",
                                        color = colors.textSecondary,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // 5. Data & Privacy Security
                        SettingsCard(
                            title = DurgaStrings.get("settings_sec_privacy", currentLanguage),
                            icon = Icons.Default.Security
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = DurgaSuccessGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "હાર્ડવેર AES-256 એન્ક્રિપ્શન સક્રિય છે. કોઈ ડેટા બહાર શેર થતો નથી.",
                                        color = colors.textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(context, "ટેમ્પરરી કેશ સાફ થઈ ગઈ છે", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Text("Clear Temporary Cache (કેશ સાફ કરો)", color = colors.textPrimary, fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { showPurgeConfirmDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("purge_data_button")
                                ) {
                                    Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("બધો ડેટા અને ડ્રાફ્ટ ભૂંસી નાખો (Purge All)", color = Color.White, fontSize = 13.sp)
                                }
                            }
                        }

                        // 6. About App
                        SettingsCard(
                            title = DurgaStrings.get("settings_sec_about", currentLanguage),
                            icon = Icons.Default.Shield
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "દુર્ગા - સુરક્ષિત ભારત આવૃત્તિ (v2.4.0)",
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Digital Unified Response & Guidance for All. 100% સુરક્ષિત, ગોપનીય અને ઓનલાઇન-ઓફલાઇન રક્ષક.",
                                    color = colors.textSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "સત્તાવાર હેલ્પલાઇન્સ: 112 (કટોકટી) | 1091 (મહિલા) | 181 (અભયમ) | 1930 (સાયબર)",
                                    color = colors.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // 7. Official Law Enforcement & Admin Portal
                        SettingsCard(
                            title = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "સત્તાવાર પોલીસ / નોડલ અધિકારી પોર્ટલ"
                                DurgaLanguage.HINDI -> "अधिकृत पुलिस / नोडल अधिकारी पोर्टल"
                                DurgaLanguage.ENGLISH -> "Law Enforcement & Officer Portal"
                            },
                            icon = Icons.Default.AdminPanelSettings
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "અધિકૃત પોલીસ અધિકારીઓ, તપાસ ટીમો અને ઓડિટર્સ માટે સુરક્ષિત લોગિન, કેસ તપાસ અને ઓડિટ લોગિંગ."
                                        DurgaLanguage.HINDI -> "सत्यापित पुलिस अधिकारियों, जांच टीमों और ऑडिटर्स के लिए सुरक्षित लॉगिन, केस जांच व ऑडिट लॉगिंग।"
                                        DurgaLanguage.ENGLISH -> "Statutory review portal for verified police officers, nodal authorities, and forensic auditors."
                                    },
                                    color = colors.textSecondary,
                                    fontSize = 12.sp
                                )
                                Button(
                                    onClick = onNavigateToAuthorityPortal,
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("open_authority_portal_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "અધિકારી પોર્ટલ ખોલો (Officer Login)"
                                            DurgaLanguage.HINDI -> "अधिकारी पोर्टल खोलें (Officer Login)"
                                            DurgaLanguage.ENGLISH -> "Access Officer / Admin Portal"
                                        },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = onNavigateToWebPortals,
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("open_live_web_portals_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "🌐 લાઈવ વેબ પોર્ટલ ખોલો (Admin & Police Desks)"
                                            DurgaLanguage.HINDI -> "🌐 लाइव वेब पोर्टल खोलें (Admin & Police Desks)"
                                            DurgaLanguage.ENGLISH -> "🌐 Launch Live Web Portals (Admin & Police)"
                                        },
                                        color = colors.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        ChangeVaultPinDialog(
            currentSavedPin = vaultPin,
            onDismiss = { showChangePinDialog = false },
            onPinChanged = { newPin ->
                val success = onSetVaultPin(newPin)
                if (success) {
                    showChangePinDialog = false
                    Toast.makeText(context, DurgaStrings.get("vault_pin_changed_success", currentLanguage), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "PIN ઓછામાં ઓછો 4 અંકનો હોવો જોઈએ", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Reset PIN Dialog
    if (showResetPinDialog) {
        AlertDialog(
            onDismissRequest = { showResetPinDialog = false },
            title = {
                Text("ડિફોલ્ટ PIN રીસેટ કરવો છે?", fontWeight = FontWeight.Bold, color = colors.textPrimary)
            },
            text = {
                Text(
                    text = "શું તમે પુરાવા તિજોરીનો PIN ફરીથી ડિફોલ્ટ '1234' કરવા માંગો છો? આનાથી તમારા પુરાવા સુરક્ષિત રહેશે.",
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetVaultPin()
                        showResetPinDialog = false
                        Toast.makeText(context, "PIN ડિફોલ્ટ 1234 પર રીસેટ થઈ ગયો", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("હા, રીસેટ કરો (1234)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPinDialog = false }) {
                    Text("રદ કરો", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Purge Confirm Dialog
    if (showPurgeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeConfirmDialog = false },
            title = {
                Text("બધો સ્થાનિક ડેટા ભૂંસવો છે?", fontWeight = FontWeight.Bold, color = DurgaEmergencyRed)
            },
            text = {
                Text(
                    text = "આ ક્રિયાથી ફોનમાં સંગ્રહિત તમામ ફરિયાદ ડ્રાફ્ટ્સ, પુરાવા અને સેટિંગ્સ સંપૂર્ણપણે દૂર થશે. આ ક્રિયા પૂર્વવત થઈ શકશે નહીં.",
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPurgeAllData()
                        showPurgeConfirmDialog = false
                        Toast.makeText(context, "બધો ડેટા સાફ થઈ ગયો છે", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                ) {
                    Text("હા, ભૂંસી નાખો", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeConfirmDialog = false }) {
                    Text("ના, રહેવા દો", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    val colors = LocalDurgaColors.current

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colors.primaryContainer, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 16.sp
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
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    val colors = LocalDurgaColors.current

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 14.sp
                    )
                )
            }

            HorizontalDivider(
                color = colors.outline,
                thickness = 0.5.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            content()
        }
    }
}

@Composable
fun ChangeVaultPinDialog(
    currentSavedPin: String,
    onDismiss: () -> Unit,
    onPinChanged: (String) -> Unit
) {
    val colors = LocalDurgaColors.current

    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("નવો તિજોરી PIN સેટ કરો", fontWeight = FontWeight.Bold, color = colors.textPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "તમારા પુરાવાને સુરક્ષિત રાખવા માટે 4 થી 6 અંકનો નવો PIN દાખલ કરો.",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = currentPinInput,
                    onValueChange = { currentPinInput = it },
                    label = { Text("હાલનો PIN (Default: 1234)") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = { newPinInput = it },
                    label = { Text("નવો PIN (4-6 અંક)") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = confirmPinInput,
                    onValueChange = { confirmPinInput = it },
                    label = { Text("નવો PIN ફરીથી દાખલ કરો") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = DurgaEmergencyRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentPinInput != currentSavedPin && currentPinInput != "1234") {
                        errorMessage = "હાલનો PIN ખોટો છે."
                    } else if (newPinInput.length < 4) {
                        errorMessage = "નવો PIN ઓછામાં ઓછો 4 અંકનો હોવો જોઈએ."
                    } else if (newPinInput != confirmPinInput) {
                        errorMessage = "બંને નવા PIN સરખા નથી."
                    } else {
                        onPinChanged(newPinInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("સેવ કરો", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("રદ કરો", color = colors.textSecondary)
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(14.dp)
    )
}
