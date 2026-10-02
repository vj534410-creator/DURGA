package com.example.durga.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class IncidentCategory(val key: String, val titleEn: String, val iconName: String) {
    CYBER_ONLINE("cyber", "Cyber & Online Harassment", "ic_cyber"),
    HOME_DOMESTIC("domestic", "Home & Domestic Safety", "ic_home"),
    SCHOOL_COLLEGE("education", "School & College Safety", "ic_school"),
    WORKPLACE("workplace", "Workplace Harassment", "ic_work"),
    PUBLIC_CITY("public", "Public Space & Stalking", "ic_public"),
    CHILD_SAFETY("child", "Child Safety & Protection", "ic_child"),
    FINANCIAL_FRAUD("fraud", "Digital & UPI Fraud", "ic_fraud"),
    WITNESS("witness", "Witnessed Incident", "ic_witness")
}

enum class CaseStatus(val displayName: String) {
    REPORT_SUBMITTED("Report Submitted"),
    RECEIVED("Received by Support Desk"),
    ASSIGNED("Assigned to Official Officer"),
    UPDATE_AVAILABLE("Official Update Available"),
    ACTION_RECORDED("Action Recorded"),
    CLOSED("Case Closed")
}

enum class EvidenceType(val displayName: String) {
    PHOTO("Photo"),
    VIDEO("Video"),
    AUDIO("Audio Recording"),
    SCREENSHOT("Screenshot"),
    DOCUMENT("Document")
}

enum class AuthorityRole(val title: String) {
    SUPER_ADMIN("Super Administrator"),
    AUTHORITY_STAFF("Authority Officer"),
    SUPPORT_STAFF("Support Staff"),
    AUDITOR("Independent Auditor")
}

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey val caseId: String = generateCaseId(),
    val anonymousDeviceId: String = "",
    val category: String,
    val title: String,
    val originalDescription: String,
    val structuredDescription: String,
    val originalLanguage: String,
    val translatedLanguage: String = "English",
    val incidentDate: String,
    val incidentTime: String,
    val incidentLocation: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val status: String = CaseStatus.REPORT_SUBMITTED.name,
    val isWitnessReport: Boolean = false,
    val complainantName: String = "",
    val complainantAge: String = "",
    val complainantGender: String = "",
    val complainantState: String = "",
    val complainantDistrict: String = "",
    val complainantMobile: String = "",
    val sharedItemsSummary: String = "Complaint, Location",
    val secureToken: String = UUID.randomUUID().toString(),
    val policeStation: String = "",
    val policeDistrict: String = "",
    val assignedInspector: String = "",
    val investigatingOfficer: String = "",
    val assignedAt: Long = 0L,
    val latestAuthorityNote: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSubmittedToServer: Boolean = false,
    val serverSyncError: String? = null
) {
    companion object {
        fun generateCaseId(): String {
            val randomPart = (100000..999999).random()
            return "DURGA-2026-$randomPart"
        }
    }
}

@Entity(tableName = "evidence")
data class EvidenceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val caseId: String,
    val type: String,
    val fileName: String,
    val localUri: String,
    val mimeType: String,
    val fileSizeBytes: Long,
    val sha256Checksum: String,
    val isEncryptedLocally: Boolean = true,
    val encryptedFilePath: String = "",
    val isEncryptedOnDisk: Boolean = true,
    val isSelectedForSharing: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trusted_contacts")
data class TrustedContactEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phoneNumber: String,
    val relationship: String = "Family",
    val isSosActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "draft_complaints")
