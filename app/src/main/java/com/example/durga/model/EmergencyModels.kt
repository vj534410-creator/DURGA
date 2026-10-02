package com.example.durga.model

import com.example.durga.database.EmergencyContact

enum class SosCallState {
    NOT_STARTED,
    CALLING_PRIMARY,
    DIALER_OPENED,
    FAILED,
    PERMISSION_DENIED
}

enum class SosLocationState {
    PENDING,
    OBTAINED,
    UNAVAILABLE,
    PERMISSION_DENIED
}

enum class SosSmsState {
    NOT_STARTED,
    PREPARED_COMPOSER, // Truthful: "SMS ready to send"
    SENT_DIRECT,        // Only if direct sending was executed
    FAILED,
    NO_CONTACTS
}

enum class SosWhatsAppState {
    NOT_STARTED,
    OPENED_CHAT,       // Truthful: "Chat opened in WhatsApp"
    NOT_INSTALLED,
    NO_CONTACTS
}

enum class Sos112State {
    NOT_STARTED,
    DIALER_OPENED,
    CALLING
}

data class SosActiveSession(
    val incidentId: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val timestampDisplay: String,
    val isSilentMode: Boolean = false, // "I Can't Talk" mode
    val callState: SosCallState = SosCallState.NOT_STARTED,
    val callTargetName: String = "",
    val callTargetPhone: String = "",
    val locationState: SosLocationState = SosLocationState.PENDING,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val mapLink: String = "",
    val locationAccuracyText: String = "",
    val smsState: SosSmsState = SosSmsState.NOT_STARTED,
    val whatsAppState: SosWhatsAppState = SosWhatsAppState.NOT_STARTED,
    val erss112State: Sos112State = Sos112State.NOT_STARTED,
    val emergencyMessage: String = "",
    val primaryContact: EmergencyContact? = null,
    val backupContacts: List<EmergencyContact> = emptyList(),
    val isEmergencyActive: Boolean = true
)

data class SosTestCheckItem(
    val title: String,
    val detail: String,
    val isPassed: Boolean,
    val isWarning: Boolean = false
)

data class SosTestResult(
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<SosTestCheckItem>,
    val isFullyConfigured: Boolean,
    val sampleMessage: String
)
