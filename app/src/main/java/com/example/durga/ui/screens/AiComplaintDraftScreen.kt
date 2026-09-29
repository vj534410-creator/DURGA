package com.example.durga.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.AiGeneratedDraft
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.ui.components.DurgaBadgeType
import com.example.durga.ui.components.DurgaStatusBadge
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaBackgroundLight
import com.example.ui.theme.DurgaBorderLight
import com.example.ui.theme.DurgaPrimaryNavy
import com.example.ui.theme.DurgaSectionSoft
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.DurgaSurfaceWhite
import com.example.ui.theme.DurgaTextNavy
import com.example.ui.theme.DurgaTextSecondaryMuted
import com.example.ui.theme.DurgaWarning
import com.example.ui.theme.DurgaWarningLight

@Composable
fun AiComplaintDraftScreen(
    currentLanguage: DurgaLanguage,
    currentDraft: DraftComplaintEntity,
    aiDraft: AiGeneratedDraft?,
    onNavigateToEdit: () -> Unit,
    onContinueToDetails: () -> Unit,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("ai_draft_result", currentLanguage),
                subtitle = "Step 3 of 7: Review AI-Structured Draft",
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
            // Calm Header Notice
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSectionSoft),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = DurgaPrimaryNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI is only an assistant to format your notes clearly. You maintain 100% control.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // 1. Original Statement
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, DurgaBorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = DurgaStrings.get("original_statement", currentLanguage),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentDraft.rawNotes.ifBlank { "No initial notes provided." },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DurgaTextSecondaryMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            // 2. Clean Draft Complaint
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DurgaSurfaceWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DurgaPrimaryNavy.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Formatted Complaint Draft",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DurgaPrimaryNavy,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                letterSpacing = 0.1.sp
                            )
                        )
                        DurgaStatusBadge(
                            text = "READY FOR REVIEW",
                            type = DurgaBadgeType.SUCCESS
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = aiDraft?.title?.ifBlank { null } ?: currentDraft.aiDraftTitle.ifBlank { "Official Complaint" },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DurgaTextNavy,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            letterSpacing = 0.15.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = aiDraft?.detailedDescription?.ifBlank { null } ?: currentDraft.aiDraftDescription.ifBlank { currentDraft.rawNotes },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DurgaTextNavy,
                            fontSize = 13.5.sp,
                            lineHeight = 21.sp, // ~1.55x
                            letterSpacing = 0.2.sp
                        )
                    )
                }
            }

            // 3. Missing Details Alert
            val missingList = aiDraft?.missingQuestions ?: emptyList()
            if (missingList.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DurgaWarningLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaWarning.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alert",
                                tint = DurgaWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DurgaStrings.get("missing_facts_alert", currentLanguage),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DurgaTextNavy,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        for (item in missingList) {
                            Text(
                                text = "• $item",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaTextSecondaryMuted,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToEdit,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("edit_draft_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = DurgaPrimaryNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Draft", color = DurgaPrimaryNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onContinueToDetails,
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaPrimaryNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("continue_to_details_button")
                ) {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
