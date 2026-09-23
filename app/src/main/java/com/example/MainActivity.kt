package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.durga.model.IncidentCategory
import com.example.durga.ui.DurgaViewModel
import com.example.durga.ui.SubmissionState
import com.example.durga.ui.components.QuickExitOverlay
import com.example.durga.ui.screens.AiComplaintDraftScreen
import com.example.durga.ui.screens.AuthorityAdminDashboardScreen
import com.example.durga.ui.screens.CaseCreatedScreen
import com.example.durga.ui.screens.CaseDetailsScreen
import com.example.durga.ui.screens.ChildFriendlyScreen
import com.example.durga.ui.screens.ComplainantDetailsScreen
import com.example.durga.ui.screens.ComplaintCategoryScreen
import com.example.durga.ui.screens.ComplaintInputScreen
import com.example.durga.ui.screens.EditComplaintScreen
import com.example.durga.ui.screens.EmergencyScreen
import com.example.durga.ui.screens.EvidenceSelectionScreen
import com.example.durga.ui.screens.FinalConfirmationScreen
import com.example.durga.ui.screens.HelpNearMeScreen
import com.example.durga.ui.screens.HomeScreen
import com.example.durga.ui.screens.IdkWhatToDoScreen
import com.example.durga.ui.screens.LanguageSelectionScreen
import com.example.durga.ui.screens.MyCasesScreen
import com.example.durga.ui.screens.PrivacyCenterScreen
import com.example.durga.ui.screens.PrivateVaultScreen
import com.example.durga.ui.screens.SelectWhatToShareScreen
import com.example.durga.ui.screens.WitnessReportScreen
import com.example.ui.theme.DurgaTheme

object DurgaRoutes {
    const val LANGUAGE_SELECT = "language_select"
    const val HOME = "home"
    const val EMERGENCY = "emergency"
    const val COMPLAINT_CATEGORIES = "complaint_categories"
    const val COMPLAINT_INPUT = "complaint_input"
    const val AI_COMPLAINT_DRAFT = "ai_complaint_draft"
    const val EDIT_COMPLAINT = "edit_complaint"
    const val COMPLAINANT_DETAILS = "complainant_details"
    const val INCIDENT_DETAILS = "incident_details"
    const val EVIDENCE_SELECTION = "evidence_selection"
    const val SELECT_WHAT_TO_SHARE = "select_what_to_share"
    const val FINAL_CONFIRMATION = "final_confirmation"
    const val CASE_CREATED = "case_created"
    const val MY_CASES = "my_cases"
    const val CASE_DETAILS = "case_details"
    const val WITNESS_REPORT = "witness_report"
    const val HELP_NEAR_ME = "help_near_me"
    const val IDK_WHAT_TO_DO = "idk_what_to_do"
    const val CHILD_FRIENDLY = "child_friendly"
    const val PRIVATE_VAULT = "private_vault"
    const val PRIVACY_CENTER = "privacy_center"
    const val AUTHORITY_PORTAL = "authority_portal"
}

class MainActivity : ComponentActivity() {
    private val viewModel: DurgaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentTheme by viewModel.currentTheme.collectAsState()
            DurgaTheme(themeMode = currentTheme) {
                DurgaApp(viewModel = viewModel, onCall = { phone ->
                    viewModel.triggerEmergencyCall(this, phone)
                })
            }
        }
    }
}