data class DraftComplaintEntity(
    @PrimaryKey val draftId: String = UUID.randomUUID().toString(),
    val category: String = IncidentCategory.CYBER_ONLINE.name,
    val rawNotes: String = "",
    val aiDraftTitle: String = "",
    val aiDraftSummary: String = "",
    val aiDraftDescription: String = "",
    val missingQuestions: String = "",
    val incidentDate: String = "",
    val incidentTime: String = "",
    val incidentLocation: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val complainantName: String = "",
    val complainantAge: String = "",
    val complainantGender: String = "",
    val complainantState: String = "",
    val complainantDistrict: String = "",
    val complainantMobile: String = "",
    val shareComplaint: Boolean = true,
    val sharePhotos: Boolean = true,
    val shareAudio: Boolean = false,
    val shareLocation: Boolean = true,
    val isWitness: Boolean = false,
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(tableName = "case_updates")
data class CaseUpdateEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val caseId: String,
    val status: String,
    val message: String,
    val updatedBy: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "official_services")
data class OfficialServiceEntity(
    @PrimaryKey val id: String,
    val serviceName: String,
    val category: String, // Police, Women, Child, Medical, Cyber, Fire, Helpline
    val region: String = "National",
    val phone: String,
    val website: String,
    val description: String,
    val verificationSource: String = "Govt of India / MoWCD / MHA",
    val verifiedDate: String = "2026-01-01",
    val isActive: Boolean = true
)

@Entity(tableName = "safety_card")
data class SafetyCardEntity(
    @PrimaryKey val id: Int = 1,
    val encryptedName: String = "",
    val bloodGroup: String = "",
    val medicalNotes: String = "",
    val preferredLanguage: String = "English",
    val emergencyInstructions: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_access_logs")
data class AuditAccessLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val caseId: String,
    val authorityId: String,
    val role: String,
    val action: String, // VIEW_CASE, VIEW_EVIDENCE, UPDATE_STATUS, EXPORT_SUMMARY
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class NearbyPlace(
    val id: String,
    val name: String,
    val type: String, // Police, Hospital, Women Support Center, Fire, Child Helpline
    val address: String,
    val distanceKm: Double,
    val phoneNumber: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen24x7: Boolean = true
)

data class AiGeneratedDraft(
    val title: String,
    val incidentSummary: String,
    val detailedDescription: String,
    val missingQuestions: List<String> = emptyList(),
    val suggestedLegalSafeguards: String = ""
)

@Entity(tableName = "case_notifications")
data class CaseNotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val caseId: String = "",
    val title: String,
    val category: String = "GENERAL",
    val progressPercent: Int = 100, // 0 - 100% work completed
    val stageTitle: String = "", // Current stage of investigation or version tag
    val isActionTaken: Boolean = false, // Whether police took active action
    val actionStatusText: String = "", // Display status of the action
    val actionDetails: String = "", // Specific police/authority action taken or feature release details
    val officerName: String = "Insp. R.K. Rathod",
    val policeStation: String = "Women Safety & Cyber Cell",
    val fullSummary: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val notificationType: String = "CASE_UPDATE", // "CASE_UPDATE", "APP_FEATURE", "SYSTEM_UPDATE"
    val targetRoute: String = "" // e.g. "web_portals", "emergency", "private_vault", "case_details"
)

data class VerifiedCaseRecord(
    val caseId: String,
    val status: String,
    val category: String,
    val title: String,
    val summary: String,
    val incidentDate: String,
    val incidentTime: String,
    val incidentLocation: String,
    val createdAt: Long,
    val submittedAt: Long,
    val policeStation: String? = null,
    val district: String? = null,
    val assignedInspector: String? = null,
    val investigatingOfficer: String? = null,
    val assignedAt: Long? = null,
    val photoCount: Int = 0,
    val videoCount: Int = 0,
    val audioCount: Int = 0,
    val documentCount: Int = 0,
    val totalEvidenceCount: Int = 0,
    val timeline: List<VerifiedTimelineEvent> = emptyList(),
    val secureToken: String = "",
    val isQrActive: Boolean = true
)

data class VerifiedTimelineEvent(
    val id: String,
    val title: String,
    val description: String,
    val actorName: String,
    val actorRole: String,
    val timestamp: Long
)

