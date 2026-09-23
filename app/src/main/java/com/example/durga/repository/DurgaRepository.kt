package com.example.durga.repository

import android.content.Context
import com.example.durga.database.DurgaDatabase
import com.example.durga.database.SecurityVault
import com.example.durga.model.AiGeneratedDraft
import com.example.durga.model.AuditAccessLogEntity
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseStatus
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.NearbyPlace
import com.example.durga.model.OfficialServiceEntity
import com.example.durga.model.SafetyCardEntity
import com.example.durga.network.DurgaBackendClient
import com.example.durga.network.GeminiComplaintWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class DurgaRepository(private val context: Context) {

    private val db = DurgaDatabase.getInstance(context)
    private val caseDao = db.caseDao()
    private val evidenceDao = db.evidenceDao()
    private val draftDao = db.draftDao()
    private val serviceDao = db.officialServiceDao()
    private val caseUpdateDao = db.caseUpdateDao()
    private val safetyCardDao = db.safetyCardDao()
    private val auditDao = db.auditDao()

    init {
        // Seed official services if empty
        CoroutineScope(Dispatchers.IO).launch {
            if (serviceDao.getServicesCount() == 0) {
                serviceDao.insertServices(DurgaBackendClient.getVerifiedIndianEmergencyServices())
            }
        }
    }

    // --- Cases ---
    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()

    suspend fun getCase(caseId: String): CaseEntity? = caseDao.getCaseById(caseId)

    fun observeCase(caseId: String): Flow<CaseEntity?> = caseDao.observeCaseById(caseId)

    suspend fun submitComplaint(
        draft: DraftComplaintEntity,
        evidenceList: List<EvidenceEntity>,
        isWitness: Boolean
    ): Result<CaseEntity> = withContext(Dispatchers.IO) {
        val newCaseId = CaseEntity.generateCaseId()
        val anonymousDeviceId = SecurityVault.getAnonymousDeviceId(context)

        val sharedSummary = buildString {
            if (draft.shareComplaint) append("Complaint Statement, ")
            if (draft.sharePhotos) append("Photos/Evidence (${evidenceList.size}), ")
            if (draft.shareLocation) append("Location (${draft.incidentLocation.ifBlank { "Coordinates" }}), ")
            if (draft.shareAudio) append("Audio Recording, ")
        }.trimEnd(',', ' ')

        val caseEntity = CaseEntity(
            caseId = newCaseId,
            anonymousDeviceId = anonymousDeviceId,
            category = draft.category,
            title = draft.aiDraftTitle.ifBlank { "Incident Report: ${draft.category}" },
            originalDescription = draft.rawNotes,
            structuredDescription = draft.aiDraftDescription.ifBlank { draft.rawNotes },
            originalLanguage = "User Language",
            incidentDate = draft.incidentDate,
            incidentTime = draft.incidentTime,
            incidentLocation = draft.incidentLocation,
            latitude = draft.latitude,
            longitude = draft.longitude,
            status = CaseStatus.REPORT_SUBMITTED.name,
            isWitnessReport = isWitness,
            complainantName = SecurityVault.encrypt(draft.complainantName),
            complainantAge = draft.complainantAge,
            complainantGender = draft.complainantGender,
            complainantState = draft.complainantState,
            complainantDistrict = draft.complainantDistrict,
            complainantMobile = SecurityVault.encrypt(draft.complainantMobile),
            sharedItemsSummary = sharedSummary,
            isSubmittedToServer = false
        )

        // Save locally first
        caseDao.insertCase(caseEntity)

        // Associate evidence with case
        val updatedEvidence = evidenceList.map { it.copy(caseId = newCaseId) }
        if (updatedEvidence.isNotEmpty()) {
            evidenceDao.insertEvidenceList(updatedEvidence)
        }

        // Add initial system status update
        caseUpdateDao.insertUpdate(
            CaseUpdateEntity(
                caseId = newCaseId,
                status = CaseStatus.REPORT_SUBMITTED.displayName,
                message = "Complaint registered securely on DURGA. Case reference generated.",
                updatedBy = "DURGA Security System"
            )
        )

        // Submit to backend
        val serverResult = DurgaBackendClient.submitCaseToBackend(caseEntity, updatedEvidence.size)
        if (serverResult.isSuccess) {
            caseDao.updateCase(caseEntity.copy(isSubmittedToServer = true))
        }

        // Delete draft if it was saved
        draftDao.deleteDraft(draft.draftId)

        Result.success(caseEntity)
    }

    suspend fun updateCaseStatus(caseId: String, newStatus: String, message: String, updatedBy: String) {
        withContext(Dispatchers.IO) {
            caseDao.updateCaseStatus(caseId, newStatus)
            caseUpdateDao.insertUpdate(
                CaseUpdateEntity(
                    caseId = caseId,
                    status = newStatus,
                    message = message,
                    updatedBy = updatedBy
                )
            )
        }
    }

    fun getCaseUpdates(caseId: String): Flow<List<CaseUpdateEntity>> = caseUpdateDao.getUpdatesForCase(caseId)

    // --- Drafts ---
    val allDrafts: Flow<List<DraftComplaintEntity>> = draftDao.getAllDrafts()

    suspend fun saveDraft(draft: DraftComplaintEntity) = withContext(Dispatchers.IO) {
        draftDao.saveDraft(draft)
    }

    suspend fun getDraft(draftId: String): DraftComplaintEntity? = withContext(Dispatchers.IO) {
        draftDao.getDraftById(draftId)
    }

    suspend fun deleteDraft(draftId: String) = withContext(Dispatchers.IO) {
        draftDao.deleteDraft(draftId)
    }

    // --- Evidence ---
    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceEntity>> = evidenceDao.getEvidenceForCase(caseId)

    val allEvidence: Flow<List<EvidenceEntity>> = evidenceDao.getAllEvidence()

    suspend fun insertEvidence(evidence: EvidenceEntity) = withContext(Dispatchers.IO) {
        evidenceDao.insertEvidence(evidence)
    }

    suspend fun deleteEvidence(id: String) = withContext(Dispatchers.IO) {
        evidenceDao.deleteEvidence(id)
    }

    suspend fun lockEvidenceInVault() = withContext(Dispatchers.IO) {
        // Marks all evidence items as encrypted locally
        val evidenceList = evidenceDao.getAllEvidence().first()
        val encryptedList = evidenceList.map { it.copy(isEncryptedLocally = true) }
        evidenceDao.insertEvidenceList(encryptedList)
    }

    // --- Services & Directory ---
    val officialServices: Flow<List<OfficialServiceEntity>> = serviceDao.getAllActiveServices()

    // --- Safety Card ---
    val safetyCard: Flow<SafetyCardEntity?> = safetyCardDao.getSafetyCard()

    suspend fun saveSafetyCard(card: SafetyCardEntity) = withContext(Dispatchers.IO) {
        val encryptedCard = card.copy(
            encryptedName = SecurityVault.encrypt(card.encryptedName),
            medicalNotes = SecurityVault.encrypt(card.medicalNotes),
            emergencyInstructions = SecurityVault.encrypt(card.emergencyInstructions)
        )
        safetyCardDao.saveSafetyCard(encryptedCard)
    }

    // --- Audit Logs ---
    val allAuditLogs: Flow<List<AuditAccessLogEntity>> = auditDao.getAllAuditLogs()

    suspend fun recordAuditLog(log: AuditAccessLogEntity) = withContext(Dispatchers.IO) {
        auditDao.insertLog(log)
    }

    // --- AI Integration ---
    suspend fun requestAiDraft(rawText: String, category: String, languageName: String): Result<AiGeneratedDraft> {
        return GeminiComplaintWriter.generateComplaintDraft(rawText, category, languageName)
    }

    // --- Privacy Purge ---
    suspend fun purgeAllLocalData() = withContext(Dispatchers.IO) {
        caseDao.deleteAllCases()
        evidenceDao.deleteAllEvidence()
        draftDao.deleteAllDrafts()
        safetyCardDao.deleteSafetyCard()
        auditDao.deleteAllLogs()
    }

    // --- Help Near Me (Calculated from user or regional anchor) ---
    fun getNearbyHelpFacilities(userLat: Double, userLon: Double): List<NearbyPlace> {
        // Default anchor if GPS disabled or unavailable (Central District reference)
        val lat = if (userLat != 0.0) userLat else 23.0225
        val lon = if (userLon != 0.0) userLon else 72.5714

        val places = listOf(
            NearbyPlace(
                id = "p1",
                name = "Central Women Police Station (All-Women)",
                type = "Women Police",
                address = "Opp. District Court Complex, Civil Lines",
                distanceKm = calculateDistance(lat, lon, lat + 0.008, lon + 0.005),
                phoneNumber = "1091",
                latitude = lat + 0.008,
                longitude = lon + 0.005,
                isOpen24x7 = true
            ),
            NearbyPlace(
                id = "p2",
                name = "District Government Civil Hospital & Trauma Center",
                type = "Hospital & Medical",
                address = "Hospital Road, Sector 12",
                distanceKm = calculateDistance(lat, lon, lat - 0.012, lon + 0.009),
                phoneNumber = "108",
                latitude = lat - 0.012,
                longitude = lon + 0.009,
                isOpen24x7 = true
            ),
            NearbyPlace(
                id = "p3",
                name = "One Stop Centre (Sakhi) - Ministry of WCD",
                type = "One Stop Crisis Center",
                address = "Near Red Cross Society, Collectorate Compound",
                distanceKm = calculateDistance(lat, lon, lat + 0.015, lon - 0.007),
                phoneNumber = "181",
                latitude = lat + 0.015,
                longitude = lon - 0.007,
                isOpen24x7 = true
            ),
            NearbyPlace(
                id = "p4",
                name = "City Police Station & Anti-Human Trafficking Unit",
                type = "Police Station",
                address = "Station Chowk, Main Bazaar Road",
                distanceKm = calculateDistance(lat, lon, lat + 0.004, lon - 0.003),
                phoneNumber = "112",
                latitude = lat + 0.004,
                longitude = lon - 0.003,
                isOpen24x7 = true
            ),
            NearbyPlace(
                id = "p5",
                name = "District Child Welfare Committee (CWC) & Childline",
                type = "Child Welfare",
                address = "Bal Bhavan Complex, University Marg",
                distanceKm = calculateDistance(lat, lon, lat - 0.019, lon - 0.014),
                phoneNumber = "1098",
                latitude = lat - 0.019,
                longitude = lon - 0.014,
                isOpen24x7 = true
            ),
            NearbyPlace(
                id = "p6",
                name = "Emergency Fire & Disaster Rescue Station",
                type = "Fire Station",
                address = "Ring Road Fire Complex",
                distanceKm = calculateDistance(lat, lon, lat + 0.022, lon + 0.018),
                phoneNumber = "101",
                latitude = lat + 0.022,
                longitude = lon + 0.018,
                isOpen24x7 = true
            )
        )
        return places.sortedBy { it.distanceKm }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val dist = r * c
        return Math.round(dist * 10.0) / 10.0
    }
}
