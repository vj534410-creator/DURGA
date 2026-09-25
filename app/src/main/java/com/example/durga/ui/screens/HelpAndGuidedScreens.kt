package com.example.durga.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import com.example.ui.theme.DurgaThemeMode
import com.example.ui.theme.LocalDurgaColors
import com.example.ui.theme.LocalDurgaThemeMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.durga.model.AuditAccessLogEntity
import com.example.durga.model.AuthorityRole
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseStatus
import com.example.durga.model.NearbyPlace
import com.example.durga.ui.components.AuthorityWatermark
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBlueLightBg
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaBorderSubtle
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

// --- 1. Help Near Me Screen ---
@Composable
fun HelpNearMeScreen(
    currentLanguage: DurgaLanguage,
    placesList: List<NearbyPlace>,
    onCallNumber: (String) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf("ALL") }

    val filterOptions = listOf("ALL", "Police", "Hospital", "Women Center", "CWC")

    val filteredPlaces = if (selectedType == "ALL") {
        placesList
    } else {
        placesList.filter { it.type.contains(selectedType, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("qa_nearby_help", currentLanguage),
                subtitle = "Police stations, hospitals & Sakhi centers",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(DurgaBlueLightBg, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NearMe,
                                contentDescription = "Nearby",
                                tint = DurgaBlueAction,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Verified 24/7 Crisis Centers Nearby",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Showing closest official facilities with direct phone and directions.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            // Category filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { filter ->
                        val isSelected = selectedType.equals(filter, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = filter },
                            label = { Text(filter, fontSize = 13.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DurgaPrimaryNavy,
                                selectedLabelColor = Color.White,
                                containerColor = DurgaSurfaceWhite,
                                labelColor = DurgaTextNavy
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DurgaBorderLight,
                                selectedBorderColor = DurgaPrimaryNavy
                            )
                        )
                    }
                }
            }

            items(filteredPlaces) { place ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text(
                                    text = place.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DurgaTextNavy,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Text(
                                text = "${place.distanceKm} km",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaBlueAction,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier
                                    .background(DurgaBlueLightBg, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = place.address,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onCallNumber(place.phoneNumber) },
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaSectionSoft),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = DurgaPrimaryNavy,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call", color = DurgaPrimaryNavy, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    val mapUri = android.net.Uri.parse("geo:${place.latitude},${place.longitude}?q=${android.net.Uri.encode(place.name)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (_: Exception) {}
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Directions,
                                    contentDescription = "Directions",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Directions", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 2. "I Need Help" 6-Step Guided Safety Assistant ---
@Composable
fun IdkWhatToDoScreen(
    currentLanguage: DurgaLanguage,
    onNavigateToEmergency: () -> Unit,
    onNavigateToComplaint: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }

    // User answers for the 6 steps
    var q1IncidentType by remember { mutableStateOf("") }
    var q2ImmediateDanger by remember { mutableStateOf<Boolean?>(null) }
    var q3IsAlone by remember { mutableStateOf<Boolean?>(null) }
    var q4NeedMedical by remember { mutableStateOf<Boolean?>(null) }
    var q5NeedPolice by remember { mutableStateOf<Boolean?>(null) }
    var q6NeedTrustedPerson by remember { mutableStateOf<Boolean?>(null) }

    fun dial(num: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("i_need_help_title", currentLanguage),
                subtitle = "Guided Step-by-Step Safety Assistant",
                currentLang = currentLanguage,
                onBack = {
                    if (currentStep > 1) {
                        currentStep--
                    } else {
                        onBack()
                    }
                },
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
                .padding(18.dp)
        ) {
            // Calm Progress Header
            if (currentStep <= 6) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question $currentStep of 6",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = DurgaTextSecondaryMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "${((currentStep / 6f) * 100).toInt()}% Complete",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DurgaPrimaryNavy,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { currentStep / 6f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(DurgaBorderLight, RoundedCornerShape(3.dp)),
                    color = DurgaPrimaryNavy,
                    trackColor = DurgaBorderLight
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Step Content
            when (currentStep) {
                1 -> {
                    // Question 1: What is happening?
                    Text(
                        text = "1. What is happening?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select what best describes your current situation:",
                        style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val incidentOptions = listOf(
                        "Facing threats, violence, or abuse at home",
                        "Being stalked, followed, or harassed in public",
                        "Online blackmail, harassment, or non-consensual image threat",
                        "Workplace or educational institution harassment",
                        "Financial scam, extortion, or other crisis"
                    )

                    incidentOptions.forEach { opt ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable {
                                    q1IncidentType = opt
                                    currentStep = 2
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = DurgaTextNavy,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = DurgaTextLightMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Question 2: Are you in immediate danger?
                    GuidedBinaryQuestion(
                        number = 2,
                        question = "Are you in immediate physical danger right now?",
                        subtext = "If someone is threatening your life or physically present, prioritize emergency escape.",
                        onAnswer = { isYes ->
                            q2ImmediateDanger = isYes
                            if (isYes) {
                                // Direct urgent route
                                currentStep = 7 // Go directly to emergency response plan
                            } else {
                                currentStep = 3
                            }
                        }
                    )
                }

                3 -> {
                    // Question 3: Are you alone?
                    GuidedBinaryQuestion(
                        number = 3,
                        question = "Are you currently alone?",
                        subtext = "Knowing whether someone is with you helps determine if trusted intervention is possible.",
                        onAnswer = { isYes ->
                            q3IsAlone = isYes
                            currentStep = 4
                        }
                    )
                }

                4 -> {
                    // Question 4: Do you need medical help?
                    GuidedBinaryQuestion(
                        number = 4,
                        question = "Do you need medical attention or first aid?",
                        subtext = "For injuries, trauma care, or medical examination.",
                        onAnswer = { isYes ->
                            q4NeedMedical = isYes
                            currentStep = 5
                        }
                    )
                }

                5 -> {
                    // Question 5: Do you need police help?
                    GuidedBinaryQuestion(
                        number = 5,
                        question = "Do you want official police intervention or an FIR registered?",
                        subtext = "Police can dispatch a mobile patrol or initiate an official inquiry.",
                        onAnswer = { isYes ->
                            q5NeedPolice = isYes
                            currentStep = 6
                        }
                    )
                }

                6 -> {
                    // Question 6: Do you need someone you trust?
                    GuidedBinaryQuestion(
                        number = 6,
                        question = "Do you want to notify a trusted friend, family member, or NGO counselor?",
                        subtext = "We can help you share your location and a pre-written message safely.",
                        onAnswer = { isYes ->
                            q6NeedTrustedPerson = isYes
                            currentStep = 7 // Show recommendations
                        }
                    )
                }

                else -> {
                    // Result: Recommended Next Steps
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "Your Recommended Safety Action Plan",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaTextNavy,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Based on your answers, here are the safest next steps:",
                                        style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    if (q2ImmediateDanger == true || q5NeedPolice == true) {
                                        ActionPlanItem(
                                            priority = "1",
                                            title = "Contact Emergency Police (112)",
                                            desc = "Request immediate patrol support or location intercept.",
                                            buttonLabel = "Call 112",
                                            isEmergency = true
                                        ) { dial("112") }
                                    }

                                    if (q4NeedMedical == true) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        ActionPlanItem(
                                            priority = "2",
                                            title = "Call Medical Ambulance (108)",
                                            desc = "Free emergency ambulance and trauma treatment.",
                                            buttonLabel = "Call 108",
                                            isEmergency = true
                                        ) { dial("108") }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    ActionPlanItem(
                                        priority = "3",
                                        title = "Connect with Women Helpline (1091 / 181)",
                                        desc = "Confidential crisis counseling, shelter and legal advice.",
                                        buttonLabel = "Call 181",
                                        isEmergency = false
                                    ) { dial("181") }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    ActionPlanItem(
                                        priority = "4",
                                        title = "Prepare Structured Complaint Draft",
                                        desc = "Use AI-assisted complaint drafting to organize dates, times and facts clearly for police submission.",
                                        buttonLabel = "Start Draft",
                                        isEmergency = false
                                    ) { onNavigateToComplaint() }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    currentStep = 1
                                    q1IncidentType = ""
                                    q2ImmediateDanger = null
                                    q3IsAlone = null
                                    q4NeedMedical = null
                                    q5NeedPolice = null
                                    q6NeedTrustedPerson = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaSectionSoft),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text("Start Over", color = DurgaPrimaryNavy, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuidedBinaryQuestion(
    number: Int,
    question: String,
    subtext: String,
    onAnswer: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "$number. $question",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DurgaTextNavy,
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted, fontSize = 13.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Button(
                onClick = { onAnswer(true) },
                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Text("YES", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }

            Button(
                onClick = { onAnswer(false) },
                colors = ButtonDefaults.buttonColors(containerColor = DurgaSectionSoft),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Text("NO", fontWeight = FontWeight.Bold, color = DurgaTextNavy, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun ActionPlanItem(
    priority: String,
    title: String,
    desc: String,
    buttonLabel: String,
    isEmergency: Boolean,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = if (isEmergency) DurgaEmergencyRedLight else DurgaSectionSoft),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEmergency) DurgaEmergencyRed.copy(alpha = 0.4f) else DurgaBorderLight),
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
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isEmergency) DurgaEmergencyRed else DurgaTextNavy,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
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
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = if (isEmergency) DurgaEmergencyRed else DurgaPrimaryNavy),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(text = buttonLabel, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

// --- 3. Child Friendly Screen ---
@Composable
fun ChildFriendlyScreen(
    currentLanguage: DurgaLanguage,
    onCallChildline: () -> Unit,
    onStartChildComplaint: (String) -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("child_friendly_mode", currentLanguage),
                subtitle = "Comforting help for young people & children",
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
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(DurgaWarningLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChildCare,
                                contentDescription = "Child Safe",
                                tint = DurgaWarning,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "You are safe and not alone.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = 16.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "If someone is hurting or scaring you, it is not your fault. Tap any card below to get gentle help.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Big Childline 1098 Call Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DurgaBlueAction),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCallChildline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Childline India: 1098",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaBlueAction,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Free 24x7 phone helpline for children in need of care & protection.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Button(
                            onClick = onCallChildline,
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaBlueAction),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text("CALL 1098", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            // Comforting Safety Cards
            item {
                Text(
                    text = "Choose what feels closest:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DurgaTextNavy,
                        fontSize = 14.sp
                    )
                )
            }

            val childCards = listOf(
                Pair(DurgaStrings.get("child_scared", currentLanguage), "I feel scared or unsafe right now."),
                Pair(DurgaStrings.get("child_troubled", currentLanguage), "Someone at school, bus, or online is harassing me."),
                Pair(DurgaStrings.get("child_hurt", currentLanguage), "Someone touched me or hurt me inappropriately."),
                Pair(DurgaStrings.get("child_trusted_adult", currentLanguage), "I want to safely speak to a teacher, doctor, or counselor.")
            )

            items(childCards) { (title, subtitle) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartChildComplaint(title) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy,
                                    fontSize = 14.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = DurgaTextLightMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- 4. Privacy & Accessibility Settings Screen ---
@Composable
fun PrivacyCenterScreen(
    currentLanguage: DurgaLanguage,
    onPurgeAllData: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    onThemeChange: (DurgaThemeMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPrivacyModeOn by remember { mutableStateOf(true) }
    var isNotificationPreviewOn by remember { mutableStateOf(false) }
    var isLargeTextOn by remember { mutableStateOf(false) }
    var isHighContrastOn by remember { mutableStateOf(false) }
    var isReduceMotionOn by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val colors = LocalDurgaColors.current
    val currentTheme = LocalDurgaThemeMode.current

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = "Privacy & Settings",
                subtitle = "Security controls & accessibility preferences",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = colors.background,
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 5 Color Themes Selector
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Theme",
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DurgaStrings.get("theme_5_colors", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                            Text(
                                text = "5 COLORS",
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DurgaThemeMode.entries.forEach { mode ->
                                val isSelected = mode == currentTheme
                                val modeLabel = when (mode) {
                                    DurgaThemeMode.GREEN -> "1. Green / પ્રાકૃતિક લીલો (Nature & Safety)"
                                    DurgaThemeMode.BLACK -> "2. Black / ડાર્ક મોડ (Midnight Dark)"
                                    DurgaThemeMode.BLUE -> "3. Blue / રક્ષક બ્લુ (Guardian Blue)"
                                    DurgaThemeMode.PINK -> "4. Pink / રાણી પિંક (Deep Rani Pink)"
                                    DurgaThemeMode.ROSE_PINK -> "5. Rose Pink / સોફ્ટ ગુલાબી (Soft Rose Pink)"
                                    DurgaThemeMode.PURPLE -> "6. Purple / શાહી ગરિમા (Royal Dignity)"
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onThemeChange(mode) }
                                        .background(
                                            if (isSelected) colors.primaryContainer else colors.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            if (isSelected) 1.5.dp else 0.5.dp,
                                            if (isSelected) colors.primary else colors.outline,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(mode.primaryColor, CircleShape)
                                                .border(2.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(mode.accentColor, CircleShape)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = modeLabel,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = colors.textPrimary,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = colors.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // Privacy Controls
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Privacy & Discreet Controls",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingToggleRow(
                            title = "Privacy Mode (Disguised Notifications)",
                            desc = "Display neutral notifications (e.g., 'Weather update') rather than emergency alerts.",
                            checked = isPrivacyModeOn,
                            onCheckedChange = { isPrivacyModeOn = it }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DurgaBorderSubtle)

                        SettingToggleRow(
                            title = "Lock-screen Notification Preview",
                            desc = "Hide case details and case tokens on phone lock screen.",
                            checked = isNotificationPreviewOn,
                            onCheckedChange = { isNotificationPreviewOn = it }
                        )
                    }
                }
            }

            // Accessibility Mode
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Accessibility Options",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaTextNavy,
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SettingToggleRow(
                            title = "Large Text Mode",
                            desc = "Increase typography scale across all buttons and emergency numbers.",
                            checked = isLargeTextOn,
                            onCheckedChange = { isLargeTextOn = it }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DurgaBorderSubtle)

                        SettingToggleRow(
                            title = "High Contrast Visuals",
                            desc = "Reinforce contrast borders and darker text for outdoor sunlight viewing.",
                            checked = isHighContrastOn,
                            onCheckedChange = { isHighContrastOn = it }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DurgaBorderSubtle)

                        SettingToggleRow(
                            title = "Reduce Motion",
                            desc = "Minimize transitions and pulsating icons for elderly or sensitive users.",
                            checked = isReduceMotionOn,
                            onCheckedChange = { isReduceMotionOn = it }
                        )
                    }
                }
            }

            // Emergency Data Purge
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaEmergencyRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = DurgaEmergencyRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1-Tap Local Data Wipe",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaEmergencyRed,
                                    fontSize = 14.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = DurgaStrings.get("delete_warning", currentLanguage),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DurgaTextSecondaryMuted,
                                fontSize = 12.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                        ) {
                            Text("Wipe All Local Evidence & Drafts", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "Permanent Local Deletion?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DurgaEmergencyRed)
                )
            },
            text = {
                Text(
                    text = "This will immediately erase all locally encrypted evidence, drafts, and case keys on this phone. Are you sure?",
                    style = MaterialTheme.typography.bodyMedium.copy(color = DurgaTextNavy)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onPurgeAllData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                ) {
                    Text("Delete Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = DurgaTextNavy)
                }
            }
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
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

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DurgaPrimaryNavy,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = DurgaBorderLight
            )
        )
    }
}

// --- 5. Authority Officer Admin Portal Screen ---
@Composable
fun AuthorityAdminDashboardScreen(
    currentLanguage: DurgaLanguage,
    currentRole: AuthorityRole?,
    staffId: String?,
    casesList: List<CaseEntity>,
    auditLogs: List<AuditAccessLogEntity>,
    onLogin: (role: AuthorityRole, staffId: String) -> Unit,
    onLogout: () -> Unit,
    onUpdateCaseStatus: (caseId: String, status: String, msg: String) -> Unit,
    onRecordAuditAction: (caseId: String, action: String, reason: String) -> Unit,
    onNavigateToWebPortals: () -> Unit = {},
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputStaffId by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(AuthorityRole.AUTHORITY_STAFF) }
    var activeTab by remember { mutableStateOf(0) }
    var selectedCaseForReview by remember { mutableStateOf<CaseEntity?>(null) }
    var updateStatusSelection by remember { mutableStateOf("UNDER_INVESTIGATION") }
    var officerNoteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("authority_portal", currentLanguage),
                subtitle = DurgaStrings.get("authority_portal_sub", currentLanguage),
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (staffId == null || currentRole == null) {
                // Officer Login Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(DurgaPrimaryNavy, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "સત્તાવાર સમીક્ષા સાઇન-ઇન"
                                            DurgaLanguage.HINDI -> "आधिकारिक समीक्षा साइन-इन"
                                            DurgaLanguage.ENGLISH -> "Official Review Sign-In"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaTextNavy
                                        )
                                    )
                                    Text(
                                        text = when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "નોંધાયેલ પોલીસ અને નોડલ અધિકારીઓ માટે અધિકૃત ઍક્સેસ"
                                            DurgaLanguage.HINDI -> "पंजीकृत पुलिस एवं नोडल अधिकारियों के लिए वैधानिक पहुंच"
                                            DurgaLanguage.ENGLISH -> "Statutory access for registered law enforcement staff"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(color = DurgaTextSecondaryMuted)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = inputStaffId,
                                onValueChange = { inputStaffId = it },
                                label = {
                                    Text(
                                        when (currentLanguage) {
                                            DurgaLanguage.GUJARATI -> "બેજ ID / સર્વિસ નંબર (દા.ત. GJ-POL-782)"
                                            DurgaLanguage.HINDI -> "बैज आईडी / सर्विस नंबर (उदा. POL-782)"
                                            DurgaLanguage.ENGLISH -> "Badge ID / Officer Service Number"
                                        }
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DurgaPrimaryNavy,
                                    unfocusedBorderColor = DurgaBorderLight
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = when (currentLanguage) {
                                    DurgaLanguage.GUJARATI -> "વિભાગીય ભૂમિકા પસંદ કરો:"
                                    DurgaLanguage.HINDI -> "विभागीय भूमिका चुनें:"
                                    DurgaLanguage.ENGLISH -> "Select Department Role:"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AuthorityRole.entries.forEach { role ->
                                    val isSelected = selectedRole == role
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedRole = role },
                                        label = { Text(role.title, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = DurgaPrimaryNavy,
                                            selectedLabelColor = Color.White,
                                            containerColor = DurgaSurfaceWhite,
                                            labelColor = DurgaTextNavy
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = DurgaBorderLight,
                                            selectedBorderColor = DurgaPrimaryNavy
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (inputStaffId.isNotBlank()) {
                                        onLogin(selectedRole, inputStaffId)
                                    }
                                },
                                enabled = inputStaffId.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "અધિકારી સત્ર પ્રમાણિત કરો (Login)"
                                        DurgaLanguage.HINDI -> "अधिकारी सत्र सत्यापित करें (Login)"
                                        DurgaLanguage.ENGLISH -> "Authenticate Officer Session"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToWebPortals,
                        colors = ButtonDefaults.buttonColors(containerColor = DurgaSurfaceWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DurgaPrimaryNavy),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            tint = DurgaPrimaryNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (currentLanguage) {
                                DurgaLanguage.GUJARATI -> "🌐 લાઈવ વેબ પોર્ટલ ખોલો (Admin & Police Desks)"
                                DurgaLanguage.HINDI -> "🌐 लाइव वेब पोर्टल खोलें (Admin & Police Desks)"
                                DurgaLanguage.ENGLISH -> "🌐 Launch Live Web Portals (Admin & Police)"
                            },
                            color = DurgaPrimaryNavy,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Officer Dashboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Officer: $staffId (${currentRole.title})",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaTextNavy
                                        )
                                    )
                                    Text(
                                        text = "Audit Logging: Active • State Jurisdiction verified",
                                        style = MaterialTheme.typography.bodySmall.copy(color = DurgaSuccessGreen, fontSize = 11.sp)
                                    )
                                }

                                TextButton(onClick = onLogout) {
                                    Text("Sign Out", color = DurgaEmergencyRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Tabs: Assigned Cases vs Audit Logs
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DurgaSurfaceWhite, RoundedCornerShape(10.dp))
                                .border(1.dp, DurgaBorderLight, RoundedCornerShape(10.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { activeTab = 0 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeTab == 0) DurgaPrimaryNavy else Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "કેસ યાદી (${casesList.size})"
                                        DurgaLanguage.HINDI -> "केस सूची (${casesList.size})"
                                        DurgaLanguage.ENGLISH -> "Cases (${casesList.size})"
                                    },
                                    color = if (activeTab == 0) Color.White else DurgaTextNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = { activeTab = 1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (activeTab == 1) DurgaPrimaryNavy else Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = when (currentLanguage) {
                                        DurgaLanguage.GUJARATI -> "ઓડિટ ટ્રાયલ (${auditLogs.size})"
                                        DurgaLanguage.HINDI -> "ऑडिट ट्रेल (${auditLogs.size})"
                                        DurgaLanguage.ENGLISH -> "Audit Trail (${auditLogs.size})"
                                    },
                                    color = if (activeTab == 1) Color.White else DurgaTextNavy,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (activeTab == 0) {
                        // Assigned Cases List
                        if (casesList.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DurgaSuccessGreen,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = when (currentLanguage) {
                                                DurgaLanguage.GUJARATI -> "કોઈ પેન્ડિંગ કેસ નથી"
                                                DurgaLanguage.HINDI -> "कोई लंबित केस नहीं है"
                                                DurgaLanguage.ENGLISH -> "No pending complaints assigned"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            color = DurgaTextNavy
                                        )
                                    }
                                }
                            }
                        } else {
                            items(casesList) { caseItem ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCaseForReview = caseItem
                                            updateStatusSelection = caseItem.status
                                            officerNoteText = ""
                                            onRecordAuditAction(caseItem.caseId, "VIEW_CASE", "Officer $staffId reviewing details")
                                        },
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
                                                text = caseItem.caseId,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = DurgaPrimaryNavy
                                                )
                                            )
                                            Text(
                                                text = caseItem.status.replace("_", " "),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (caseItem.status) {
                                                        "RESOLVED" -> DurgaSuccessGreen
                                                        "UNDER_INVESTIGATION" -> Color(0xFFD97706)
                                                        else -> DurgaPrimaryNavy
                                                    }
                                                ),
                                                modifier = Modifier
                                                    .background(
                                                        when (caseItem.status) {
                                                            "RESOLVED" -> DurgaSuccessLight
                                                            "UNDER_INVESTIGATION" -> Color(0xFFFEF3C7)
                                                            else -> DurgaBlueLightBg
                                                        },
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = caseItem.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = DurgaTextNavy
                                            )
                                        )
                                        Text(
                                            text = "${caseItem.category} • Tap to review & update status",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DurgaTextSecondaryMuted,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Audit Logs List
                        if (auditLogs.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "No audit events recorded yet",
                                            fontWeight = FontWeight.Medium,
                                            color = DurgaTextSecondaryMuted
                                        )
                                    }
                                }
                            }
                        } else {
                            items(auditLogs) { log ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Action: ${log.action}",
                                                fontWeight = FontWeight.Bold,
                                                color = DurgaPrimaryNavy,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Case: ${log.caseId.take(12)}",
                                                fontSize = 11.sp,
                                                color = DurgaTextSecondaryMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Authority: ${log.authorityId} (${log.role})",
                                            fontSize = 12.sp,
                                            color = DurgaTextNavy
                                        )
                                        Text(
                                            text = "Reason: ${log.reason}",
                                            fontSize = 11.sp,
                                            color = DurgaTextSecondaryMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        AuthorityWatermark(
                            officerId = staffId,
                            roleTitle = currentRole.title
                        )
                    }
                }
            }
        }
    }

    // Case Review & Status Update Dialog
    if (selectedCaseForReview != null) {
        val currentCase = selectedCaseForReview!!
        AlertDialog(
            onDismissRequest = { selectedCaseForReview = null },
            title = {
                Text(
                    text = "Official Case Review: ${currentCase.caseId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DurgaTextNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Title: ${currentCase.title}",
                        fontWeight = FontWeight.SemiBold,
                        color = DurgaTextNavy,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Category: ${currentCase.category}",
                        color = DurgaTextSecondaryMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Description:\n${currentCase.structuredDescription.ifBlank { currentCase.originalDescription }}",
                        color = DurgaTextNavy,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    HorizontalDivider()

                    Text(
                        text = "Set Investigation Status:",
                        fontWeight = FontWeight.Bold,
                        color = DurgaTextNavy,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("UNDER_INVESTIGATION", "ACTION_TAKEN", "RESOLVED").forEach { statusOpt ->
                            val isChosen = updateStatusSelection == statusOpt
                            FilterChip(
                                selected = isChosen,
                                onClick = { updateStatusSelection = statusOpt },
                                label = {
                                    Text(
                                        text = statusOpt.replace("_", " "),
                                        fontSize = 10.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DurgaPrimaryNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = officerNoteText,
                        onValueChange = { officerNoteText = it },
                        label = { Text("Officer Action Remarks / Dispatch Note", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val notes = officerNoteText.ifBlank { "Status updated by Officer $staffId to $updateStatusSelection" }
                        onUpdateCaseStatus(currentCase.caseId, updateStatusSelection, notes)
                        onRecordAuditAction(currentCase.caseId, "STATUS_UPDATE", "Status set to $updateStatusSelection. Note: $notes")
                        selectedCaseForReview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy)
                ) {
                    Text("Save & Log Audit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCaseForReview = null }) {
                    Text("Cancel", color = DurgaTextSecondaryMuted)
                }
            }
        )
    }
}

@Composable
fun AuthorityWatermark(officerId: String, roleTitle: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "OFFICIAL SESSION • BADGE #$officerId • $roleTitle • AUDIT LOGGED",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = DurgaTextSecondaryMuted.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        )
    }
}
