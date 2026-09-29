package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.auth.FirebaseAuthService
import com.example.data.auth.UserRole
import com.example.data.cloud.CloudFirestoreService
import com.example.data.local.AppDatabase
import com.example.data.repository.SendaRepository
import com.example.ui.components.AuthLoginDialog
import com.example.ui.components.CloudFirestoreSyncDialog
import com.example.ui.components.SendaBottomBar
import com.example.ui.components.SendaDrawerContent
import com.example.ui.components.SendaTopAppBar
import com.example.ui.navigation.MainSection
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SendaViewModel
import com.example.ui.viewmodel.SendaViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this, lifecycleScope)
        val repository = SendaRepository(
            database.sendaDao(),
            database.expedienteDao(),
            database.administrativeRecordDao()
        )
        val authService = FirebaseAuthService(this)
        val cloudFirestoreService = CloudFirestoreService(this)
        val factory = SendaViewModelFactory(repository, authService, cloudFirestoreService)

        setContent {
            MyApplicationTheme {
                val viewModel: SendaViewModel = viewModel(factory = factory)
                SendaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SendaApp(viewModel: SendaViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentSection by viewModel.currentSection.collectAsStateWithLifecycle()
    val residents by viewModel.residents.collectAsStateWithLifecycle()
    val expedientes by viewModel.expedientes.collectAsStateWithLifecycle()
    val clinicalRecords by viewModel.clinicalRecords.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val financeTransactions by viewModel.financeTransactions.collectAsStateWithLifecycle()
    val agendaEvents by viewModel.agendaEvents.collectAsStateWithLifecycle()
    val staffList by viewModel.staffMembers.collectAsStateWithLifecycle()
    val operationLogs by viewModel.operationLogs.collectAsStateWithLifecycle()
    val administrativeRecords by viewModel.administrativeRecords.collectAsStateWithLifecycle()

    val usuariosSubTab by viewModel.usuariosSubTab.collectAsStateWithLifecycle()
    val clinicaSubTab by viewModel.clinicaSubTab.collectAsStateWithLifecycle()
    val familiasSubTab by viewModel.familiasSubTab.collectAsStateWithLifecycle()
    val finanzasSubTab by viewModel.finanzasSubTab.collectAsStateWithLifecycle()
    val operacionSubTab by viewModel.operacionSubTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val cloudSyncState by viewModel.cloudSyncState.collectAsStateWithLifecycle()

    var showAuthDialog by remember { mutableStateOf(false) }
    var showCloudSyncDialog by remember { mutableStateOf(false) }
    var requiredRoleForAuth by remember { mutableStateOf("Personal Clínico o Administrador") }

    val pendingMedsCount = medications.count { !it.isTakenToday }
    val pendingAlertsCount = pendingMedsCount + clinicalRecords.count { it.severity == "URGENTE" }

    // Back button handling: if drawer is open, close it; if in another section, navigate back to INICIO
    BackHandler(enabled = drawerState.isOpen || currentSection != MainSection.INICIO) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            viewModel.navigateTo(MainSection.INICIO)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SendaDrawerContent(
                currentSection = currentSection,
                onSectionSelected = { section ->
                    viewModel.navigateTo(section)
                    viewModel.setSearchQuery("")
                    coroutineScope.launch { drawerState.close() }
                },
                residentCount = residents.size,
                pendingMedsCount = pendingMedsCount,
                currentUser = currentUser,
                onOpenAuth = {
                    requiredRoleForAuth = "Personal Clínico o Administrador"
                    showAuthDialog = true
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                SendaTopAppBar(
                    currentSection = currentSection,
                    onMenuClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    pendingAlertsCount = pendingAlertsCount,
                    currentUser = currentUser,
                    onAuthClick = {
                        requiredRoleForAuth = "Personal Clínico o Administrador"
                        showAuthDialog = true
                    },
                    onCloudSyncClick = {
                        showCloudSyncDialog = true
                    },
                    isCloudSyncing = cloudSyncState.isSyncing,
                    isCloudOnline = cloudSyncState.isAvailable
                )
            },
            bottomBar = {
                SendaBottomBar(
                    currentSection = currentSection,
                    onSectionSelected = { section ->
                        viewModel.navigateTo(section)
                        viewModel.setSearchQuery("")
                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 680.dp)
                ) {
                    when (currentSection) {
                MainSection.INICIO -> {
                    DashboardScreen(
                        residents = residents,
                        clinicalRecords = clinicalRecords,
                        medications = medications,
                        transactions = financeTransactions,
                        agendaEvents = agendaEvents,
                        onNavigate = { section ->
                            viewModel.navigateTo(section)
                            viewModel.setSearchQuery("")
                        },
                        onToggleMedication = { viewModel.toggleMedicationTaken(it) },
                        onOpenAddResident = {
                            viewModel.navigateTo(MainSection.USUARIOS)
                            viewModel.setUsuariosSubTab("PREINGRESO")
                        },
                        onOpenAddPayment = {
                            viewModel.navigateTo(MainSection.FINANZAS)
                            viewModel.setFinanzasSubTab("PAGOS")
                        },
                        onOpenAddBitacora = {
                            viewModel.navigateTo(MainSection.OPERACION)
                            viewModel.setOperacionSubTab("BITACORAS")
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.USUARIOS -> {
                    UsuariosScreen(
                        residents = residents,
                        clinicalRecords = clinicalRecords,
                        medications = medications,
                        selectedSubTab = usuariosSubTab,
                        onSubTabSelected = { viewModel.setUsuariosSubTab(it) },
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSaveResident = { viewModel.addOrUpdateResident(it) },
                        onChangeStatus = { res, st, bed -> viewModel.changeResidentStatus(res, st, bed) },
                        onDeleteResident = { viewModel.deleteResident(it) },
                        expedientes = expedientes,
                        onSaveExpediente = { viewModel.addOrUpdateExpediente(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.CLINICA -> {
                    // Secure access check for Clínica module
                    if (!viewModel.canAccessClinica()) {
                        AuthGateScreen(
                            title = "Módulo Clínico Protegido",
                            subtitle = "El acceso a valoraciones médicas, notas de psicología, psiquiatría, fármacos e incidentes está regulado conforme a la NOM-028 para expedientes confidenciales.",
                            requiredRoleDescription = "Médico, Psiquiatra o Administrador General",
                            currentUser = currentUser,
                            onOpenAuthDialog = {
                                requiredRoleForAuth = "Médico o Administrador"
                                showAuthDialog = true
                            },
                            onQuickSignInClinico = { viewModel.quickSignInRole(UserRole.CLINICO) },
                            onQuickSignInAdmin = { viewModel.quickSignInRole(UserRole.ADMIN) },
                            onBackToHome = { viewModel.navigateTo(MainSection.INICIO) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        ClinicaScreen(
                            residents = residents,
                            clinicalRecords = clinicalRecords,
                            medications = medications,
                            selectedSubTab = clinicaSubTab,
                            onSubTabSelected = { viewModel.setClinicaSubTab(it) },
                            searchQuery = searchQuery,
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onToggleMedication = { viewModel.toggleMedicationTaken(it) },
                            onAddClinicalRecord = { viewModel.addClinicalRecord(it) },
                            onAddMedication = { viewModel.addMedication(it) },
                            expedientes = expedientes,
                            onSaveExpediente = { viewModel.addOrUpdateExpediente(it) },
                            onDeleteExpediente = { viewModel.deleteExpediente(it) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                MainSection.FAMILIAS -> {
                    FamiliasScreen(
                        residents = residents,
                        agendaEvents = agendaEvents,
                        selectedSubTab = familiasSubTab,
                        onSubTabSelected = { viewModel.setFamiliasSubTab(it) },
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onScheduleVisit = { viewModel.addAgendaEvent(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.FINANZAS -> {
                    FinanzasScreen(
                        transactions = financeTransactions,
                        residents = residents,
                        selectedSubTab = finanzasSubTab,
                        onSubTabSelected = { viewModel.setFinanzasSubTab(it) },
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onAddTransaction = { viewModel.addFinanceTransaction(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.AGENDA -> {
                    AgendaScreen(
                        agendaEvents = agendaEvents,
                        onAddAgendaEvent = { viewModel.addAgendaEvent(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.PERSONAL -> {
                    PersonalScreen(
                        staffList = staffList,
                        onAddStaff = { viewModel.addStaffMember(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.OPERACION -> {
                    OperacionScreen(
                        operationLogs = operationLogs,
                        selectedSubTab = operacionSubTab,
                        onSubTabSelected = { viewModel.setOperacionSubTab(it) },
                        onAddLog = { viewModel.addOperationLog(it) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.DOCUMENTOS -> {
                    DocumentosEvidenciasScreen(
                        residents = residents,
                        administrativeRecords = administrativeRecords,
                        onSaveAdministrativeRecord = { viewModel.addOrUpdateAdministrativeRecord(it) },
                        isEvidenciasMode = false,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.EVIDENCIAS -> {
                    DocumentosEvidenciasScreen(
                        residents = residents,
                        administrativeRecords = administrativeRecords,
                        onSaveAdministrativeRecord = { viewModel.addOrUpdateAdministrativeRecord(it) },
                        isEvidenciasMode = true,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.COMUNICACIONES -> {
                    ComunicacionesScreen(
                        residents = residents,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                MainSection.ADMINISTRACION -> {
                    // Secure access check for Administración module
                    if (!viewModel.canAccessAdmin()) {
                        AuthGateScreen(
                            title = "Módulo de Administración Protegido",
                            subtitle = "Este apartado contiene la configuración de seguridad, auditoría, respaldos de base de datos cifrados y asignación de privilegios del sistema.",
                            requiredRoleDescription = "Director General / Administrador del Sistema",
                            currentUser = currentUser,
                            onOpenAuthDialog = {
                                requiredRoleForAuth = "Administrador del Sistema"
                                showAuthDialog = true
                            },
                            onQuickSignInAdmin = { viewModel.quickSignInRole(UserRole.ADMIN) },
                            onBackToHome = { viewModel.navigateTo(MainSection.INICIO) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        MasOpcionesScreen(
                            currentSection = currentSection,
                            residents = residents,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }

                MainSection.REPORTES,
                MainSection.GOOGLE_WORKSPACE,
                MainSection.CONFIGURACION -> {
                    MasOpcionesScreen(
                        currentSection = currentSection,
                        residents = residents,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
                }
            }
        }

        // Cloud Firestore ("Cloudfire") Sync Dialog
        if (showCloudSyncDialog) {
            CloudFirestoreSyncDialog(
                syncState = cloudSyncState,
                onSyncNow = {
                    viewModel.syncAllToCloudFirestore()
                },
                onDismiss = { showCloudSyncDialog = false }
            )
        }

        // Firebase Auth Login / Register / Role Dialog
        if (showAuthDialog) {
            AuthLoginDialog(
                requiredRoleName = requiredRoleForAuth,
                currentUser = currentUser,
                onDismiss = { showAuthDialog = false },
                onSignIn = { email, pass ->
                    viewModel.signInWithEmail(email, pass) { success, _ ->
                        if (success) showAuthDialog = false
                    }
                },
                onSignUp = { email, pass, name, role ->
                    viewModel.signUpWithEmail(email, pass, name, role) { success, _ ->
                        if (success) showAuthDialog = false
                    }
                },
                onQuickSignIn = { role ->
                    viewModel.quickSignInRole(role)
                    showAuthDialog = false
                },
                onSignOut = {
                    viewModel.signOut()
                    showAuthDialog = false
                }
            )
        }
    }
}
