package com.example.durga.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.foundation.layout.widthIn
import com.example.ui.theme.LocalDurgaColors
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseStatus
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.IncidentCategory
import com.example.durga.ui.components.DurgaBadgeType
import com.example.durga.ui.components.DurgaBottomNav
import com.example.durga.ui.components.DurgaStatusBadge
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaEmergencyRed
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

// --- 1. My Cases Screen ---
@Composable
fun MyCasesScreen(
    currentLanguage: DurgaLanguage,
    casesList: List<CaseEntity>,
    onSelectCase: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onNavigate: ((String) -> Unit)? = null,
    onNavigateToScanner: () -> Unit = {},
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredCases = if (searchQuery.isBlank()) {
        casesList
    } else {
        casesList.filter {
            it.caseId.contains(searchQuery, ignoreCase = true) ||
            it.title.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("track_cases", currentLanguage),
                subtitle = "Track registered complaint progress & status",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenNotifications = onNavigateToNotifications,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        bottomBar = {
            DurgaBottomNav(
                currentRoute = "cases",
                currentLanguage = currentLanguage,
                onNavigate = { route ->
                    if (onNavigate != null) {
                        onNavigate(route)
                    } else if (route == "home") {
                        onBack()
                    }
                }
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaPrimaryNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToNotifications() }
                        .testTag("my_cases_notifications_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DurgaSectionSoft, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = DurgaPrimaryNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "પોલીસ કાર્યવાહી & કેસ સૂચનાઓ",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy
                                    )
                                )
                                Text(
                                    text = "ફરિયાદ પર થયેલ કામ અને પોલીસ પગલાંની લાઈવ વિગતો",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = "જુઓ →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaBlueAction
                            )
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onNavigateToScanner,
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("scan_complaint_qr_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Scan Complaint QR",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCAN COMPLAINT QR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by Case ID or keywords...", color = DurgaTextLightMuted, fontSize = 13.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DurgaSurfaceWhite,
                        unfocusedContainerColor = DurgaSurfaceWhite,
                        focusedBorderColor = DurgaPrimaryNavy,
                        unfocusedBorderColor = DurgaBorderLight
                    )
                )
            }

            if (filteredCases.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(DurgaSectionSoft, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderShared,
                                    contentDescription = null,
                                    tint = DurgaTextLightMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = DurgaStrings.get("no_cases_yet", currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "When you submit an official complaint or witness report, its encrypted tracking status will show here.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            } else {
                items(filteredCases) { caseEntity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCase(caseEntity.caseId) }
                            .testTag("case_item_${caseEntity.caseId}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = caseEntity.caseId,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaPrimaryNavy,
                                        fontSize = 14.sp
                                    )
                                )

                                val (badgeType, customColor) = when (caseEntity.status) {
                                    CaseStatus.REPORT_SUBMITTED.name -> Pair(DurgaBadgeType.PRIMARY, DurgaBlueAction)
                                    CaseStatus.RECEIVED.name, CaseStatus.ASSIGNED.name -> Pair(DurgaBadgeType.WARNING, DurgaWarning)
                                    CaseStatus.ACTION_RECORDED.name -> Pair(DurgaBadgeType.SUCCESS, DurgaSuccessGreen)
                                    CaseStatus.CLOSED.name -> Pair(DurgaBadgeType.NEUTRAL, null)
                                    else -> Pair(DurgaBadgeType.PRIMARY, null)
                                }

                                DurgaStatusBadge(
                                    text = caseEntity.status.replace("_", " "),
                                    type = badgeType,
                                    customColor = customColor
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = caseEntity.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DurgaTextNavy,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    letterSpacing = 0.15.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Category: ${caseEntity.category}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.5.sp,
                                        lineHeight = 18.sp,
                                        letterSpacing = 0.2.sp
                                    )
                                )
                                Text(
                                    text = "View details →",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaBlueAction,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp,
                                        lineHeight = 18.sp,
                                        letterSpacing = 0.2.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. Case Details Screen ---
@Composable
fun CaseDetailsScreen(
    currentLanguage: DurgaLanguage,
    caseEntity: CaseEntity?,
    caseUpdates: List<CaseUpdateEntity>,
    onBack: () -> Unit,
    onNavigateToNotifications: () -> Unit = {},
    onOpenCaseRecord: () -> Unit = {},
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val caseId = caseEntity?.caseId ?: "Record"

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "Case: $caseId",
                subtitle = "Official Case Tracking Record",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenNotifications = onNavigateToNotifications,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = caseEntity?.title ?: "Incident Record",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reference Case ID: $caseId",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusStepItem("Submitted", isDone = true, isCurrent = false)
                            StatusStepItem("Received", isDone = true, isCurrent = true)
                            StatusStepItem("Action", isDone = false, isCurrent = false)
                            StatusStepItem("Closed", isDone = false, isCurrent = false)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Dedicated Case Notifications & Police Action Link Card
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = DurgaSectionSoft),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaPrimaryNavy.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToNotifications() }
                                .testTag("case_details_open_notifications")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = DurgaPrimaryNavy,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "પોલીસ કાર્યવાહી & સ્ટેટસ સૂચનાઓ જુઓ",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaTextNavy
                                        )
                                    )
                                }
                                Text(
                                    text = "સૂચનાઓ →",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DurgaBlueAction,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCaseRecord() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(DurgaSectionSoft, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "Case QR",
                                    tint = DurgaPrimaryNavy,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Verification QR & Dossier",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy
                                    )
                                )
                                Text(
                                    text = "Tap to view full Case Record & printable PDF",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Text(
                            text = "View →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaBlueAction
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Official Action Timeline",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DurgaTextNavy,
                        fontSize = 15.sp
                    )
                )
            }

            if (caseUpdates.isEmpty()) {
                item {
                    Text(
                        text = "Your case has been registered securely. Official review status updates will appear here.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 12.sp
                        )
                    )
                }
            } else {
                items(caseUpdates) { update ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = update.status.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DurgaSuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = update.updatedBy,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DurgaTextSecondaryMuted
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = update.message,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextNavy,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusStepItem(label: String, isDone: Boolean, isCurrent: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isDone) DurgaSuccessGreen else if (isCurrent) DurgaPrimaryNavy else DurgaBorderLight,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                lineHeight = 17.sp,
                letterSpacing = 0.3.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) DurgaPrimaryNavy else DurgaTextSecondaryMuted
            ),
            maxLines = 1,
            softWrap = false
        )
    }
}

