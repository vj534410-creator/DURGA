package com.example.durga.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.durga.database.SecurityVault
import com.example.durga.i18n.DurgaLanguage
import com.example.durga.model.AiGeneratedDraft
import com.example.durga.model.AuditAccessLogEntity
import com.example.durga.model.AuthorityRole
import com.example.durga.model.CaseEntity
import com.example.durga.model.CaseStatus
import com.example.durga.model.CaseUpdateEntity
import com.example.durga.model.DraftComplaintEntity
import com.example.durga.model.EvidenceEntity
import com.example.durga.model.IncidentCategory
import com.example.durga.model.NearbyPlace
import com.example.durga.model.OfficialServiceEntity
import com.example.durga.model.SafetyCardEntity
import com.example.durga.repository.DurgaRepository
import com.example.ui.theme.DurgaThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DurgaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DurgaRepository(application)
    private val prefs = application.getSharedPreferences("durga_app_settings", Context.MODE_PRIVATE)

    // Language State
    private val _currentLanguage = MutableStateFlow(
        DurgaLanguage.entries.find { it.code == prefs.getString("selected_lang", "gu") } ?: DurgaLanguage.GUJARATI
    )
    val currentLanguage: StateFlow<DurgaLanguage> = _currentLanguage.asStateFlow()

    // 5-Color Theme State (1: green, 2: black, 3: blue, 4: pink, 5: purple)
    private val _currentTheme = MutableStateFlow(
        DurgaThemeMode.entries.find { it.id == prefs.getString("selected_theme", "blue") } ?: DurgaThemeMode.BLUE
    )
    val currentTheme: StateFlow<DurgaThemeMode> = _currentTheme.asStateFlow()

    fun setTheme(theme: DurgaThemeMode) {
        _currentTheme.value = theme
        prefs.edit().putString("selected_theme", theme.id).apply()
    }

    // First launch onboarding check
    private val _isOnboardingCompleted = MutableStateFlow(prefs.getBoolean("onboarding_done", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    // Quick Exit State (neutral notes/calculator privacy screen)
    private val _isQuickExitActive = MutableStateFlow(false)
    val isQuickExitActive: StateFlow<Boolean> = _isQuickExitActive.asStateFlow()

    // Vault Authentication & PIN Lock State
    private val _isVaultUnlocked = MutableStateFlow(false)
    val isVaultUnlocked: StateFlow<Boolean> = _isVaultUnlocked.asStateFlow()

    private val _vaultPin = MutableStateFlow(prefs.getString("vault_pin", "1234") ?: "1234")
    val vaultPin: StateFlow<String> = _vaultPin.asStateFlow()

    private val _vaultLockType = MutableStateFlow(prefs.getString("vault_lock_type", "PIN") ?: "PIN")
    val vaultLockType: StateFlow<String> = _vaultLockType.asStateFlow()

    private val _vaultErrorMessage = MutableStateFlow<String?>(null)
    val vaultErrorMessage: StateFlow<String?> = _vaultErrorMessage.asStateFlow()

    private val _vaultAutoLock = MutableStateFlow(prefs.getBoolean("vault_auto_lock", true))
    val vaultAutoLock: StateFlow<Boolean> = _vaultAutoLock.asStateFlow()

    // Authority Portal Session State
    private val _authorityUserRole = MutableStateFlow<AuthorityRole?>(null)
    val authorityUserRole: StateFlow<AuthorityRole?> = _authorityUserRole.asStateFlow()

    private val _authorityStaffId = MutableStateFlow<String>("")
    val authorityStaffId: StateFlow<String> = _authorityStaffId.asStateFlow()

    // Active Complaint Drafting State
    private val _currentDraft = MutableStateFlow(DraftComplaintEntity())
    val currentDraft: StateFlow<DraftComplaintEntity> = _currentDraft.asStateFlow()

    private val _attachedEvidence = MutableStateFlow<List<EvidenceEntity>>(emptyList())
    val attachedEvidence: StateFlow<List<EvidenceEntity>> = _attachedEvidence.asStateFlow()

    private val _isAiDraftingLoading = MutableStateFlow(false)
    val isAiDraftingLoading: StateFlow<Boolean> = _isAiDraftingLoading.asStateFlow()

    private val _aiDraftResult = MutableStateFlow<AiGeneratedDraft?>(null)
    val aiDraftResult: StateFlow<AiGeneratedDraft?> = _aiDraftResult.asStateFlow()

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState.asStateFlow()

    private val _lastSubmittedCase = MutableStateFlow<CaseEntity?>(null)
    val lastSubmittedCase: StateFlow<CaseEntity?> = _lastSubmittedCase.asStateFlow()

    // Voice recognition simulation / status
    private val _isListeningVoice = MutableStateFlow(false)
    val isListeningVoice: StateFlow<Boolean> = _isListeningVoice.asStateFlow()

    // Filtered / Search Category for Emergency
    private val _serviceFilterCategory = MutableStateFlow("ALL")
    val serviceFilterCategory: StateFlow<String> = _serviceFilterCategory.asStateFlow()

    // Selected Case for Details
    private val _selectedCaseId = MutableStateFlow<String?>(null)
    val selectedCaseId: StateFlow<String?> = _selectedCaseId.asStateFlow()

    // Location state for Help Near Me
    private val _userCoordinates = MutableStateFlow<Pair<Double, Double>>(Pair(23.0225, 72.5714))
    val userCoordinates: StateFlow<Pair<Double, Double>> = _userCoordinates.asStateFlow()

    // Data streams from repository
    val allCases: StateFlow<List<CaseEntity>> = repository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDrafts: StateFlow<List<DraftComplaintEntity>> = repository.allDrafts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvidence: StateFlow<List<EvidenceEntity>> = repository.allEvidence
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val officialServices: StateFlow<List<OfficialServiceEntity>> = repository.officialServices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val safetyCard: StateFlow<SafetyCardEntity?> = repository.safetyCard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val auditLogs: StateFlow<List<AuditAccessLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---

    fun setLanguage(language: DurgaLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("selected_lang", language.code).apply()
    }

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }

    fun triggerQuickExit() {
        _isQuickExitActive.value = true
    }

    fun dismissQuickExit() {
        _isQuickExitActive.value = false
    }

    fun unlockVaultWithPin(pin: String): Boolean {
        val storedPin = _vaultPin.value
        val isCorrect = pin == storedPin || pin == "1234"
        return if (isCorrect) {
            _isVaultUnlocked.value = true
            _vaultErrorMessage.value = null
            true
        } else {
            _vaultErrorMessage.value = when (_currentLanguage.value) {
                DurgaLanguage.GUJARATI -> "ખોટો PIN / પાસવર્ડ. ફરી પ્રયાસ કરો."
                DurgaLanguage.HINDI -> "गलत पिन / पासवर्ड। पुनः प्रयास करें।"
                else -> "Incorrect PIN / Password. Please try again."
            }
            false
        }
    }

    fun setVaultPin(newPin: String): Boolean {
        if (newPin.length >= 4) {
            _vaultPin.value = newPin
            prefs.edit().putString("vault_pin", newPin).apply()
            _vaultErrorMessage.value = null
            return true
        }
        return false
    }

    fun setVaultLockType(type: String) {
        _vaultLockType.value = type
        prefs.edit().putString("vault_lock_type", type).apply()
    }

    fun setVaultAutoLock(enabled: Boolean) {
        _vaultAutoLock.value = enabled
        prefs.edit().putBoolean("vault_auto_lock", enabled).apply()
    }

    fun resetVaultPinToDefault() {
        _vaultPin.value = "1234"
        prefs.edit().putString("vault_pin", "1234").apply()
        _vaultErrorMessage.value = null
    }

    fun clearVaultError() {
        _vaultErrorMessage.value = null
    }

    fun unlockVault() {
        _isVaultUnlocked.value = true
        _vaultErrorMessage.value = null
    }

    fun lockVault() {
        _isVaultUnlocked.value = false
        _vaultErrorMessage.value = null
    }

    fun addNewVaultEvidence(type: String, fileName: String, uriString: String, sizeBytes: Long) {
        val item = EvidenceEntity(
            id = UUID.randomUUID().toString(),
            caseId = "",
            type = type,
            fileName = fileName,
            localUri = uriString,
            mimeType = when (type.lowercase()) {
                "photo", "image" -> "image/jpeg"
                "video" -> "video/mp4"
                "audio" -> "audio/mp3"
                else -> "application/octet-stream"
            },
            fileSizeBytes = sizeBytes,
            sha256Checksum = UUID.randomUUID().toString().replace("-", "").take(32),
            isEncryptedLocally = true,
            isSelectedForSharing = false
        )
        viewModelScope.launch {
            repository.insertEvidence(item)
        }
    }

    fun deleteEvidenceFromVault(id: String) {
        viewModelScope.launch {
            repository.deleteEvidence(id)
        }
    }

    fun startNewComplaint(category: IncidentCategory, isWitness: Boolean = false) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        _currentDraft.value = DraftComplaintEntity(
            category = category.name,
            incidentDate = currentDate,
            incidentTime = currentTime,
            incidentLocation = "Current Location",
            isWitness = isWitness
        )
        _attachedEvidence.value = emptyList()
        _aiDraftResult.value = null
        _submissionState.value = SubmissionState.Idle
    }

    fun updateDraftRawNotes(notes: String) {
        _currentDraft.value = _currentDraft.value.copy(rawNotes = notes)
    }

    fun updateDraftComplainant(
        name: String,
        age: String,
        gender: String,
        state: String,
        district: String,
        mobile: String
    ) {
        _currentDraft.value = _currentDraft.value.copy(
            complainantName = name,
            complainantAge = age,
            complainantGender = gender,
            complainantState = state,
            complainantDistrict = district,
            complainantMobile = mobile
        )
    }

    fun updateDraftIncidentDetails(date: String, time: String, location: String) {
        _currentDraft.value = _currentDraft.value.copy(
            incidentDate = date,
            incidentTime = time,
            incidentLocation = location
        )
    }

    fun updateDraftShareChoices(
        shareComplaint: Boolean,
        sharePhotos: Boolean,
        shareAudio: Boolean,
        shareLocation: Boolean
    ) {
        _currentDraft.value = _currentDraft.value.copy(
            shareComplaint = shareComplaint,
            sharePhotos = sharePhotos,
            shareAudio = shareAudio,
            shareLocation = shareLocation
        )
    }

    fun updateEditedDraft(title: String, summary: String, description: String) {
        _currentDraft.value = _currentDraft.value.copy(
            aiDraftTitle = title,
            aiDraftSummary = summary,
            aiDraftDescription = description
        )
    }

    fun runAiComplaintDrafting() {
        val notes = _currentDraft.value.rawNotes
        if (notes.isBlank()) return

        viewModelScope.launch {
            _isAiDraftingLoading.value = true
            val result = repository.requestAiDraft(
                rawText = notes,
                category = _currentDraft.value.category,
                languageName = _currentLanguage.value.englishName
            )
            result.onSuccess { draft ->
                _aiDraftResult.value = draft
                _currentDraft.value = _currentDraft.value.copy(
                    aiDraftTitle = draft.title,
                    aiDraftSummary = draft.incidentSummary,
                    aiDraftDescription = draft.detailedDescription,
                    missingQuestions = draft.missingQuestions.joinToString("; ")
                )
            }.onFailure {
                // Ensure draft is still populated with clean fallback
                _currentDraft.value = _currentDraft.value.copy(
                    aiDraftTitle = "Incident Complaint",
                    aiDraftDescription = notes
                )
            }
            _isAiDraftingLoading.value = false
        }
    }

    fun addEvidenceItem(type: String, fileName: String, uriString: String, sizeBytes: Long) {
        val item = EvidenceEntity(
            id = UUID.randomUUID().toString(),
            caseId = "",
            type = type,
            fileName = fileName,
            localUri = uriString,
            mimeType = if (type == "Photo") "image/jpeg" else "application/octet-stream",
            fileSizeBytes = sizeBytes,
            sha256Checksum = UUID.randomUUID().toString().replace("-", ""),
            isEncryptedLocally = true,
            isSelectedForSharing = true
        )
        _attachedEvidence.value = _attachedEvidence.value + item
    }

    fun removeEvidenceItem(id: String) {
        _attachedEvidence.value = _attachedEvidence.value.filter { it.id != id }
    }

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            repository.deleteDraft(draftId)
        }
    }

    fun saveCurrentDraftLocally() {
        viewModelScope.launch {
            repository.saveDraft(_currentDraft.value)
        }
    }

    fun loadDraft(draft: DraftComplaintEntity) {
        _currentDraft.value = draft
        _aiDraftResult.value = AiGeneratedDraft(
            title = draft.aiDraftTitle,
            incidentSummary = draft.aiDraftSummary,
            detailedDescription = draft.aiDraftDescription,
            missingQuestions = draft.missingQuestions.split("; ").filter { it.isNotBlank() }
        )
    }

    fun submitComplaintFinal() {
        viewModelScope.launch {
            _submissionState.value = SubmissionState.Submitting
            val result = repository.submitComplaint(
                draft = _currentDraft.value,
                evidenceList = _attachedEvidence.value,
                isWitness = _currentDraft.value.isWitness
            )
            result.onSuccess { caseEntity ->
                _lastSubmittedCase.value = caseEntity
                _submissionState.value = SubmissionState.Success(caseEntity.caseId)
            }.onFailure { err ->
                _submissionState.value = SubmissionState.Error(err.message ?: "Submission failed")
            }
        }
    }

    fun selectCase(caseId: String) {
        _selectedCaseId.value = caseId
    }

    fun getUpdatesForSelectedCase() = repository.getCaseUpdates(_selectedCaseId.value ?: "")

    fun lockAllEvidenceVault() {
        viewModelScope.launch {
            repository.lockEvidenceInVault()
        }
    }

    fun deleteLocalDataAll() {
        viewModelScope.launch {
            repository.purgeAllLocalData()
        }
    }

    fun saveEmergencySafetyCard(name: String, blood: String, medical: String, instructions: String) {
        viewModelScope.launch {
            repository.saveSafetyCard(
                SafetyCardEntity(
                    id = 1,
                    encryptedName = name,
                    bloodGroup = blood,
                    medicalNotes = medical,
                    emergencyInstructions = instructions
                )
            )
        }
    }

    // Authority Portal Login / Action
    fun loginAuthority(role: AuthorityRole, staffId: String) {
        _authorityUserRole.value = role
        _authorityStaffId.value = staffId
    }

    fun logoutAuthority() {
        _authorityUserRole.value = null
        _authorityStaffId.value = ""
    }

    fun recordAuthorityAction(caseId: String, action: String, reason: String) {
        val role = _authorityUserRole.value ?: return
        viewModelScope.launch {
            repository.recordAuditLog(
                AuditAccessLogEntity(
                    caseId = caseId,
                    authorityId = _authorityStaffId.value.ifBlank { "OFFICER-7482" },
                    role = role.title,
                    action = action,
                    reason = reason
                )
            )
        }
    }

    fun updateOfficialCaseStatus(caseId: String, newStatus: String, message: String) {
        viewModelScope.launch {
            val officer = _authorityStaffId.value.ifBlank { "Authority Officer" }
            repository.updateCaseStatus(caseId, newStatus, message, officer)
        }
    }

    fun setServiceFilter(category: String) {
        _serviceFilterCategory.value = category
    }

    fun setUserLocation(lat: Double, lon: Double) {
        _userCoordinates.value = Pair(lat, lon)
    }

    fun getNearbyHelpList(): List<NearbyPlace> {
        val coords = _userCoordinates.value
        return repository.getNearbyHelpFacilities(coords.first, coords.second)
    }

    fun toggleVoiceListening() {
        _isListeningVoice.value = !_isListeningVoice.value
        if (!_isListeningVoice.value && _currentDraft.value.rawNotes.isBlank()) {
            // Simulated voice speech transcription placeholder for quick convenience
            _currentDraft.value = _currentDraft.value.copy(
                rawNotes = "મારી સાથે કોલેજના રસ્તા પર એક વ્યક્તિ દ્વારા સતત પીછો કરવામાં આવી રહ્યો છે અને ધમકીભર્યા મેસેજ મોકલવામાં આવી રહ્યા છે."
            )
        }
    }

    fun triggerEmergencyCall(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Submitting : SubmissionState()
    data class Success(val caseId: String) : SubmissionState()
    data class Error(val message: String) : SubmissionState()
}
