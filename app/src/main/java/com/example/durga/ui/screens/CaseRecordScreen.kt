package com.example.durga.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseQrEntity
import com.example.durga.model.CaseTimelineEntity
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.VerifiedCaseRecord
import com.example.durga.ui.components.DurgaStatusBadge
import com.example.durga.ui.components.DurgaBadgeType
import com.example.durga.ui.components.DurgaTopBar
import com.example.durga.util.CaseQrHelper
import com.example.durga.util.CaseRecordPdfGenerator
import com.example.durga.util.DurgaDateTimeUtil
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
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted
import com.example.ui.theme.DurgaWarning
import com.example.ui.theme.DurgaWarningLight

@Composable
fun CaseRecordScreen(
    currentLanguage: DurgaLanguage,
    verifiedRecord: VerifiedCaseRecord?,
    fallbackCaseEntity: CaseEntity? = null,
    fallbackUpdates: List<CaseUpdateEntity> = emptyList(),
    fallbackEvidence: List<EvidenceEntity> = emptyList(),
    caseQrRecord: CaseQrEntity? = null,
    caseTimelineEvents: List<CaseTimelineEntity> = emptyList(),
    onAddTimelineEvent: (eventType: String, title: String, description: String, officerName: String) -> Unit = { _, _, _, _ -> },
    onUpdateQrStatus: (newStatus: String) -> Unit = {},
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToMyCases: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {},
    onLanguageChange: (DurgaLanguage) -> Unit = {},
    onQuickExit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current

    // Resolve unified view data between VerifiedCaseRecord or CaseEntity
    val caseId = verifiedRecord?.caseId ?: fallbackCaseEntity?.caseId ?: "DURGA-2026-UNKNOWN"
    val status = verifiedRecord?.status ?: fallbackCaseEntity?.status ?: "Submitted"
    val category = verifiedRecord?.category ?: fallbackCaseEntity?.category ?: "Safety Incident"
    val title = verifiedRecord?.title ?: fallbackCaseEntity?.title ?: "Incident Report"
    val summary = verifiedRecord?.summary ?: fallbackCaseEntity?.structuredDescription?.ifBlank { fallbackCaseEntity.originalDescription } ?: "Complaint details filed securely on record."
    
    val incidentDateInput = verifiedRecord?.incidentDate ?: fallbackCaseEntity?.incidentDate ?: ""
    val incidentTimeInput = verifiedRecord?.incidentTime ?: fallbackCaseEntity?.incidentTime ?: ""
    val incidentLocation = (verifiedRecord?.incidentLocation ?: fallbackCaseEntity?.incidentLocation)?.takeIf { it.isNotBlank() } ?: "Not provided"

    val submittedEpoch = verifiedRecord?.submittedAt ?: fallbackCaseEntity?.createdAt ?: System.currentTimeMillis()
    val submittedDt = remember(submittedEpoch) { DurgaDateTimeUtil.formatEpochMillis(submittedEpoch) }
    val incidentDt = remember(incidentDateInput, incidentTimeInput) {
        DurgaDateTimeUtil.parseIncidentDateTime(incidentDateInput, incidentTimeInput)
    }

    val policeStation = verifiedRecord?.policeStation ?: fallbackCaseEntity?.policeStation?.takeIf { it.isNotBlank() } ?: "Not assigned yet"
    val district = verifiedRecord?.district ?: fallbackCaseEntity?.policeDistrict?.takeIf { it.isNotBlank() } ?: "Not assigned yet"
    val assignedInspector = verifiedRecord?.assignedInspector ?: fallbackCaseEntity?.assignedInspector?.takeIf { it.isNotBlank() } ?: "Not assigned yet"
    val investigatingOfficer = verifiedRecord?.investigatingOfficer ?: fallbackCaseEntity?.investigatingOfficer?.takeIf { it.isNotBlank() } ?: "Not assigned yet"

    val photoCount = verifiedRecord?.photoCount ?: fallbackEvidence.count { it.type.contains("PHOTO", true) }
    val videoCount = verifiedRecord?.videoCount ?: fallbackEvidence.count { it.type.contains("VIDEO", true) }
    val audioCount = verifiedRecord?.audioCount ?: fallbackEvidence.count { it.type.contains("AUDIO", true) }
    val docCount = verifiedRecord?.documentCount ?: fallbackEvidence.count { it.type.contains("DOC", true) || it.type.contains("SCREENSHOT", true) }

    val secureToken = verifiedRecord?.secureToken ?: fallbackCaseEntity?.secureToken ?: caseId
    val qrPayload = remember(secureToken) { CaseQrHelper.buildSecureQrPayload(secureToken) }
    val qrBitmap: Bitmap? = remember(qrPayload) { CaseQrHelper.generateQrBitmap(qrPayload, sizePixels = 420) }

    val currentQrStatus = caseQrRecord?.qrStatus
        ?: verifiedRecord?.qrStatus
        ?: if (verifiedRecord?.isQrActive == false) "REVOKED" else "ACTIVE"

    var selectedEventType by remember { mutableStateOf("STATEMENT_RECORDED") }
    var eventTitleText by remember { mutableStateOf("Statement Recorded") }
    var eventDescriptionText by remember { mutableStateOf("") }
    var officerActorName by remember {
        mutableStateOf(investigatingOfficer.takeIf { it != "Not assigned yet" } ?: "Insp. R. Sharma (POL-GJ-4029)")
    }
    var eventSuccessMessage by remember { mutableStateOf("") }
    var showPolicePhrasesDropdown by remember { mutableStateOf(false) }

    val standardPolicePhrases = listOf(
        "First response dispatched to incident location for spot verification.",
        "Complainant contacted via registered phone; official statement recorded.",
        "Patrol unit mobilized in the sector; heightened vigilance initiated.",
        "Digital evidence secured; CCTV surveillance footage requisitioned.",
        "Person of interest identified; summoned to station for formal interrogation.",
        "Medical examination and psychological counseling coordinated for victim.",
        "On-site dispute de-escalated; mutual undertaking recorded with warning.",
        "Formal FIR registered under statutory women safety provisions.",
        "Site inspection completed; area declared secure with no ongoing threat.",
        "Case dossier transferred to Specialized Women Safety Cell for further inquiry."
    )

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "DURGA – CASE RECORD",
                subtitle = "Case ID: $caseId",
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
            // 1. TOP HEADER & OFFICIAL BADGE
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DURGA OFFICIAL CASE DOSSIER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = caseId,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = DurgaPrimaryNavy,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                )
                            }

                            DurgaStatusBadge(
                                text = status.replace("_", " "),
                                type = when {
                                    status.contains("CLOSED", true) || status.contains("RESOLVED", true) -> DurgaBadgeType.SUCCESS
                                    status.contains("ASSIGN", true) || status.contains("ACTION", true) -> DurgaBadgeType.INFO
                                    else -> DurgaBadgeType.NEUTRAL
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = DurgaBorderLight, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Complaint Category", style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp))
                                Text(category, style = MaterialTheme.typography.bodyMedium.copy(color = DurgaTextNavy, fontWeight = FontWeight.Bold))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Filing Jurisdiction", style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp))
                                Text("Gujarat, India", style = MaterialTheme.typography.bodyMedium.copy(color = DurgaTextNavy, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // 2. SECURE CASE QR SECTION
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, DurgaPrimaryNavy.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SECURE CASE QR CODE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Opaque token reference • No private personal data encoded",
                            style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (qrBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, DurgaBorderLight, RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "Secure Case QR Code for $caseId",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(170.dp)
                                    .background(DurgaSectionSoft, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(64.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "QR Reference: /case/secure/${secureToken.take(16)}...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 10.5.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (qrBitmap != null) {
                                        CaseQrHelper.shareQrCode(context, qrBitmap, caseId)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(44.dp).minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save QR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    CaseQrHelper.printOrShareFieldQrPass(
                                        context = context,
                                        caseId = caseId,
                                        token = secureToken,
                                        title = title,
                                        category = category,
                                        status = status,
                                        officerName = investigatingOfficer.takeIf { it != "Not assigned yet" } ?: "Duty Officer"
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaPrimaryNavy),
                                modifier = Modifier.weight(1f).height(44.dp).minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Print Field Pass", color = DurgaPrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onNavigateToScanner,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier.fillMaxWidth().height(40.dp).minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Another QR Code", color = DurgaPrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }
                }
            }

            // 3. INCIDENT INFORMATION SECTION
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "INCIDENT INFORMATION",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(label = "Incident Date:", value = incidentDt.dateString)
                        DetailRow(label = "Incident Day:", value = incidentDt.dayName)
                        DetailRow(label = "Incident Time:", value = incidentDt.timeString)
                        DetailRow(label = "Incident Location:", value = incidentLocation)
                    }
                }
            }

            // 4. COMPLAINT INFORMATION SECTION
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "COMPLAINT INFORMATION",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(label = "Complaint Filed On:", value = submittedDt.dateString)
                        DetailRow(label = "Complaint Filed Day:", value = submittedDt.dayName)
                        DetailRow(label = "Complaint Filed Time:", value = submittedDt.timeString)
                        DetailRow(label = "Complaint Title:", value = title)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Complaint Summary:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DurgaSectionSoft, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = summary,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextNavy,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            // 5. POLICE / AUTHORITY INFORMATION (Strict: No Fake Names)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "POLICE & AUTHORITY INFORMATION",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaPrimaryNavy,
                                    fontSize = 13.sp
                                )
                            )
                            Icon(Icons.Default.Shield, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        DetailRow(label = "Police Station:", value = policeStation)
                        DetailRow(label = "District:", value = district)
                        DetailRow(label = "Assigned Inspector:", value = assignedInspector)
                        DetailRow(label = "Officer / Investigator:", value = investigatingOfficer)
                        DetailRow(
                            label = "Assignment Status:",
                            value = if (policeStation != "Not assigned yet") "Official Assignment Active" else "Pending Station Triage"
                        )
                    }
                }
            }

            // 6. EVIDENCE INFORMATION (Safe Metadata Summary)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "EVIDENCE REPOSITORY (SAFE METADATA)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Evidence Attached: $photoCount Photos • $videoCount Video • $audioCount Audio • $docCount Documents",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DurgaTextNavy,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stored inside Private Evidence Vault with SHA-256 seal. Cryptographic references protected from public QR extraction.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // 6B. INVESTIGATION ACTION DOSSIER SECTION
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, DurgaPrimaryNavy.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("investigation_action_section")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Section Header with Title and Visual QR Status Badge (Active/Revoked/Expired)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "INVESTIGATION ACTION",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaPrimaryNavy,
                                        letterSpacing = 0.5.sp,
                                        fontSize = 14.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Official Dossier Dispatch & Timeline Updates",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Visual 'QR Status' badge pulling from case_qr database model
                            QrStatusBadge(status = currentQrStatus)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DurgaBorderLight, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Security Reference Banner & Status Management
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DurgaSectionSoft, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Case QR Record Status (case_qr):",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DurgaTextSecondaryMuted,
                                        fontSize = 10.5.sp
                                    )
                                )
                                Text(
                                    text = "State: $currentQrStatus • Token: ${secureToken.take(16)}...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        color = DurgaTextNavy,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val nextStatus = if (currentQrStatus == "ACTIVE") "REVOKED" else "ACTIVE"
                                    onUpdateQrStatus(nextStatus)
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (currentQrStatus == "ACTIVE") DurgaEmergencyRed else DurgaSuccessGreen
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (currentQrStatus == "ACTIVE") DurgaEmergencyRed else DurgaSuccessGreen
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("toggle_qr_status_button")
                            ) {
                                Text(
                                    text = if (currentQrStatus == "ACTIVE") "Deactivate QR" else "Activate QR",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- Field Group: Manually add an 'Event' to case_timeline ---
                        Text(
                            text = "MANUAL EVENT ENTRY (case_timeline)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = "Add verified investigation milestones, evidence custody, or inspection notes to the real-time timeline.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Type Selector
                        Text(
                            text = "Select Event Type:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val eventTypes = listOf(
                            "STATEMENT_RECORDED" to "Statement Recorded",
                            "EVIDENCE_COLLECTED" to "Evidence Collected",
                            "POLICE_DISPATCH" to "Police Dispatched",
                            "SITE_INSPECTION" to "Site Inspection",
                            "SUSPECT_INTERROGATED" to "Suspect Inquiry",
                            "MEDICAL_EXAM" to "Medical Exam",
                            "ACTION_TAKEN" to "Action Taken"
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(eventTypes) { (typeKey, typeLabel) ->
                                val isSelected = selectedEventType == typeKey
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedEventType = typeKey
                                        if (eventTitleText.isBlank() || eventTypes.any { it.second == eventTitleText }) {
                                            eventTitleText = typeLabel
                                        }
                                    },
                                    label = { Text(typeLabel, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = DurgaPrimaryNavy,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Event Title Field
                        OutlinedTextField(
                            value = eventTitleText,
                            onValueChange = { eventTitleText = it },
                            label = { Text("Event Title / Milestone Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("timeline_event_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DurgaPrimaryNavy,
                                unfocusedBorderColor = DurgaBorderLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Event Description Field / Officer Dispatch Notes Header with Dropdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Event Description / Officer Dispatch Notes *",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy
                                )
                            )

                            Box {
                                OutlinedButton(
                                    onClick = { showPolicePhrasesDropdown = true },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .defaultMinSize(minHeight = 28.dp)
                                        .testTag("standard_police_phrases_dropdown_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = DurgaPrimaryNavy
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Standard Phrases",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaPrimaryNavy
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = DurgaPrimaryNavy
                                    )
                                }

                                DropdownMenu(
                                    expanded = showPolicePhrasesDropdown,
                                    onDismissRequest = { showPolicePhrasesDropdown = false }
                                ) {
                                    standardPolicePhrases.forEach { phrase ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = phrase,
                                                    fontSize = 12.sp,
                                                    lineHeight = 15.sp,
                                                    color = DurgaTextNavy
                                                )
                                            },
                                            onClick = {
                                                eventDescriptionText = phrase
                                                showPolicePhrasesDropdown = false
                                            },
                                            modifier = Modifier.testTag("case_record_phrase_${phrase.take(12).replace(" ", "_")}")
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Event Description Field
                        OutlinedTextField(
                            value = eventDescriptionText,
                            onValueChange = { eventDescriptionText = it },
                            placeholder = { Text("Enter detailed findings, officer actions, or select standard police phrase above...", fontSize = 11.sp) },
                            minLines = 2,
                            maxLines = 4,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("timeline_event_description_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DurgaPrimaryNavy,
                                unfocusedBorderColor = DurgaBorderLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Officer / Actor Name Field
                        OutlinedTextField(
                            value = officerActorName,
                            onValueChange = { officerActorName = it },
                            label = { Text("Recording Officer Name / Badge ID", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("timeline_officer_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DurgaPrimaryNavy,
                                unfocusedBorderColor = DurgaBorderLight
                            )
                        )

                        if (eventSuccessMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DurgaSuccessLight, RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = DurgaSuccessGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = eventSuccessMessage,
                                    color = DurgaSuccessGreen,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val desc = eventDescriptionText.trim()
                                if (desc.isNotBlank()) {
                                    val finalTitle = eventTitleText.ifBlank { selectedEventType.replace("_", " ") }
                                    onAddTimelineEvent(selectedEventType, finalTitle, desc, officerActorName)
                                    eventDescriptionText = ""
                                    eventSuccessMessage = "✓ Event added to case_timeline! Live Case Record updated in real-time."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                            shape = RoundedCornerShape(8.dp),
                            enabled = eventDescriptionText.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("submit_timeline_event_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Post Event to Case Timeline", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 7. LIVE CASE TIMELINE
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CASE TIMELINE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Event 1: Created & Stored
                        TimelineEntry(
                            title = "Complaint Created",
                            dateTime = submittedDt.fullDisplay,
                            isFirst = true,
                            isLast = verifiedRecord?.timeline.isNullOrEmpty() && fallbackUpdates.isEmpty() && caseTimelineEvents.isEmpty()
                        )

                        // Event 2: Submitted
                        TimelineEntry(
                            title = "Complaint Submitted",
                            dateTime = submittedDt.fullDisplay,
                            isFirst = false,
                            isLast = verifiedRecord?.timeline.isNullOrEmpty() && fallbackUpdates.isEmpty() && caseTimelineEvents.isEmpty()
                        )

                        // Real-time events from case_timeline Room database model
                        if (caseTimelineEvents.isNotEmpty()) {
                            caseTimelineEvents.forEach { ev ->
                                val dt = DurgaDateTimeUtil.formatEpochMillis(ev.timestamp)
                                TimelineEntry(
                                    title = "[${ev.eventType.replace("_", " ")}] ${ev.title} (${ev.actorName})",
                                    description = ev.description,
                                    dateTime = dt.fullDisplay,
                                    isFirst = false,
                                    isLast = false
                                )
                            }
                        }

                        // Verified timeline events from backend or local updates
                        if (!verifiedRecord?.timeline.isNullOrEmpty()) {
                            verifiedRecord!!.timeline.forEachIndexed { idx, ev ->
                                val dt = DurgaDateTimeUtil.formatEpochMillis(ev.timestamp)
                                TimelineEntry(
                                    title = "${ev.title} (${ev.actorName})",
                                    description = ev.description,
                                    dateTime = dt.fullDisplay,
                                    isFirst = false,
                                    isLast = idx == verifiedRecord.timeline.lastIndex
                                )
                            }
                        } else if (fallbackUpdates.isNotEmpty()) {
                            fallbackUpdates.forEachIndexed { idx, up ->
                                val dt = DurgaDateTimeUtil.formatEpochMillis(up.timestamp)
                                TimelineEntry(
                                    title = "${up.status.replace("_", " ")} — ${up.updatedBy}",
                                    description = up.message,
                                    dateTime = dt.fullDisplay,
                                    isFirst = false,
                                    isLast = idx == fallbackUpdates.lastIndex
                                )
                            }
                        } else if (caseTimelineEvents.isEmpty()) {
                            TimelineEntry(
                                title = "Desk Review Active",
                                description = "Case lodged in central triage vault. Official authority updates will appear as investigation proceeds.",
                                dateTime = submittedDt.fullDisplay,
                                isFirst = false,
                                isLast = true
                            )
                        }
                    }
                }
            }

            // 8. ACTION BUTTONS: DOWNLOAD PDF & DIRECT NAVIGATION
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val dummyEntity = fallbackCaseEntity ?: CaseEntity(
                                caseId = caseId,
                                category = category,
                                title = title,
                                originalDescription = summary,
                                structuredDescription = summary,
                                originalLanguage = "en",
                                incidentDate = incidentDateInput,
                                incidentTime = incidentTimeInput,
                                incidentLocation = incidentLocation,
                                status = status,
                                policeStation = policeStation,
                                policeDistrict = district,
                                assignedInspector = assignedInspector,
                                investigatingOfficer = investigatingOfficer,
                                secureToken = secureToken,
                                createdAt = submittedEpoch
                            )
                            val pdfFile = CaseRecordPdfGenerator.generateCaseRecordPdf(
                                context = context,
                                caseEntity = dummyEntity,
                                caseUpdates = fallbackUpdates,
                                evidenceList = fallbackEvidence
                            )
                            if (pdfFile != null) {
                                CaseRecordPdfGenerator.openOrSharePdf(context, pdfFile)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("download_case_record_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DOWNLOAD CASE RECORD (PDF)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToMyCases,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier.weight(1f).height(46.dp).minimumInteractiveComponentSize()
                        ) {
                            Text("View My Cases", color = DurgaTextNavy, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onNavigateToHome,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                            modifier = Modifier.weight(1f).height(46.dp).minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = DurgaTextNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Return Home", color = DurgaTextNavy, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DurgaTextSecondaryMuted,
                fontSize = 12.sp
            ),
            modifier = Modifier.weight(0.42f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DurgaTextNavy,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.58f)
        )
    }
}

@Composable
private fun TimelineEntry(
    title: String,
    dateTime: String,
    description: String? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(DurgaPrimaryNavy, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(38.dp)
                        .background(DurgaBorderLight)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "● $title",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DurgaTextNavy,
                    fontSize = 12.5.sp
                )
            )
            Text(
                text = dateTime,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DurgaTextSecondaryMuted,
                    fontSize = 11.sp
                )
            )
            if (!description.isNullOrBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DurgaTextNavy.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
fun QrStatusBadge(status: String, modifier: Modifier = Modifier) {
    val upperStatus = status.uppercase()
    val isRevoked = upperStatus == "REVOKED"
    val isExpired = upperStatus == "EXPIRED"

    val bg = when {
        isRevoked -> DurgaEmergencyRedLight
        isExpired -> DurgaWarningLight
        else -> DurgaSuccessLight
    }
    val borderClr = when {
        isRevoked -> DurgaEmergencyRed.copy(alpha = 0.5f)
        isExpired -> DurgaWarning.copy(alpha = 0.5f)
        else -> DurgaSuccessGreen.copy(alpha = 0.5f)
    }
    val textClr = when {
        isRevoked -> DurgaEmergencyRed
        isExpired -> DurgaWarning
        else -> DurgaSuccessGreen
    }
    val icon = when {
        isRevoked -> Icons.Default.Block
        isExpired -> Icons.Default.Schedule
        else -> Icons.Default.CheckCircle
    }

    Row(
        modifier = modifier
            .background(bg, RoundedCornerShape(20.dp))
            .border(1.dp, borderClr, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = textClr, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "QR: $upperStatus",
            color = textClr,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.3.sp
        )
    }
}