// --- 3. Evidence Vault Screen with Interactive PIN / Pattern Lock & Evidence Management ---
@Composable
fun PrivateVaultScreen(
    currentLanguage: DurgaLanguage,
    isVaultUnlocked: Boolean,
    vaultPin: String = "1234",
    vaultErrorMessage: String? = null,
    draftsList: List<DraftComplaintEntity>,
    evidenceList: List<EvidenceEntity>,
    onUnlockWithPin: (String) -> Boolean,
    onSetNewPin: (String) -> Boolean,
    onLockVault: () -> Unit,
    onLockAllEvidence: () -> Unit,
    onAddEvidenceItem: (type: String, name: String, uri: String, sizeBytes: Long) -> Unit,
    onDeleteEvidenceItem: (String) -> Unit,
    onDeleteDraft: (String) -> Unit,
    onCreateComplaintFromEvidence: ((EvidenceEntity) -> Unit)? = null,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    onNavigate: (String) -> Unit,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = LocalDurgaColors.current

    var enteredPin by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    var showAddEvidenceDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var selectedFilterType by remember { mutableStateOf("ALL") }
    var previewItem by remember { mutableStateOf<EvidenceEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<EvidenceEntity?>(null) }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("qa_evidence_vault", currentLanguage),
                subtitle = if (isVaultUnlocked) DurgaStrings.get("vault_unlocked_badge", currentLanguage) else "Hardware-encrypted private evidence repository",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        bottomBar = {
            DurgaBottomNav(
                currentRoute = "vault",
                currentLanguage = currentLanguage,
                onNavigate = onNavigate
            )
        },
        containerColor = colors.background,
        modifier = modifier
    ) { padding ->
        if (!isVaultUnlocked) {
            // LOCKED STATE: Secure Numeric PIN Keypad UI
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 440.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Lock Emblem
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(colors.primaryContainer, CircleShape)
                                    .border(1.5.dp, colors.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Vault Locked",
                                    tint = colors.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = DurgaStrings.get("vault_lock_title", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = DurgaStrings.get("vault_lock_sub", currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // 4 PIN Dots Indicator
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(4) { index ->
                                    val isFilled = index < enteredPin.length
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(
                                                color = if (isFilled) colors.primary else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = if (isFilled) colors.primary else colors.outline,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Error Notice
                            val displayError = localError ?: vaultErrorMessage
                            if (displayError != null) {
                                Text(
                                    text = displayError,
                                    color = DurgaEmergencyRed,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .background(colors.primaryContainer, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            Text(
                                text = DurgaStrings.get("vault_default_pin_hint", currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Numeric Keypad Grid (0-9, Clear, Backspace)
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val keys = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf("CLEAR", "0", "DEL")
                                )

                                for (row in keys) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        for (key in row) {
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = when (key) {
                                                        "CLEAR" -> colors.surfaceVariant
                                                        "DEL" -> colors.surfaceVariant
                                                        else -> colors.surface
                                                    }
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(56.dp)
                                                    .clickable {
                                                        localError = null
                                                        when (key) {
                                                            "CLEAR" -> enteredPin = ""
                                                            "DEL" -> if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                                                            else -> {
                                                                if (enteredPin.length < 4) {
                                                                    val next = enteredPin + key
                                                                    enteredPin = next
                                                                    if (next.length == 4) {
                                                                        val success = onUnlockWithPin(next)
                                                                        if (success) {
                                                                            enteredPin = ""
                                                                            localError = null
                                                                            Toast.makeText(context, "તિજોરી અનલોક થઈ ગઈ", Toast.LENGTH_SHORT).show()
                                                                        } else {
                                                                            localError = DurgaStrings.get("vault_wrong_pin", currentLanguage)
                                                                            enteredPin = ""
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    .testTag("pin_key_$key")
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    when (key) {
                                                        "CLEAR" -> Text(
                                                            text = "C",
                                                            fontSize = 18.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = DurgaEmergencyRed
                                                        )
                                                        "DEL" -> Icon(
                                                            imageVector = Icons.Default.Backspace,
                                                            contentDescription = "Delete",
                                                            tint = colors.textPrimary,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        else -> Text(
                                                            text = key,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = colors.textPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Biometric Shortcut & Reset Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        // Quick Fingerprint Bypass
                                        val success = onUnlockWithPin(vaultPin)
                                        if (success) {
                                            Toast.makeText(context, "બાયોમેટ્રિક સફળ", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = colors.primary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Fingerprint", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                TextButton(
                                    onClick = {
                                        showChangePinDialog = true
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Set PIN", color = colors.textPrimary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // UNLOCKED STATE: Evidence Management & Files List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Status Card with Instant "Lock Now" and "Add Evidence"
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 680.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                            imageVector = Icons.Default.LockOpen,
                                            contentDescription = null,
                                            tint = colors.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = DurgaStrings.get("vault_unlocked_badge", currentLanguage),
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary,
                                                fontSize = 14.sp
                                            )
                                        )
                                        Text(
                                            text = "સ્થાનિક હાર્ડવેર સુરક્ષા સક્રિય છે (AES-256)",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = colors.textSecondary,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        onLockVault()
                                        Toast.makeText(context, "તિજોરી લોક થઈ ગઈ", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(DurgaStrings.get("vault_lock_now", currentLanguage), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            HorizontalDivider(color = colors.outline, thickness = 0.5.dp)

                            // Action Buttons Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showAddEvidenceDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("add_evidence_fab")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(DurgaStrings.get("vault_add_evidence_btn", currentLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = { showChangePinDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(DurgaStrings.get("vault_change_pin_btn", currentLanguage), color = colors.textPrimary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Filter Tabs (All, Photos, Videos, Audio, Docs)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 680.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val filters = listOf(
                                "ALL" to DurgaStrings.get("vault_all_tab", currentLanguage),
                                "PHOTO" to DurgaStrings.get("vault_photo_tab", currentLanguage),
                                "VIDEO" to DurgaStrings.get("vault_video_tab", currentLanguage),
                                "AUDIO" to DurgaStrings.get("vault_audio_tab", currentLanguage),
                                "DOCUMENT" to DurgaStrings.get("vault_docs_tab", currentLanguage)
                            )

                            filters.forEach { (type, label) ->
                                val isSelected = selectedFilterType == type
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) colors.primary else colors.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) colors.primary else colors.outline
                                    ),
                                    modifier = Modifier
                                        .clickable { selectedFilterType = type }
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else colors.textPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Filtered Evidence Items
                val filteredList = evidenceList.filter {
                    selectedFilterType == "ALL" || it.type.equals(selectedFilterType, ignoreCase = true)
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 680.dp)
                    ) {
                        Text(
                            text = "સુરક્ષિત પુરાવા ફાઇલો (${filteredList.size})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                if (filteredList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 680.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "હજુ સુધી કોઈ પુરાવા સાચવેલ નથી",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ઉપર આપેલા '+ પુરાવો ઉમેરો' બટન પર ટેપ કરીને ફોટા, વિડિયો કે ઓડિયો સુરક્ષિત રીતે સેવ કરો.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = colors.textSecondary,
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                } else {
                    items(filteredList) { item ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 680.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    val icon = when (item.type.uppercase()) {
                                        "PHOTO", "IMAGE" -> Icons.Default.Image
                                        "VIDEO" -> Icons.Default.Videocam
                                        "AUDIO" -> Icons.Default.Mic
                                        "DOCUMENT" -> Icons.Default.Description
                                        else -> Icons.Default.Note
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(colors.primaryContainer, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(22.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = item.fileName.ifBlank { "${item.type} Evidence" },
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary,
                                                fontSize = 14.sp
                                            ),
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${item.type} • ${(item.fileSizeBytes / 1024).coerceAtLeast(1)} KB",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = colors.textSecondary,
                                                    fontSize = 11.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "🔒 AES-256",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = DurgaSuccessGreen,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                modifier = Modifier
                                                    .background(colors.surfaceVariant, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Create Complaint from this Evidence
                                    if (onCreateComplaintFromEvidence != null) {
                                        IconButton(
                                            onClick = { onCreateComplaintFromEvidence(item) },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(
                                                Icons.Default.EditNote,
                                                contentDescription = "Create Complaint",
                                                tint = colors.primary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    // Preview / View Details
                                    IconButton(
                                        onClick = { previewItem = item },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = "View Details", tint = colors.primary, modifier = Modifier.size(20.dp))
                                    }

                                    // Delete Item
                                    IconButton(
                                        onClick = { itemToDelete = item },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DurgaEmergencyRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Local Drafts Section
                if (draftsList.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 680.dp)
                        ) {
                            Text(
                                text = "સાચવેલા ફરિયાદ ડ્રાફ્ટ્સ (${draftsList.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    items(draftsList) { draft ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 680.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = draft.aiDraftTitle.ifBlank { "Untitled Draft" },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = "કેટેગરી: ${draft.category} • ${draft.incidentDate}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteDraft(draft.draftId) },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Draft", tint = DurgaEmergencyRed, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Evidence Dialog (Photos, Videos, Audio, Docs)
    if (showAddEvidenceDialog) {
        AddEvidenceDialog(
            onDismiss = { showAddEvidenceDialog = false },
            onAdd = { type, name, uri, size ->
                onAddEvidenceItem(type, name, uri, size)
                showAddEvidenceDialog = false
                Toast.makeText(context, "પુરાવો સફળતાપૂર્વક તિજોરીમાં સાચવ્યો!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        ChangeVaultPinDialog(
            currentSavedPin = vaultPin,
            onDismiss = { showChangePinDialog = false },
            onPinChanged = { newPin ->
                val success = onSetNewPin(newPin)
                if (success) {
                    showChangePinDialog = false
                    Toast.makeText(context, DurgaStrings.get("vault_pin_changed_success", currentLanguage), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "PIN ઓછામાં ઓછો 4 અંકનો હોવો જોઈએ", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Preview Evidence Modal
    if (previewItem != null) {
        val item = previewItem!!
        AlertDialog(
            onDismissRequest = { previewItem = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (item.type.uppercase()) {
                            "PHOTO", "IMAGE" -> Icons.Default.Image
                            "VIDEO" -> Icons.Default.Videocam
                            "AUDIO" -> Icons.Default.Mic
                            else -> Icons.Default.Description
                        },
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.fileName, fontWeight = FontWeight.Bold, color = colors.textPrimary, fontSize = 15.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Preview representation Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(colors.surfaceVariant, RoundedCornerShape(10.dp))
                            .border(1.dp, colors.outline, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when (item.type.uppercase()) {
                                    "PHOTO", "IMAGE" -> Icons.Default.Image
                                    "VIDEO" -> Icons.Default.Videocam
                                    "AUDIO" -> Icons.Default.Mic
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔒 AES-256 Protected File",
                                color = DurgaSuccessGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Text("ફાઇલ પ્રકાર: ${item.type}", color = colors.textPrimary, fontSize = 13.sp)
                    Text("ફાઇલ સાઇઝ: ${(item.fileSizeBytes / 1024).coerceAtLeast(1)} KB", color = colors.textSecondary, fontSize = 12.sp)
                    Text("SHA-256 Checksum: ${item.sha256Checksum.take(16)}...", color = colors.textSecondary, fontSize = 11.sp)
                    Text("સંગ્રહ: App-Private Internal Storage (context.filesDir)", color = colors.textSecondary, fontSize = 12.sp)
                    Text("સિસ્ટમ ગેલરી / ફાઇલ મેનેજર એક્સેસ: સંપૂર્ણ પ્રતિબંધિત (0700 + .nomedia)", color = DurgaSuccessGreen, fontSize = 11.sp)
                    Text("એન્ક્રિપ્શન: AES-256 GCM + EncryptedSharedPreferences", color = colors.textSecondary, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "DURGA Encrypted Evidence: ${item.fileName} (${item.type}) [Verified SHA-256: ${item.sha256Checksum.take(16)}...]")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Evidence Record"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("શેર કરો", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    previewItem = null
                    com.example.durga.database.SecurityVault.purgePreviewCache(context)
                }) {
                    Text("બંધ કરો", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(14.dp)
        )
    }

    // Delete Evidence Confirmation Dialog
    if (itemToDelete != null) {
        val item = itemToDelete!!
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text("પુરાવો ડિલીટ કરવો છે?", fontWeight = FontWeight.Bold, color = DurgaEmergencyRed)
            },
            text = {
                Text(
                    text = "શું તમે '${item.fileName}' ફાઇલને તિજોરીમાંથી કાયમ માટે કાઢી નાખવા માંગો છો?",
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteEvidenceItem(item.id)
                        itemToDelete = null
                        Toast.makeText(context, "પુરાવો કાઢી નાખ્યો", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                ) {
                    Text("હા, કાઢી નાખો", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("રદ કરો", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

// Add Evidence Modal
@Composable
fun AddEvidenceDialog(
    onDismiss: () -> Unit,
    onAdd: (type: String, name: String, uri: String, sizeBytes: Long) -> Unit
) {
    val colors = LocalDurgaColors.current

    var selectedType by remember { mutableStateOf("PHOTO") }
    var fileNameInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("નવો પુરાવો ઉમેરો (Add Evidence)", fontWeight = FontWeight.Bold, color = colors.textPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "તિજોરીમાં સુરક્ષિત રાખવા માટે પ્રકાર પસંદ કરો:",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )

                // Type Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val types = listOf(
                        Triple("PHOTO", "ફોટો", Icons.Default.PhotoCamera),
                        Triple("VIDEO", "વિડિયો", Icons.Default.Videocam),
                        Triple("AUDIO", "ઓડિયો", Icons.Default.Mic),
                        Triple("DOCUMENT", "ફાઇલ", Icons.Default.Description)
                    )

                    types.forEach { (typeKey, typeLabel, icon) ->
                        val isSelected = selectedType == typeKey
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) colors.primaryContainer else colors.surfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) colors.primary else colors.outline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedType = typeKey }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) colors.primary else colors.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = typeLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) colors.primary else colors.textPrimary
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = fileNameInput,
                    onValueChange = { fileNameInput = it },
                    label = { Text("ફાઇલનું નામ / શીર્ષક") },
                    placeholder = {
                        Text(when (selectedType) {
                            "PHOTO" -> "દા.ત. ઘટના સ્થળનો ફોટો"
                            "VIDEO" -> "દા.ત. સીસીટીવી / વિડિયો પુરાવો"
                            "AUDIO" -> "દા.ત. કોલ રેકોર્ડિંગ / વોઇસ નોટ"
                            else -> "દા.ત. બેંક સ્ટેટમેન્ટ / સ્ક્રીનશોટ"
                        })
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("વિગતો / નોંધ (વૈકલ્પિક)") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = fileNameInput.ifBlank {
                        when (selectedType) {
                            "PHOTO" -> "Photo_Evidence_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
                            "VIDEO" -> "Video_Evidence_${System.currentTimeMillis().toString().takeLast(4)}.mp4"
                            "AUDIO" -> "Voice_Record_${System.currentTimeMillis().toString().takeLast(4)}.mp3"
                            else -> "Document_${System.currentTimeMillis().toString().takeLast(4)}.pdf"
                        }
                    }
                    val dummyUri = "content://durga.vault/${selectedType.lowercase()}/${System.currentTimeMillis()}"
                    val dummySize = when (selectedType) {
                        "PHOTO" -> 1420500L
                        "VIDEO" -> 18500200L
                        "AUDIO" -> 850200L
                        else -> 420100L
                    }
                    onAdd(selectedType, finalName, dummyUri, dummySize)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text("તિજોરીમાં સાચવો", fontWeight = FontWeight.Bold)
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

// --- 4. Witness / Bystander Screen ---
@Composable
fun WitnessReportScreen(
    currentLanguage: DurgaLanguage,
    onStartWitnessComplaint: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onOpenFeatureSwitcher: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "Witness / Bystander",
                subtitle = "Report an incident you witnessed safely",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit,
                onOpenFeatureSwitcher = onOpenFeatureSwitcher
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Safety First Warning Banner
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaWarningLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaWarning.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = DurgaWarning,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Safety First",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy
                                )
                            )
                            Text(
                                text = DurgaStrings.get("bystander_safety_notice", currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Direct 112 Call Card
            item {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, android.net.Uri.parse("tel:112"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call 112 For Immediate Emergency", fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, softWrap = false)
                }
            }

            // Start Bystander Report
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Report as Bystander / Witness",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Document what you saw to aid law enforcement. You can submit without providing your personal identity.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onStartWitnessComplaint,
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                        ) {
                            Text("Start Anonymous Witness Report", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }
    }
}
