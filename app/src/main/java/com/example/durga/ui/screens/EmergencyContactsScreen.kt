package com.example.durga.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durga.database.EmergencyContact
import com.example.durga.database.SecurityVault
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.i18n.DurgaStrings
import com.example.durga.model.SosTestResult
import com.example.durga.ui.components.DurgaTopBar
import com.example.ui.theme.DurgaEmergencyRed
import com.example.ui.theme.DurgaSuccessGreen
import com.example.ui.theme.LocalDurgaColors

@Composable
fun EmergencyContactsScreen(
    currentLanguage: DurgaLanguage,
    contacts: List<EmergencyContact>,
    onAddContact: (String, String, String, Boolean) -> Pair<Boolean, String?>,
    onUpdateContact: (EmergencyContact) -> Pair<Boolean, String?>,
    onDeleteContact: (String) -> Boolean,
    onReorderContacts: (List<String>) -> Boolean,
    onRunTestSos: () -> SosTestResult,
    onBack: () -> Unit,
    onLanguageChange: (DurgaLanguage) -> Unit,
    onQuickExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalDurgaColors.current
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var editingContact by remember { mutableStateOf<EmergencyContact?>(null) }
    var contactToDelete by remember { mutableStateOf<EmergencyContact?>(null) }
    var testResultDialog by remember { mutableStateOf<SosTestResult?>(null) }

    Scaffold(
        topBar = {
            DurgaTopBar(
                title = DurgaStrings.get("manage_contacts_btn", currentLanguage),
                subtitle = "EncryptedSharedPreferences Protected",
                currentLang = currentLanguage,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onQuickExit = onQuickExit
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
            // 1. INFO CARD
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Zero-Knowledge Encrypted Storage",
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Add up to 5 trusted family or friends. Contact priority determines who is called first when One-Tap SOS is activated. Phone numbers are validated before saving.",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            // 2. CONTACTS LIST HEADER & ADD BUTTON
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Configured Contacts (${contacts.size}/5)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )

                    Button(
                        onClick = { showAddDialog = true },
                        enabled = contacts.size < 5,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                        modifier = Modifier.testTag("add_emergency_contact_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(DurgaStrings.get("add_contact_title", currentLanguage))
                    }
                }
            }

            // 3. CONTACT ITEMS
            if (contacts.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No emergency contacts configured yet",
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Add at least 1 contact (e.g. Mother, Sister, Friend) to enable 1-tap emergency calling & alerts.",
                                fontSize = 12.sp,
                                color = colors.textSecondary
                            )
                        }
                    }
                }
            } else {
                items(contacts) { contact ->
                    val isPrimary = contact.priority == 1
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isPrimary) 2.dp else 1.dp,
                            if (isPrimary) colors.primary else colors.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (isPrimary) colors.primaryContainer else colors.surfaceVariant,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#${contact.priority}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPrimary) colors.primary else colors.textPrimary,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = contact.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = "${contact.phoneNumber} • ${contact.relationship}",
                                            fontSize = 13.sp,
                                            color = colors.textSecondary
                                        )
                                    }
                                }

                                if (isPrimary) {
                                    Row(
                                        modifier = Modifier
                                            .background(colors.primaryContainer, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = colors.primary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = DurgaStrings.get("contact_primary_badge", currentLanguage),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = colors.primary,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                } else {
                                    Text(
                                        text = DurgaStrings.get("contact_backup_badge", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textSecondary,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier
                                            .background(colors.surfaceVariant, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            HorizontalDivider(color = colors.outline.copy(alpha = 0.5f))

                            // Action buttons: Reorder, Edit, Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Priority reorder
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Priority: ", fontSize = 12.sp, color = colors.textSecondary)
                                    IconButton(
                                        onClick = {
                                            val index = contacts.indexOfFirst { it.id == contact.id }
                                            if (index > 0) {
                                                val mutable = contacts.map { it.id }.toMutableList()
                                                val temp = mutable[index]
                                                mutable[index] = mutable[index - 1]
                                                mutable[index - 1] = temp
                                                onReorderContacts(mutable)
                                            }
                                        },
                                        enabled = contact.priority > 1,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", modifier = Modifier.size(18.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            val index = contacts.indexOfFirst { it.id == contact.id }
                                            if (index < contacts.size - 1) {
                                                val mutable = contacts.map { it.id }.toMutableList()
                                                val temp = mutable[index]
                                                mutable[index] = mutable[index + 1]
                                                mutable[index + 1] = temp
                                                onReorderContacts(mutable)
                                            }
                                        },
                                        enabled = contact.priority < contacts.size,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", modifier = Modifier.size(18.dp))
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { editingContact = contact },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Edit", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { contactToDelete = contact },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DurgaEmergencyRed),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delete", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. TEST SOS SETUP BUTTON
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = colors.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DurgaStrings.get("test_sos_title", currentLanguage),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colors.textPrimary
                            )
                        }

                        Text(
                            text = DurgaStrings.get("test_sos_disclaimer", currentLanguage),
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )

                        Button(
                            onClick = {
                                val result = onRunTestSos()
                                testResultDialog = result
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_sos_test_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(DurgaStrings.get("test_sos_btn", currentLanguage))
                        }
                    }
                }
            }
        }
    }

    // ADD / EDIT CONTACT DIALOG
    if (showAddDialog || editingContact != null) {
        val isEditing = editingContact != null
        var nameInput by remember { mutableStateOf(editingContact?.name ?: "") }
        var phoneInput by remember { mutableStateOf(editingContact?.phoneNumber ?: "") }
        var relationInput by remember { mutableStateOf(editingContact?.relationship ?: "Family") }
        var whatsAppOpt by remember { mutableStateOf(editingContact?.isWhatsAppAvailable ?: true) }
        var validationError by remember { mutableStateOf<String?>(null) }
        var showConfirmationStep by remember { mutableStateOf(false) }

        val relations = listOf("Family", "Mother", "Father", "Sister", "Brother", "Spouse", "Friend", "Trusted Person")

        if (!showConfirmationStep) {
            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    editingContact = null
                },
                title = {
                    Text(
                        text = if (isEditing) DurgaStrings.get("edit_contact_title", currentLanguage) else DurgaStrings.get("add_contact_title", currentLanguage),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(DurgaStrings.get("contact_name_label", currentLanguage)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = {
                                phoneInput = it
                                validationError = null
                            },
                            label = { Text(DurgaStrings.get("contact_phone_label", currentLanguage)) },
                            placeholder = { Text("+91 9876543210") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            isError = validationError != null,
                            supportingText = {
                                if (validationError != null) {
                                    Text(validationError!!, color = DurgaEmergencyRed)
                                } else {
                                    Text("Valid mobile/phone with 7-15 digits")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Relationship:", fontSize = 12.sp, color = colors.textSecondary)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(relations) { r ->
                                FilterChip(
                                    selected = relationInput == r,
                                    onClick = { relationInput = r },
                                    label = { Text(r, fontSize = 12.sp) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(DurgaStrings.get("contact_whatsapp_opt", currentLanguage), fontSize = 13.sp)
                            Switch(
                                checked = whatsAppOpt,
                                onCheckedChange = { whatsAppOpt = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF25D366))
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nameInput.isBlank()) {
                                validationError = "Please enter contact name"
                                return@Button
                            }
                            if (!SecurityVault.validatePhoneNumber(phoneInput)) {
                                validationError = "Invalid phone number. Digits only, 7-15 length."
                                return@Button
                            }
                            showConfirmationStep = true
                        }
                    ) {
                        Text("Next")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showAddDialog = false
                        editingContact = null
                    }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            // Confirmation dialog before saving
            AlertDialog(
                onDismissRequest = { showConfirmationStep = false },
                title = { Text("Confirm Contact Details", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Please confirm the entered contact information before saving:")
                        Card(
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Name: $nameInput", fontWeight = FontWeight.Bold)
                                Text("Phone: ${SecurityVault.normalizePhoneNumber(phoneInput)}", color = colors.primary, fontWeight = FontWeight.Bold)
                                Text("Relationship: $relationInput")
                                Text("WhatsApp Enabled: ${if (whatsAppOpt) "Yes" else "No"}")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (isEditing) {
                                val updated = editingContact!!.copy(
                                    name = nameInput,
                                    phoneNumber = phoneInput,
                                    relationship = relationInput,
                                    isWhatsAppAvailable = whatsAppOpt
                                )
                                val (ok, err) = onUpdateContact(updated)
                                if (ok) {
                                    Toast.makeText(context, "Contact updated successfully", Toast.LENGTH_SHORT).show()
                                    showAddDialog = false
                                    editingContact = null
                                } else {
                                    Toast.makeText(context, err ?: "Update failed", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val (ok, err) = onAddContact(nameInput, phoneInput, relationInput, whatsAppOpt)
                                if (ok) {
                                    Toast.makeText(context, "Contact added successfully", Toast.LENGTH_SHORT).show()
                                    showAddDialog = false
                                } else {
                                    Toast.makeText(context, err ?: "Add failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Text("Confirm & Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmationStep = false }) {
                        Text("Back to Edit")
                    }
                }
            )
        }
    }

    // DELETE CONFIRMATION DIALOG
    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { contactToDelete = null },
            title = { Text("Delete Emergency Contact", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to remove ${contact.name} (${contact.phoneNumber}) from your trusted emergency contacts?",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteContact(contact.id)
                        contactToDelete = null
                        Toast.makeText(context, "Contact removed", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DurgaEmergencyRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { contactToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // TEST SOS SETUP RESULT DIALOG
    testResultDialog?.let { result ->
        AlertDialog(
            onDismissRequest = { testResultDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (result.isFullyConfigured) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (result.isFullyConfigured) DurgaSuccessGreen else Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SOS Diagnostic Report", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Text(
                            text = "DIAGNOSTIC TEST ONLY — NO REAL ALERTS SENT",
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            fontSize = 11.sp
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }

                    items(result.items) { item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isPassed && !item.isWarning) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (item.isPassed && !item.isWarning) DurgaSuccessGreen.copy(alpha = 0.5f) else Color(0xFFF59E0B)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (item.isPassed && !item.isWarning) Icons.Default.CheckCircle else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (item.isPassed && !item.isWarning) DurgaSuccessGreen else Color(0xFFD97706),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Text(item.detail, fontSize = 11.sp, color = colors.textSecondary)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Sample Generated Message:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = result.sampleMessage,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { testResultDialog = null }) {
                    Text("Close Test Report")
                }
            }
        )
    }
}