@Composable
fun DurgaApp(
    viewModel: DurgaViewModel,
    onCall: (String) -> Unit
) {
    val navController = rememberNavController()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val isOnboardingDone by viewModel.isOnboardingCompleted.collectAsState()
    val isQuickExitActive by viewModel.isQuickExitActive.collectAsState()

    val currentDraft by viewModel.currentDraft.collectAsState()
    val attachedEvidence by viewModel.attachedEvidence.collectAsState()
    val isAiDraftingLoading by viewModel.isAiDraftingLoading.collectAsState()
    val aiDraftResult by viewModel.aiDraftResult.collectAsState()
    val submissionState by viewModel.submissionState.collectAsState()
    val lastSubmittedCase by viewModel.lastSubmittedCase.collectAsState()
    val isListeningVoice by viewModel.isListeningVoice.collectAsState()

    val allCases by viewModel.allCases.collectAsState()
    val allDrafts by viewModel.allDrafts.collectAsState()
    val allEvidence by viewModel.allEvidence.collectAsState()
    val officialServices by viewModel.officialServices.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val isVaultUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val authorityRole by viewModel.authorityUserRole.collectAsState()
    val authorityStaffId by viewModel.authorityStaffId.collectAsState()
    val selectedCaseId by viewModel.selectedCaseId.collectAsState()
    val selectedCaseUpdates by viewModel.getUpdatesForSelectedCase().collectAsState(initial = emptyList())

    // Always launch directly to the main Home Screen so the user immediately sees the full app interface
    val startDestination = DurgaRoutes.HOME

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                // 1. Language Selection (First launch or switch)
                composable(DurgaRoutes.LANGUAGE_SELECT) {
                    LanguageSelectionScreen(
                        currentLanguage = currentLang,
                        onSelectLanguage = { viewModel.setLanguage(it) },
                        onContinue = {
                            viewModel.completeOnboarding()
                            navController.navigate(DurgaRoutes.HOME) {
                                popUpTo(DurgaRoutes.LANGUAGE_SELECT) { inclusive = true }
                            }
                        }
                    )
                }

                // 2. Home Dashboard
                composable(DurgaRoutes.HOME) {
                    HomeScreen(
                        currentLanguage = currentLang,
                        onNavigateToEmergency = { navController.navigate(DurgaRoutes.EMERGENCY) },
                        onNavigateToComplaintCategories = { navController.navigate(DurgaRoutes.COMPLAINT_CATEGORIES) },
                        onNavigateToWitness = { navController.navigate(DurgaRoutes.WITNESS_REPORT) },
                        onNavigateToHelpNearMe = { navController.navigate(DurgaRoutes.HELP_NEAR_ME) },
                        onNavigateToIdkWhatToDo = { navController.navigate(DurgaRoutes.IDK_WHAT_TO_DO) },
                        onNavigateToPrivateData = { navController.navigate(DurgaRoutes.PRIVATE_VAULT) },
                        onNavigateToMyCases = { navController.navigate(DurgaRoutes.MY_CASES) },
                        onNavigateToChildFriendly = { navController.navigate(DurgaRoutes.CHILD_FRIENDLY) },
                        onNavigateToPrivacyCenter = { navController.navigate(DurgaRoutes.PRIVACY_CENTER) },
                        onNavigateToSettings = { navController.navigate(DurgaRoutes.LANGUAGE_SELECT) },
                        onNavigateToAuthorityPortal = { navController.navigate(DurgaRoutes.AUTHORITY_PORTAL) },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onThemeChange = { viewModel.setTheme(it) },
                        onStartCategoryComplaint = { category ->
                            viewModel.startNewComplaint(category, isWitness = false)
                            navController.navigate(DurgaRoutes.COMPLAINT_INPUT)
                        },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 3. Emergency Helpline Directory
                composable(DurgaRoutes.EMERGENCY) {
                    EmergencyScreen(
                        currentLanguage = currentLang,
                        officialServices = officialServices,
                        onBack = { navController.popBackStack() },
                        onCallNumber = onCall,
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 4. Complaint Category Selection
                composable(DurgaRoutes.COMPLAINT_CATEGORIES) {
                    ComplaintCategoryScreen(
                        currentLanguage = currentLang,
                        onSelectCategory = { category ->
                            viewModel.startNewComplaint(category, isWitness = false)
                            navController.navigate(DurgaRoutes.COMPLAINT_INPUT)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 5. Complaint Input (Text / Voice)
                composable(DurgaRoutes.COMPLAINT_INPUT) {
                    ComplaintInputScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        isListeningVoice = isListeningVoice,
                        isAiLoading = isAiDraftingLoading,
                        onNotesChange = { viewModel.updateDraftRawNotes(it) },
                        onToggleVoice = { viewModel.toggleVoiceListening() },
                        onRunAiDraft = {
                            viewModel.runAiComplaintDrafting()
                            navController.navigate(DurgaRoutes.AI_COMPLAINT_DRAFT)
                        },
                        onSaveDraftLocally = {
                            viewModel.saveCurrentDraftLocally()
                            navController.popBackStack(DurgaRoutes.HOME, inclusive = false)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 6. AI Draft Review Screen
                composable(DurgaRoutes.AI_COMPLAINT_DRAFT) {
                    AiComplaintDraftScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        aiDraft = aiDraftResult,
                        onNavigateToEdit = { navController.navigate(DurgaRoutes.EDIT_COMPLAINT) },
                        onContinueToDetails = { navController.navigate(DurgaRoutes.COMPLAINANT_DETAILS) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 7. Edit Complaint Draft Screen
                composable(DurgaRoutes.EDIT_COMPLAINT) {
                    EditComplaintScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        onSaveEdit = { title, summary, desc ->
                            viewModel.updateEditedDraft(title, summary, desc)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 8. Complainant Details Screen
                composable(DurgaRoutes.COMPLAINANT_DETAILS) {
                    ComplainantDetailsScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        onSaveDetails = { name, age, gender, state, district, mobile ->
                            viewModel.updateDraftComplainant(name, age, gender, state, district, mobile)
                        },
                        onContinue = { navController.navigate(DurgaRoutes.INCIDENT_DETAILS) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 9. Incident Details Screen
                composable(DurgaRoutes.INCIDENT_DETAILS) {
                    com.example.durga.ui.screens.IncidentDetailsScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        onSaveIncidentDetails = { date, time, location ->
                            viewModel.updateDraftIncidentDetails(date, time, location)
                        },
                        onContinue = { navController.navigate(DurgaRoutes.EVIDENCE_SELECTION) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 10. Evidence Selection Screen
                composable(DurgaRoutes.EVIDENCE_SELECTION) {
                    EvidenceSelectionScreen(
                        currentLanguage = currentLang,
                        attachedEvidence = attachedEvidence,
                        onAddEvidence = { type, name, uri, size ->
                            viewModel.addEvidenceItem(type, name, uri, size)
                        },
                        onRemoveEvidence = { viewModel.removeEvidenceItem(it) },
                        onContinue = { navController.navigate(DurgaRoutes.SELECT_WHAT_TO_SHARE) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 11. Select What to Share Screen
                composable(DurgaRoutes.SELECT_WHAT_TO_SHARE) {
                    SelectWhatToShareScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        evidenceCount = attachedEvidence.size,
                        onUpdateChoices = { comp, photo, audio, loc ->
                            viewModel.updateDraftShareChoices(comp, photo, audio, loc)
                        },
                        onContinue = { navController.navigate(DurgaRoutes.FINAL_CONFIRMATION) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 12. Final Confirmation Screen
                composable(DurgaRoutes.FINAL_CONFIRMATION) {
                    FinalConfirmationScreen(
                        currentLanguage = currentLang,
                        currentDraft = currentDraft,
                        evidenceCount = attachedEvidence.size,
                        submissionState = submissionState,
                        onSubmitFinal = {
                            viewModel.submitComplaintFinal()
                            navController.navigate(DurgaRoutes.CASE_CREATED) {
                                popUpTo(DurgaRoutes.HOME) { inclusive = false }
                            }
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 13. Case Created Confirmation Screen
                composable(DurgaRoutes.CASE_CREATED) {
                    val caseItem = lastSubmittedCase ?: allCases.firstOrNull() ?: com.example.durga.model.CaseEntity(
                        caseId = "DURGA-2026-REG729",
                        category = "CYBER",
                        title = "Incident Registered",
                        originalDescription = "",
                        structuredDescription = "",
                        originalLanguage = "Gu",
                        incidentDate = "Today",
                        incidentTime = "Now",
                        incidentLocation = "Registered",
                        status = "REPORT_SUBMITTED"
                    )
                    CaseCreatedScreen(
                        currentLanguage = currentLang,
                        createdCase = caseItem,
                        onTrackCases = { navController.navigate(DurgaRoutes.MY_CASES) },
                        onGoHome = {
                            navController.navigate(DurgaRoutes.HOME) {
                                popUpTo(DurgaRoutes.HOME) { inclusive = true }
                            }
                        }
                    )
                }

                // 14. My Cases Screen
                composable(DurgaRoutes.MY_CASES) {
                    MyCasesScreen(
                        currentLanguage = currentLang,
                        casesList = allCases,
                        onSelectCase = { caseId ->
                            viewModel.selectCase(caseId)
                            navController.navigate(DurgaRoutes.CASE_DETAILS)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 15. Case Details Screen
                composable(DurgaRoutes.CASE_DETAILS) {
                    val caseItem = allCases.find { it.caseId == selectedCaseId }
                    CaseDetailsScreen(
                        currentLanguage = currentLang,
                        caseEntity = caseItem,
                        caseUpdates = selectedCaseUpdates,
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 16. Witness Report Screen
                composable(DurgaRoutes.WITNESS_REPORT) {
                    WitnessReportScreen(
                        currentLanguage = currentLang,
                        onStartWitnessComplaint = {
                            viewModel.startNewComplaint(IncidentCategory.PUBLIC_CITY, isWitness = true)
                            navController.navigate(DurgaRoutes.COMPLAINT_INPUT)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 17. Help Near Me Screen
                composable(DurgaRoutes.HELP_NEAR_ME) {
                    HelpNearMeScreen(
                        currentLanguage = currentLang,
                        placesList = viewModel.getNearbyHelpList(),
                        onCallNumber = onCall,
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 18. I Don't Know What To Do
                composable(DurgaRoutes.IDK_WHAT_TO_DO) {
                    IdkWhatToDoScreen(
                        currentLanguage = currentLang,
                        onNavigateToEmergency = { navController.navigate(DurgaRoutes.EMERGENCY) },
                        onNavigateToComplaint = { navController.navigate(DurgaRoutes.COMPLAINT_CATEGORIES) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 19. Child Friendly Mode
                composable(DurgaRoutes.CHILD_FRIENDLY) {
                    ChildFriendlyScreen(
                        currentLanguage = currentLang,
                        onCallChildline = { onCall("1098") },
                        onStartChildComplaint = { phrase ->
                            viewModel.startNewComplaint(IncidentCategory.CHILD_SAFETY, isWitness = false)
                            viewModel.updateDraftRawNotes(phrase)
                            navController.navigate(DurgaRoutes.COMPLAINT_INPUT)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 20. Private Vault
                composable(DurgaRoutes.PRIVATE_VAULT) {
                    PrivateVaultScreen(
                        currentLanguage = currentLang,
                        isVaultUnlocked = isVaultUnlocked,
                        draftsList = allDrafts,
                        evidenceList = allEvidence,
                        onUnlockVault = { viewModel.unlockVault() },
                        onLockVault = { viewModel.lockVault() },
                        onLockAllEvidence = { viewModel.lockAllEvidenceVault() },
                        onDeleteDraft = { viewModel.deleteDraft(it) },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 21. Privacy Center
                composable(DurgaRoutes.PRIVACY_CENTER) {
                    PrivacyCenterScreen(
                        currentLanguage = currentLang,
                        onPurgeAllData = {
                            viewModel.deleteLocalDataAll()
                            navController.popBackStack(DurgaRoutes.HOME, inclusive = false)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onThemeChange = { viewModel.setTheme(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }

                // 22. Authority / Admin Portal
                composable(DurgaRoutes.AUTHORITY_PORTAL) {
                    AuthorityAdminDashboardScreen(
                        currentLanguage = currentLang,
                        currentRole = authorityRole,
                        staffId = authorityStaffId,
                        casesList = allCases,
                        auditLogs = auditLogs,
                        onLogin = { role, id -> viewModel.loginAuthority(role, id) },
                        onLogout = { viewModel.logoutAuthority() },
                        onUpdateCaseStatus = { caseId, status, msg ->
                            viewModel.updateOfficialCaseStatus(caseId, status, msg)
                        },
                        onRecordAuditAction = { caseId, action, reason ->
                            viewModel.recordAuthorityAction(caseId, action, reason)
                        },
                        onBack = { navController.popBackStack() },
                        onLanguageChange = { viewModel.setLanguage(it) },
                        onQuickExit = { viewModel.triggerQuickExit() }
                    )
                }
            }

            // Quick Exit Overlay
            AnimatedVisibility(
                visible = isQuickExitActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                QuickExitOverlay(
                    onDismiss = { viewModel.dismissQuickExit() }
                )
            }
        }
    }
}
