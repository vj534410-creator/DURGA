package com.example.durga.repository

import android.content.Context
import com.example.durga.database.DurgaDatabase
import com.example.durga.database.SecurityVault
import com.example.durga.model.AiGeneratedDraft
import com.example.durga.model.AuditAccessLogEntity
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseNotificationEntity
import com.example.durga.model.CaseStatus
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.NearbyPlace
import com.example.durga.model.OfficialServiceEntity
import com.example.durga.model.SafetyCardEntity
import com.example.durga.network.DurgaBackendClient
import com.example.durga.network.GeminiComplaintWriter
import com.example.durga.notification.DurgaNotificationHelper
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
    private val notificationDao = db.notificationDao()

    init {
        // Seed official services & sample notifications if empty
        CoroutineScope(Dispatchers.IO).launch {
            if (serviceDao.getServicesCount() == 0) {
                serviceDao.insertServices(DurgaBackendClient.getVerifiedIndianEmergencyServices())
            }
            seedSampleNotificationsIfEmpty()
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

        // Post initial notification for newly filed complaint
        val initialNotification = CaseNotificationEntity(
            caseId = newCaseId,
            title = caseEntity.title,
            category = caseEntity.category,
            progressPercent = 20,
            stageTitle = "ફરિયાદ દાખલ થઈ (Complaint Lodged)",
            isActionTaken = false,
            actionStatusText = "તપાસ પ્રાથમિક તબક્કે (Desk Review Started)",
            actionDetails = "તમારી ફરિયાદ DURGA કેન્દ્રીય સિસ્ટમ પર સત્તાવાર રીતે નોંધવામાં આવી છે. કેસ આઈડી $newCaseId ફાળવવામાં આવ્યો છે. સંબંધિત મહિલા સહાયતા ડેસ્કને જાણ કરાઈ છે.",
            officerName = "Duty Desk Officer",
            policeStation = "Central Women Safety & Cyber Cell",
            fullSummary = "Initial filing confirmed at 20% progress.",
            isRead = false,
            timestamp = System.currentTimeMillis()
        )
        notificationDao.insertNotification(initialNotification)
        DurgaNotificationHelper.postCaseProgressNotification(context, initialNotification)

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

            val existingCase = caseDao.getCaseById(caseId)
            val (progress, isAction, actionStatus) = when (newStatus) {
                CaseStatus.RECEIVED.name, CaseStatus.RECEIVED.displayName -> Triple(35, false, "ડેસ્ક દ્વારા સ્વીકૃત (Received)")
                CaseStatus.ASSIGNED.name, CaseStatus.ASSIGNED.displayName -> Triple(55, false, "તપાસ અધિકારી નિયુક્ત (Officer Assigned)")
                CaseStatus.UPDATE_AVAILABLE.name, CaseStatus.UPDATE_AVAILABLE.displayName -> Triple(75, true, "તપાસ પ્રગતિમાં (Investigation Active)")
                CaseStatus.ACTION_RECORDED.name, CaseStatus.ACTION_RECORDED.displayName -> Triple(90, true, "પોલીસ કાર્યવાહી સંપન્ન (Police Action Taken)")
                CaseStatus.CLOSED.name, CaseStatus.CLOSED.displayName -> Triple(100, true, "કેસ પૂર્ણ (Case Resolved)")
                else -> Triple(60, false, "અપડેટ ઉપલબ્ધ (Update Available)")
            }

            val notification = CaseNotificationEntity(
                caseId = caseId,
                title = existingCase?.title ?: "Case Update: $caseId",
                category = existingCase?.category ?: "Safety",
                progressPercent = progress,
                stageTitle = newStatus,
                isActionTaken = isAction,
                actionStatusText = actionStatus,
                actionDetails = message,
                officerName = updatedBy,
                policeStation = "Women Safety & Cyber Cell",
                fullSummary = message,
                isRead = false,
                timestamp = System.currentTimeMillis()
            )
            notificationDao.insertNotification(notification)
            DurgaNotificationHelper.postCaseProgressNotification(context, notification)
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

    // --- Case Notifications & Police Actions Tracking ---
    val allNotifications: Flow<List<CaseNotificationEntity>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    fun getNotificationsForCase(caseId: String): Flow<List<CaseNotificationEntity>> = notificationDao.getNotificationsForCase(caseId)

    suspend fun insertNotification(notification: CaseNotificationEntity) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(notification)
        DurgaNotificationHelper.postCaseProgressNotification(context, notification)
    }

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun deleteNotification(id: String) = withContext(Dispatchers.IO) {
        notificationDao.deleteNotification(id)
    }

    suspend fun deleteAllNotifications() = withContext(Dispatchers.IO) {
        notificationDao.deleteAllNotifications()
    }

    private data class ProgressionStep(
        val progress: Int,
        val stageTitle: String,
        val isAction: Boolean,
        val actionStatus: String,
        val actionDetails: String,
        val officer: String,
        val station: String
    )

    suspend fun simulatePoliceProgression(targetCaseId: String? = null): CaseNotificationEntity = withContext(Dispatchers.IO) {
        val cases = caseDao.getAllCases().first()
        val targetCase = if (!targetCaseId.isNullOrBlank()) {
            caseDao.getCaseById(targetCaseId) ?: cases.firstOrNull()
        } else {
            cases.firstOrNull()
        }

        val caseId = targetCase?.caseId ?: run {
            val newId = CaseEntity.generateCaseId()
            val sample = CaseEntity(
                caseId = newId,
                category = "CYBER_ONLINE",
                title = "ઓનલાઇન હેરાનગતિ અને ધમકી રિપોર્ટ",
                originalDescription = "સોશિયલ મીડિયા પર અજાણ્યા નંબરથી વારંવાર કોલ અને મેસેજ આવી રહ્યા છે.",
                structuredDescription = "ફરિયાદી મહિલાને અજાણ્યા સ્ત્રોત દ્વારા સતત અસભ્ય મેસેજ અને ધમકી મળવા બાબતે.",
                originalLanguage = "Gujarati",
                incidentDate = "25/09/2026",
                incidentTime = "10:30 AM",
                incidentLocation = "અમદાવાદ",
                status = CaseStatus.REPORT_SUBMITTED.name
            )
            caseDao.insertCase(sample)
            newId
        }

        val pastNotifications = notificationDao.getNotificationsForCase(caseId).first()
        val maxProgress = pastNotifications.maxOfOrNull { it.progressPercent } ?: 20

        val step = when {
            maxProgress < 30 -> ProgressionStep(
                progress = 45,
                stageTitle = "તપાસ અધિકારી સોંપાયા (Officer Assigned & Preliminary Verification)",
                isAction = false,
                actionStatus = "તપાસ ચાલુ (Under Investigation)",
                actionDetails = "પી.આઈ. એમ. કે. સોલંકી અને મહિલા સહાયક સ્ટાફને કેસ સોંપવામાં આવ્યો. GD એન્ટ્રી નં. 418/2026 દાખલ કરી પ્રાથમિક ચકાસણી શરૂ થઈ.",
                officer = "PI M.K. Solanki (Cyber Unit)",
                station = "Women Safety & Cyber Crime Police Station"
            )
            maxProgress < 60 -> ProgressionStep(
                progress = 70,
                stageTitle = "સ્થળ મુલાકાત & ડિજિટલ પુરાવા ચકાસણી (Site Inspection & Evidence Verification)",
                isAction = false,
                actionStatus = "પુરાવા તપાસ પ્રગતિમાં (Evidence Verified)",
                actionDetails = "તપાસ ટીમે ઘટના સ્થળની મુલાકાત લીધી. સીસીટીવી ફૂટેજ, કોલ રેકોર્ડ્સ અને સ્ક્રીનશોટ્સ ડિજિટલ ફોરેન્સિક લેબમાં મોકલવામાં આવ્યા.",
                officer = "PSI V.P. Rathod & Team",
                station = "Special Women Protection Squad"
            )
            maxProgress < 85 -> ProgressionStep(
                progress = 90,
                stageTitle = "પોલીસ કાર્યવાહી સંપન્ન (Police Action Taken & Safeguards Enacted)",
                isAction = true,
                actionStatus = "પોલીસ કાર્યવાહી લેવાઈ (ACTION TAKEN)",
                actionDetails = "આરોપીની ઓળખ કરી કલમ 354D/79 BNS હેઠળ કાનૂની નોટિસ બજાવી. ફરિયાદકર્તાના વિસ્તારમાં PCR વાનનું પેટ્રોલીંગ વધારી દેવામાં આવ્યું છે.",
                officer = "Senior Inspector S.A. Jadeja",
                station = "City Police Command & Control"
            )
            else -> ProgressionStep(
                progress = 100,
                stageTitle = "કેસ પૂર્ણ અને એક્શન રિપોર્ટ સુપરત (Case Resolved & Final Action Report)",
                isAction = true,
                actionStatus = "સંપૂર્ણ કાર્યવાહી પૂર્ણ (Case Resolved)",
                actionDetails = "પોલીસ દ્વારા તમામ કાનૂની પગલાં સફળતાપૂર્વક લેવાઈ ગયા છે. આરોપી વિરુદ્ધ ચાર્જશીટ/બાંહેધરી લેવાઈ છે. ફરિયાદકર્તાની સંપૂર્ણ સુરક્ષા સુનિશ્ચિત કરી કેસ સંપન્ન જાહેર કરાયો છે.",
                officer = "DCP Women & Child Protection",
                station = "State Police Headquarter"
            )
        }

        val newNotification = CaseNotificationEntity(
            caseId = caseId,
            title = targetCase?.title ?: "કેસ તપાસ અપડેટ: $caseId",
            category = targetCase?.category ?: "CYBER_ONLINE",
            progressPercent = step.progress,
            stageTitle = step.stageTitle,
            isActionTaken = step.isAction,
            actionStatusText = step.actionStatus,
            actionDetails = step.actionDetails,
            officerName = step.officer,
            policeStation = step.station,
            fullSummary = step.actionDetails,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )

        notificationDao.insertNotification(newNotification)
        caseDao.updateCaseStatus(caseId, if (step.isAction) CaseStatus.ACTION_RECORDED.name else CaseStatus.UPDATE_AVAILABLE.name)
        caseUpdateDao.insertUpdate(
            CaseUpdateEntity(
                caseId = caseId,
                status = step.stageTitle,
                message = step.actionDetails,
                updatedBy = step.officer
            )
        )
        DurgaNotificationHelper.postCaseProgressNotification(context, newNotification)
        newNotification
    }

    suspend fun seedSampleNotificationsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = notificationDao.getAllNotifications().first()
        if (existing.isEmpty()) {
            val sampleCaseId = "DURGA-2026-784291"
            val sampleNotifications = listOf(
                CaseNotificationEntity(
                    caseId = sampleCaseId,
                    title = "ઓનલાઇન પીછો અને ધમકીની ફરિયાદ (Cyber Harassment)",
                    category = "CYBER_ONLINE",
                    progressPercent = 90,
                    stageTitle = "પોલીસ કાર્યવાહી સંપન્ન (Police Action Taken)",
                    isActionTaken = true,
                    actionStatusText = "કાર્યવાહી લેવાઈ (Action Taken)",
                    actionDetails = "આરોપીને પોલીસ સ્ટેશન બોલાવી ભારતીય ન્યાય સંહિતાની સંબંધિત કલમો હેઠળ આકરી ચેતવણી અને 41A CrPC નોટિસ પાઠવવામાં આવી. ફરિયાદકર્તાના વિસ્તારમાં નિયમિત રાત્રિ પેટ્રોલીંગ ગોઠવવામાં આવ્યું છે.",
                    officerName = "PI એમ.કે. સોલંકી (તપાસ અધિકારી)",
                    policeStation = "મહિલા સાયબર ક્રાઈમ સેલ, પશ્ચિમ ડિવિઝન",
                    fullSummary = "Police action completed with notice issued and security patrolling assigned.",
                    isRead = false,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 35)
                ),
                CaseNotificationEntity(
                    caseId = sampleCaseId,
                    title = "ઓનલાઇન પીછો અને ધમકીની ફરિયાદ (Cyber Harassment)",
                    category = "CYBER_ONLINE",
                    progressPercent = 70,
                    stageTitle = "ડિજિટલ પુરાવા ચકાસણી (Digital Evidence Verified)",
                    isActionTaken = false,
                    actionStatusText = "પુરાવા ચકાસાયા (Evidence Verified)",
                    actionDetails = "સાયબર એક્સપર્ટ દ્વારા શંકાસ્પદ IP સરનામું અને મોબાઈલ નંબરની CDR/IPDR વિગતો ચકાસવામાં આવી છે અને શંકાસ્પદ વ્યક્તિનું લોકેશન ટ્રેક થઈ ગયું છે.",
                    officerName = "PSI વી.પી. રાઠોડ",
                    policeStation = "સાયબર સેલ ટેકનિકલ શાખા",
                    fullSummary = "IP and CDR evidence analyzed successfully.",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 120)
                ),
                CaseNotificationEntity(
                    caseId = sampleCaseId,
                    title = "ઓનલાઇન પીછો અને ધમકીની ફરિયાદ (Cyber Harassment)",
                    category = "CYBER_ONLINE",
                    progressPercent = 45,
                    stageTitle = "તપાસ અધિકારી સોંપાયા (Officer Assigned)",
                    isActionTaken = false,
                    actionStatusText = "તપાસ ચાલુ (Officer Assigned)",
                    actionDetails = "તમારી ફરિયાદ સાયબર ક્રાઈમ બ્રાંચ અને મહિલા સુરક્ષા સેલમાં રજીસ્ટર થઈ. PI સોલંકીને તપાસ સોંપાઈ.",
                    officerName = "ડ્યુટી ઈન્સ્પેક્ટર",
                    policeStation = "કેન્દ્રીય મહિલા સુરક્ષા કમાન્ડ",
                    fullSummary = "Assigned to investigating officer.",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 300)
                ),
                CaseNotificationEntity(
                    caseId = sampleCaseId,
                    title = "ઓનલાઇન પીછો અને ધમકીની ફરિયાદ (Cyber Harassment)",
                    category = "CYBER_ONLINE",
                    progressPercent = 20,
                    stageTitle = "ફરિયાદ દાખલ થઈ (Complaint Lodged)",
                    isActionTaken = false,
                    actionStatusText = "ડેસ્ક સમીક્ષા (Desk Review)",
                    actionDetails = "DURGA સિસ્ટમ પર ફરિયાદ સફળતાપૂર્વક નોંધાઈ. ગોપનીય કેસ આઈડી DURGA-2026-784291 ફાળવાયો.",
                    officerName = "સિસ્ટમ એડમિન",
                    policeStation = "DURGA કંટ્રોલ રૂમ",
                    fullSummary = "Initial filing confirmed.",
                    isRead = true,
                    timestamp = System.currentTimeMillis() - (1000 * 60 * 600)
                )
            )
            notificationDao.insertNotifications(sampleNotifications)
        }
    }
}
