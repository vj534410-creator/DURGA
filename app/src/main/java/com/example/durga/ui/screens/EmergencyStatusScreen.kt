package com.example.durga.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.database.EmergencyContact
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.SosActiveSession
import com.example.durga.model.SosCallState
import com.example.durga.model.SosLocationState
import com.example.durga.model.SosSmsState
import com.example.durga.model.SosWhatsAppState
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.LocalDurgaColors

@Composable
fun EmergencyStatusScreen(
    currentLanguage: DurgaLanguage,
    session: SosActiveSession,
    onCancelEmergency: () -> Unit,
    onRedialPrimary: () -> Unit,
    onRetryLocation: () -> Unit,
    onOpenSmsComposer: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onCall112: () -> Unit,
    onBackupCall: (EmergencyContact) -> Unit,
    onBackupSms: (EmergencyContact) -> Unit,
    onBackupWhatsApp: (EmergencyContact) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    val context = LocalContext.current
    var showCancelDialog by remember { mutableStateOf(false) }

    // Pulsing halo animation for active SOS state
    val infiniteTransition = rememberInfiniteTransition(label = "SosPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    BackHandler {
        showCancelDialog = true
    }

    Scaffold(
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
            // 1. TOP ACTIVE HEADER
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DurgaEmergencyRed.copy(alpha = 0.12f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DurgaEmergencyRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pulsing Icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(DurgaEmergencyRed.copy(alpha = pulseAlpha))
                        ) {
                            Icon(
                                imageVector = if (session.isSilentMode) Icons.Default.VolumeOff else Icons.Default.Shield,
                                contentDescription = "SOS Active",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = DurgaStrings.get("sos_active_title", currentLanguage),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = DurgaEmergencyRed,
                                fontSize = 24.sp,
                                letterSpacing = 1.sp
                            )
                        )

                        Text(
                            text = DurgaStrings.get("sos_active_subtitle", currentLanguage),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        )

                        if (session.isSilentMode) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = DurgaStrings.get("sos_silent_badge", currentLanguage),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier
                                    .background(Color(0xFF374151), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DurgaEmergencyRed.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${DurgaStrings.get("sos_incident_id", currentLanguage)}: ${session.incidentId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary
                            )
                            Text(
                                text = session.timestampDisplay,
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            }

            // 2. PRIMARY CALL STATUS
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                    modifier = Modifier.fillMaxWidth()
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = if (session.isSilentMode) colors.textSecondary else colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DurgaStrings.get("sos_call_status", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = 16.sp
                                    )
                                )
                            }

                            // Truthful Status Badge
                            val (badgeText, badgeBg, badgeFg) = when {
                                session.isSilentMode -> Triple("SILENT / NO CALL", Color(0xFFE5E7EB), Color(0xFF374151))
                                session.callState == SosCallState.CALLING_PRIMARY -> Triple("CALLING...", Color(0xFFDCFCE7), DurgaSuccessGreen)
                                session.callState == SosCallState.DIALER_OPENED -> Triple("DIALER OPENED", Color(0xFFFEF3C7), Color(0xFFB45309))
                                session.callState == SosCallState.FAILED -> Triple("CALL FAILED", Color(0xFFFEE2E2), DurgaEmergencyRed)
                                else -> Triple("STANDBY", colors.surfaceVariant, colors.textSecondary)
                            }
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = badgeFg,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier
                                    .background(badgeBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (session.primaryContact != null) {
                            Text(
                                text = "${session.primaryContact.name} (${session.primaryContact.phoneNumber})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                            )
                            Text(
                                text = "${session.primaryContact.relationship} • Priority 1",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        } else {
                            Text(
                                text = "No primary emergency contact configured.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DurgaEmergencyRed,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onRedialPrimary,
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("redial_primary_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(DurgaStrings.get("sos_call_redial", currentLanguage))
                        }
                    }
                }
            }

            // 3. LOCATION STATUS
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                    modifier = Modifier.fillMaxWidth()
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (session.locationState == SosLocationState.OBTAINED) DurgaSuccessGreen else Color(0xFFF59E0B),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = DurgaStrings.get("sos_location_status", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        fontSize = 16.sp
                                    )
                                )
                            }

                            val (locBadge, locBg, locFg) = when (session.locationState) {
                                SosLocationState.OBTAINED -> Triple("OBTAINED", Color(0xFFDCFCE7), DurgaSuccessGreen)
                                SosLocationState.PENDING -> Triple("SEARCHING...", Color(0xFFFEF3C7), Color(0xFFB45309))
                                SosLocationState.PERMISSION_DENIED -> Triple("NO PERMISSION", Color(0xFFFEE2E2), DurgaEmergencyRed)
                                SosLocationState.UNAVAILABLE -> Triple("UNAVAILABLE", Color(0xFFFEE2E2), DurgaEmergencyRed)
                            }
                            Text(
                                text = locBadge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = locFg,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier
                                    .background(locBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (session.locationState == SosLocationState.OBTAINED) {
                            Text(
                                text = "Coordinates: ${"%.4f".format(session.latitude)}, ${"%.4f".format(session.longitude)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                            )
                            if (session.locationAccuracyText.isNotBlank()) {
                                Text(
                                    text = session.locationAccuracyText,
                                    style = MaterialTheme.typography.bodySmall.copy(color = colors.textSecondary)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            if (session.mapLink.isNotBlank()) {
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(session.mapLink)))
                                        } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Google Maps Link")
                                }
                            }
                        } else {
                            Text(
                                text = DurgaStrings.get("sos_location_unavailable", currentLanguage),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = colors.textSecondary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = onRetryLocation,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(DurgaStrings.get("sos_location_retry", currentLanguage))
                            }
                        }
                    }
                }
            }

            // 4. SMS & WHATSAPP ACTIONS
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Emergency Messaging",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                        )

                        // SMS Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Message, contentDescription = null, tint = colors.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = DurgaStrings.get("sos_sms_status", currentLanguage),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = DurgaStrings.get("sos_sms_ready", currentLanguage),
                                        fontSize = 12.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                            Button(
                                onClick = onOpenSmsComposer,
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                            ) {
                                Text("Send SMS")
                            }
                        }

                        // WhatsApp Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF25D366))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = DurgaStrings.get("sos_whatsapp_status", currentLanguage),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = if (session.whatsAppState == SosWhatsAppState.OPENED_CHAT)
                                            DurgaStrings.get("sos_whatsapp_opened", currentLanguage)
                                        else
                                            DurgaStrings.get("sos_whatsapp_ready", currentLanguage),
                                        fontSize = 12.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }
                            Button(
                                onClick = onOpenWhatsApp,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text("WhatsApp", color = Color.White)
                            }
                        }
                    }
                }
            }

            // 5. OFFICIAL 112 NATIONAL EMERGENCY
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEF2F2)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DurgaEmergencyRed),
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
                                text = "Emergency Service (ERSS 112)",
                                fontWeight = FontWeight.Bold,
                                color = DurgaEmergencyRed,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "National Unified Emergency Response (Police, Fire, Medical)",
                                fontSize = 12.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onCall112,
                            colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("112")
                        }
                    }
                }
            }

            // 6. BACKUP CONTACTS LIST
            if (session.backupContacts.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = DurgaStrings.get("sos_backup_contacts", currentLanguage),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                            )

                            session.backupContacts.forEach { backup ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = backup.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "${backup.phoneNumber} • ${backup.relationship}",
                                            fontSize = 12.sp,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { onBackupCall(backup) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                        OutlinedButton(
                                            onClick = { onBackupSms(backup) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }
                                        if (backup.isWhatsAppAvailable) {
                                            OutlinedButton(
                                                onClick = { onBackupWhatsApp(backup) },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. SAFETY GUIDANCE
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = DurgaStrings.get("sos_guidance_title", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "• " + DurgaStrings.get("sos_guidance_1", currentLanguage),
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "• " + DurgaStrings.get("sos_guidance_2", currentLanguage),
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                        Text(
                            text = "• " + DurgaStrings.get("sos_guidance_3", currentLanguage),
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // 8. CANCEL / STOP BUTTON
            item {
                Button(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("cancel_sos_btn")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = DurgaStrings.get("sos_cancel_btn", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(DurgaStrings.get("sos_cancel_btn", currentLanguage), fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(DurgaStrings.get("sos_cancel_confirm", currentLanguage))
                    Text(
                        text = DurgaStrings.get("sos_cancel_note", currentLanguage),
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelEmergency()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                ) {
                    Text("Confirm Stop")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Keep Active")
                }
            }
        )
    }
}
