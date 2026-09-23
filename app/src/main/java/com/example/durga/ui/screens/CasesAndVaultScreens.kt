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
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.durga.ui.components.DurgaBottomNav
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
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
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
                onQuickExit = onQuickExit
            )
        },
        bottomBar = {
            DurgaBottomNav(
                currentRoute = "cases",
                currentLanguage = currentLanguage,
                onNavigate = { route ->
                    if (route == "home") onBack()
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

                                val statusColor = when (caseEntity.status) {
                                    CaseStatus.REPORT_SUBMITTED.name -> DurgaBlueAction
                                    CaseStatus.RECEIVED.name, CaseStatus.ASSIGNED.name -> DurgaWarning
                                    CaseStatus.ACTION_RECORDED.name -> DurgaSuccessGreen
                                    CaseStatus.CLOSED.name -> DurgaTextSecondaryMuted
                                    else -> DurgaPrimaryNavy
                                }

                                Text(
                                    text = caseEntity.status.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier
                                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = caseEntity.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DurgaTextNavy,
                                    fontSize = 14.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Category: ${caseEntity.category}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = "View details →",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaBlueAction,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
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
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
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
                onQuickExit = onQuickExit
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
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                                text = "Verification QR Token",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy
                                )
                            )
                            Text(
                                text = "Token: ${caseEntity?.secureToken?.take(16) ?: "PROTECTED"}. No personal identifiers visible.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
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
                fontSize = 10.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) DurgaPrimaryNavy else DurgaTextSecondaryMuted
            )
        )
    }
}

// --- 3. Evidence Vault Screen ---
@Composable
fun PrivateVaultScreen(
    currentLanguage: DurgaLanguage,
    isVaultUnlocked: Boolean,
    draftsList: List<DraftComplaintEntity>,
    evidenceList: List<EvidenceEntity>,
    onUnlockVault: () -> Unit,
    onLockVault: () -> Unit,
    onLockAllEvidence: () -> Unit,
    onDeleteDraft: (String) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("qa_evidence_vault", currentLanguage),
                subtitle = "Hardware-encrypted private evidence repository",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        bottomBar = {
            DurgaBottomNav(
                currentRoute = "evidence",
                currentLanguage = currentLanguage,
                onNavigate = { route ->
                    if (route == "home") onBack()
                }
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        if (!isVaultUnlocked) {
            // Locked State Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(DurgaSectionSoft, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = DurgaPrimaryNavy,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Evidence Vault is Locked",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Files are protected under AES-256 hardware encryption. Tap below to unlock.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onUnlockVault,
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlock with Biometrics / PIN", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Unlocked Evidence List & Controls
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // One-Tap Lock Header
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        modifier = Modifier.fillMaxWidth()
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
                                        .background(DurgaSuccessLight, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = DurgaSuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Vault Unlocked",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaTextNavy
                                        )
                                    )
                                    Text(
                                        text = "AES-256 GCM encrypted",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DurgaTextSecondaryMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = onLockVault,
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaSectionSoft),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text("Lock Now", color = DurgaPrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Evidence Items List
                item {
                    Text(
                        text = "Encrypted Evidence Files (${evidenceList.size})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = 14.sp
                        )
                    )
                }

                if (evidenceList.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No evidence saved yet.",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your files remain locally encrypted on this device and are never uploaded without your explicit choice.",
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
                    items(evidenceList) { item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier.fillMaxWidth()
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
                                        "PHOTO" -> Icons.Default.Image
                                        "AUDIO" -> Icons.Default.Audiotrack
                                        "DOCUMENT" -> Icons.Default.Description
                                        else -> Icons.Default.Note
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(DurgaSectionSoft, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(18.dp))
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = item.type,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DurgaTextNavy,
                                                fontSize = 13.sp
                                            )
                                        )
                                        Text(
                                            text = item.fileName.ifBlank { item.localUri }.take(30),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DurgaTextSecondaryMuted,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            val shareIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "Encrypted Evidence Item: ${item.localUri}")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share Evidence"))
                                        },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = DurgaTextSecondaryMuted, modifier = Modifier.size(18.dp))
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
                        Text(
                            text = "Unsubmitted Local Drafts (${draftsList.size})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = 14.sp
                            )
                        )
                    }

                    items(draftsList) { draft ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier.fillMaxWidth()
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
                                            color = DurgaTextNavy,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = "Category: ${draft.category}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DurgaTextSecondaryMuted,
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
}

// --- 4. Witness / Bystander Screen ---
@Composable
fun WitnessReportScreen(
    currentLanguage: DurgaLanguage,
    onStartWitnessComplaint: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
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
                onQuickExit = onQuickExit
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
                    Text("Call 112 For Immediate Emergency", fontWeight = FontWeight.Bold, color = Color.White)
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
                            Text("Start Anonymous Witness Report", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
