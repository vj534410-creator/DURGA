package com.example.durga.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBlueAction
import com.example.ui.theme.DurgaBlueLightBg
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaEmergencyRedLight
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSectionSoft
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextLightMuted
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted

@Composable
fun ComplaintInputScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    isListeningVoice: Boolean,
    isAiLoading: Boolean,
    onNotesChange: (String) -> Unit,
    onToggleVoice: () -> Unit,
    onRunAiDraft: () -> Unit,
    onSaveDraftLocally: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListeningVoice) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("describe_incident", currentLanguage),
                subtitle = "Step 2 of 7: Speak or Type Description",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
            )
        },
        containerColor = DurgaBackgroundLight,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Category & Security Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Category: ${currentDraft.category.replace("_", " ")}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DurgaPrimaryNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .background(DurgaSectionSoft, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )

                Text(
                    text = "No login • 100% Private",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = DurgaSuccessGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
            }

            // Text input area
            OutlinedTextField(
                value = currentDraft.rawNotes,
                onValueChange = onNotesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .testTag("complaint_narrative_input"),
                placeholder = {
                    Text(
                        text = DurgaStrings.get("describe_hint", currentLanguage),
                        color = DurgaTextLightMuted,
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DurgaTextNavy,
                    unfocusedTextColor = DurgaTextNavy,
                    focusedContainerColor = DurgaSurfaceWhite,
                    unfocusedContainerColor = DurgaSurfaceWhite,
                    focusedBorderColor = DurgaPrimaryNavy,
                    unfocusedBorderColor = DurgaBorderLight
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Voice Recording Section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isListeningVoice) DurgaEmergencyRedLight else DurgaSurfaceWhite
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isListeningVoice) DurgaEmergencyRed else DurgaBorderLight
                ),
                shape = RoundedCornerShape(12.dp),
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
                            text = if (isListeningVoice) DurgaStrings.get("listening", currentLanguage)
                            else DurgaStrings.get("tap_to_speak", currentLanguage),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isListeningVoice) DurgaEmergencyRed else DurgaTextNavy,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Supports Gujarati, Hindi, English voice dictation",
                            style = MaterialTheme.typography.labelSmall.copy(color = DurgaTextSecondaryMuted, fontSize = 11.sp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .scale(pulseScale)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isListeningVoice) DurgaEmergencyRed else DurgaPrimaryNavy)
                            .clickable(onClick = onToggleVoice)
                            .testTag("voice_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListeningVoice) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // AI Safety Principle Notice
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
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Safety Principle",
                        tint = DurgaPrimaryNavy,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Principle: DURGA AI is ONLY used for structuring your complaint draft clearly. It will NEVER invent facts, judge guilt, or submit automatically.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Action: Run AI Complaint Writer
            Button(
                onClick = onRunAiDraft,
                enabled = currentDraft.rawNotes.isNotBlank() && !isAiLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DurgaPrimaryNavy,
                    disabledContainerColor = DurgaPrimaryNavy.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("run_ai_complaint_button")
            ) {
                if (isAiLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Structuring Complaint...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DurgaStrings.get("ai_drafting", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            // Save Draft Privately
            Button(
                onClick = onSaveDraftLocally,
                colors = ButtonDefaults.buttonColors(containerColor = DurgaSurfaceWhite),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("save_draft_button")
            ) {
                Text(
                    text = DurgaStrings.get("save_draft", currentLanguage),
                    color = DurgaTextNavy,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
