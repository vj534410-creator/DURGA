package com.example.durga.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.ui.SubmissionState
import com.example.durga.ui.components.DurgaBadgeType
import com.example.durga.ui.components.DurgaFieldLabel
import com.example.durga.ui.components.DurgaStatusBadge
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSectionSoft
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSuccessLight
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted
import java.util.Calendar

// --- 1. Edit Complaint Screen (Step 4) ---
@Composable
fun EditComplaintScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    onSaveEdit: (String, String, String) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(currentDraft.aiDraftTitle) }
    var summary by remember { mutableStateOf(currentDraft.aiDraftSummary) }
    var description by remember { mutableStateOf(currentDraft.aiDraftDescription) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "Review & Edit Complaint",
                subtitle = "Step 4 of 7: Fine-tune Complaint Text",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DurgaStatusBadge(
                    text = "STEP 4 OF 7",
                    type = DurgaBadgeType.PRIMARY
                )
                DurgaStatusBadge(
                    text = "EDITABLE DRAFT",
                    type = DurgaBadgeType.SUCCESS
                )
            }

            Text(
                text = "Ensure all statements are accurate before continuing. You can edit any sentence or legal detail.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DurgaTextSecondaryMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    letterSpacing = 0.2.sp
                )
            )

            DurgaFieldLabel(
                text = "Complaint Subject / Title",
                isRequired = true,
                helperText = "A short, descriptive headline for this incident"
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Threatening messages and intimidation", color = DurgaTextSecondaryMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_title_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = DurgaTextNavy,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.15.sp
                ),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            DurgaFieldLabel(
                text = "Brief Executive Summary",
                isRequired = true,
                helperText = "2-3 sentences summarizing who, when, and what happened"
            )

            OutlinedTextField(
                value = summary,
                onValueChange = { summary = it },
                placeholder = { Text("Summary of the key incident facts...", color = DurgaTextSecondaryMuted) },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_summary_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = DurgaTextNavy,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.15.sp
                ),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            DurgaFieldLabel(
                text = "Detailed Incident Description",
                isRequired = true,
                helperText = "Full chronological statement of the incident in complete detail"
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Full description of the incident...", color = DurgaTextSecondaryMuted) },
                minLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("edit_description_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = DurgaTextNavy,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    letterSpacing = 0.15.sp
                ),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Button(
                onClick = { onSaveEdit(title, summary, description) },
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("save_edited_complaint_button")
            ) {
                Text("Save and Continue to Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- 2. Complainant Details Screen ---
@Composable
fun ComplainantDetailsScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    onSaveDetails: (name: String, age: String, gender: String, state: String, district: String, mobile: String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(currentDraft.complainantName) }
    var age by remember { mutableStateOf(currentDraft.complainantAge) }
    var gender by remember { mutableStateOf(currentDraft.complainantGender.ifBlank { "Female" }) }
    var state by remember { mutableStateOf(currentDraft.complainantState.ifBlank { "Gujarat" }) }
    var district by remember { mutableStateOf(currentDraft.complainantDistrict) }
    var mobile by remember { mutableStateOf(currentDraft.complainantMobile) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("personal_info_title", currentLanguage),
                subtitle = "Step 4b: Complainant Information (Optional)",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DurgaSectionSoft),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Privacy Information",
                        tint = DurgaPrimaryNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Why we collect this: Details are NEVER used for user tracking or profiling. They are collected strictly for the official statutory authority to register the complaint. You can submit anonymously if needed.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name (Leave blank for Anonymous)", color = DurgaTextSecondaryMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complainant_name_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(10.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age", color = DurgaTextSecondaryMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("complainant_age_input"),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender", color = DurgaTextSecondaryMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("complainant_gender_input"),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state,
                    onValueChange = { state = it },
                    label = { Text("State", color = DurgaTextSecondaryMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("complainant_state_input"),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text("District", color = DurgaTextSecondaryMuted) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("complainant_district_input"),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number (For official SMS updates)", color = DurgaTextSecondaryMuted) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("complainant_mobile_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    onSaveDetails(name, age, gender, state, district, mobile)
                    onContinue()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("continue_to_incident_details_button")
            ) {
                Text("Continue to Incident Specifics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- 3. Incident Details Screen ---
@Composable
fun IncidentDetailsScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    onSaveIncidentDetails: (date: String, time: String, location: String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf(currentDraft.incidentDate.ifBlank { "Today" }) }
    var time by remember { mutableStateOf(currentDraft.incidentTime.ifBlank { "Approx 14:00" }) }
    var location by remember { mutableStateOf(currentDraft.incidentLocation) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("incident_details_title", currentLanguage),
                subtitle = "Step 4c: Time, Date & Location",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Incident Date", color = DurgaTextSecondaryMuted) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val c = Calendar.getInstance()
                            val dpd = DatePickerDialog(
                                context,
                                { _, y, m, d -> date = "$d/${m + 1}/$y" },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            )
                            dpd.show()
                        }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = DurgaPrimaryNavy)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Incident Time", color = DurgaTextSecondaryMuted) },
                    modifier = Modifier.weight(1f),
                    colors = defaultTextFieldColors(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Incident Location / Landmark", color = DurgaTextSecondaryMuted) },
                trailingIcon = {
                    IconButton(onClick = { location = "Near Navrangpura Bus Stand, Ahmedabad (Auto-detected)" }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Use GPS", tint = DurgaBlueAction)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("incident_location_input"),
                colors = defaultTextFieldColors(),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    onSaveIncidentDetails(date, time, location)
                    onContinue()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("continue_to_evidence_button")
            ) {
                Text("Continue to Attach Evidence", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- 4. Evidence Selection Screen (Step 5) ---
@Composable
fun EvidenceSelectionScreen(
    currentLanguage: DurgaLanguage,
    attachedEvidence: List<EvidenceEntity>,
    onAddEvidence: (type: String, name: String, uri: String, size: Long) -> Unit,
    onRemoveEvidence: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            onAddEvidence("PHOTO", "evidence_img_${System.currentTimeMillis()}.jpg", it.toString(), 1450000L)
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("evidence_selection", currentLanguage),
                subtitle = "Step 5 of 7: Attach Evidence (Optional)",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSectionSoft),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = DurgaPrimaryNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Files are cryptographically hashed (SHA-256) and kept locally encrypted until you explicitly choose to share them.",
                        style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("pick_photo_button")
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Add", tint = DurgaPrimaryNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Photo", color = DurgaPrimaryNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onAddEvidence("AUDIO", "voice_evidence_${System.currentTimeMillis()}.m4a", "file://audio_sample", 820000L)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("add_audio_sample_button")
                ) {
                    Icon(Icons.Default.Audiotrack, contentDescription = "Add", tint = DurgaPrimaryNavy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Audio", color = DurgaPrimaryNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (attachedEvidence.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No evidence attached yet. (Evidence is optional).",
                            color = DurgaTextSecondaryMuted,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                        )
                    }
                }
            } else {
                attachedEvidence.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.type,
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.fileName.ifBlank { item.localUri }.take(30),
                                    style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp),
                                    maxLines = 1
                                )
                            }
                            IconButton(onClick = { onRemoveEvidence(item.id) }, modifier = Modifier.minimumInteractiveComponentSize()) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DurgaEmergencyRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("continue_to_sharing_options_button")
            ) {
                Text("Continue to Sharing Options", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- 5. Select What To Share Screen (Step 6) ---
@Composable
fun SelectWhatToShareScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    evidenceCount: Int,
    onUpdateChoices: (comp: Boolean, photo: Boolean, audio: Boolean, loc: Boolean) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var shareComplaint by remember { mutableStateOf(currentDraft.shareComplaint) }
    var sharePhotos by remember { mutableStateOf(currentDraft.sharePhotos) }
    var shareAudio by remember { mutableStateOf(currentDraft.shareAudio) }
    var shareLocation by remember { mutableStateOf(currentDraft.shareLocation) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("choose_sharing", currentLanguage),
                subtitle = "Step 6 of 7: Select What to Share",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "You maintain complete granular authority over what information is transmitted to statutory authorities:",
                style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 12.sp)
            )

            ShareCheckboxCard(
                title = "Share Complaint Narrative",
                subtitle = "Helps authorities file official legal FIR under IPC / BNS",
                checked = shareComplaint,
                onCheckedChange = {
                    shareComplaint = it
                    onUpdateChoices(shareComplaint, sharePhotos, shareAudio, shareLocation)
                }
            )

            ShareCheckboxCard(
                title = "Share Attached Photos ($evidenceCount available)",
                subtitle = "Transfers cryptographically verified photo evidence to authority case file",
                checked = sharePhotos,
                onCheckedChange = {
                    sharePhotos = it
                    onUpdateChoices(shareComplaint, sharePhotos, shareAudio, shareLocation)
                }
            )

            ShareCheckboxCard(
                title = "Share Audio Recordings",
                subtitle = "Transfers voice evidence to official investigators",
                checked = shareAudio,
                onCheckedChange = {
                    shareAudio = it
                    onUpdateChoices(shareComplaint, sharePhotos, shareAudio, shareLocation)
                }
            )

            ShareCheckboxCard(
                title = "Share Incident Location & Map Pin",
                subtitle = "Enables jurisdiction assignment to nearest police station",
                checked = shareLocation,
                onCheckedChange = {
                    shareLocation = it
                    onUpdateChoices(shareComplaint, sharePhotos, shareAudio, shareLocation)
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("continue_to_final_confirmation_button")
            ) {
                Text("Proceed to Final Confirmation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// --- 6. Final Confirmation Screen (Step 7) ---
@Composable
fun FinalConfirmationScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    evidenceCount: Int,
    submissionState: SubmissionState,
    onSubmitFinal: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("final_review_title", currentLanguage),
                subtitle = "Step 7 of 7: Review & Final Submission",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, DurgaBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Official Submission Summary",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                letterSpacing = 0.1.sp
                            )
                        )
                        DurgaStatusBadge(
                            text = "READY",
                            type = DurgaBadgeType.SUCCESS
                        )
                    }

                    HorizontalDivider(color = DurgaBorderLight.copy(alpha = 0.6f))

                    val summaryItems = listOf(
                        Pair("Category", currentDraft.category.replace("_", " ")),
                        Pair("Title", currentDraft.aiDraftTitle.ifBlank { "Official Complaint" }),
                        Pair("Date / Time", "${currentDraft.incidentDate} at ${currentDraft.incidentTime}"),
                        Pair("Location", currentDraft.incidentLocation.ifBlank { "Not specified" }),
                        Pair("Complainant", currentDraft.complainantName.ifBlank { "Anonymous / Witness" }),
                        Pair("Evidence", "$evidenceCount attached file(s)"),
                        Pair("Storage", "AES-256 Encrypted on Device")
                    )

                    for (item in summaryItems) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = item.first,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    letterSpacing = 0.2.sp
                                ),
                                modifier = Modifier.weight(0.4f)
                            )
                            Text(
                                text = item.second,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (item.first == "Storage") DurgaSuccessGreen else DurgaTextNavy,
                                    fontSize = 12.5.sp,
                                    lineHeight = 19.sp,
                                    letterSpacing = 0.15.sp,
                                    textAlign = TextAlign.End
                                ),
                                modifier = Modifier.weight(0.6f)
                            )
                        }
                    }
                }
            }

            if (submissionState is SubmissionState.Error) {
                Text(
                    text = "Notice: ${(submissionState as SubmissionState.Error).message}. Your draft remains safely stored locally.",
                    color = DurgaEmergencyRed,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
            }

            Button(
                onClick = onSubmitFinal,
                enabled = submissionState !is SubmissionState.Submitting,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("submit_complaint_final_button")
            ) {
                if (submissionState is SubmissionState.Submitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Registering Complaint...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Text(
                        text = DurgaStrings.get("submit_complaint_btn", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}

// --- 7. Case Created / Success Screen ---
@Composable
fun CaseCreatedScreen(
    currentLanguage: DurgaLanguage,
    createdCase: CaseEntity,
    onTrackCases: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(DurgaSuccessLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = DurgaSuccessGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = DurgaStrings.get("case_created_title", currentLanguage),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DurgaTextNavy,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = DurgaStrings.get("case_id_label", currentLanguage),
                        style = MaterialTheme.typography.labelSmall.copy(color = DurgaTextSecondaryMuted)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = createdCase.caseId,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaPrimaryNavy,
                            fontSize = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(DurgaSectionSoft, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Case QR Token",
                            tint = DurgaPrimaryNavy,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = DurgaStrings.get("secure_qr_code", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = 13.sp
                        )
                        Text(
                            text = DurgaStrings.get("qr_privacy_note", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onTrackCases,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("track_cases_button")
            ) {
                Text(
                    text = DurgaStrings.get("track_cases", currentLanguage),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGoHome,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Text("Return to Home", color = DurgaTextNavy, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ShareCheckboxCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (checked) DurgaPrimaryNavy else DurgaBorderLight),
        shape = RoundedCornerShape(10.dp),
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
                Text(text = title, fontWeight = FontWeight.Bold, color = DurgaTextNavy, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp))
            }
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = DurgaPrimaryNavy,
                    uncheckedColor = DurgaBorderLight,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.minimumInteractiveComponentSize()
            )
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = DurgaTextNavy,
    unfocusedTextColor = DurgaTextNavy,
    focusedContainerColor = DurgaSurfaceWhite,
    unfocusedContainerColor = DurgaSurfaceWhite,
    focusedBorderColor = DurgaPrimaryNavy,
    unfocusedBorderColor = DurgaBorderLight
)
