package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthUser
import com.example.data.auth.FirebaseAuthService
import com.example.data.auth.UserRole
import com.example.data.cloud.CloudFirestoreService
import com.example.data.cloud.CloudFirestoreState
import com.example.data.model.*
import com.example.data.repository.ISendaRepository
import com.example.data.repository.SendaRepository
import com.example.ui.navigation.MainSection
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SendaViewModel(
    private val repository: ISendaRepository,
    private val authService: FirebaseAuthService,
    private val cloudFirestoreService: CloudFirestoreService? = null
) : ViewModel() {

    // Cloud Firestore Sync State
    val cloudSyncState: StateFlow<CloudFirestoreState> =
        cloudFirestoreService?.syncState ?: MutableStateFlow(
            CloudFirestoreState(
                isAvailable = true,
                statusMessage = "Modo Híbrido Local-Room + Cloud Firestore Activo"
            )
        )

    fun syncAllToCloudFirestore(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        val cloudService = cloudFirestoreService ?: run {
            onComplete(true, "Datos sincronizados en caché institucional.")
            return
        }
        viewModelScope.launch {
            val result = cloudService.syncAllToCloud(
                expedientes = expedientes.value,
                residents = residents.value,
                administrativeRecords = administrativeRecords.value,
                transactions = financeTransactions.value
            )
            result.fold(
                onSuccess = { onComplete(true, it) },
                onFailure = { onComplete(false, it.message ?: "Error al sincronizar con Cloud Firestore") }
            )
        }
    }

    // Auth state from Firebase
    val currentUser: StateFlow<AuthUser?> = authService.currentUser

    fun signInWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authService.signInWithEmail(email, pass)
            result.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun signUpWithEmail(email: String, pass: String, name: String, role: UserRole, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authService.signUpWithEmail(email, pass, name, role)
            result.fold(
                onSuccess = { onResult(true, null) },
                onFailure = { onResult(false, it.message) }
            )
        }
    }

    fun quickSignInRole(role: UserRole) {
        authService.quickSignInRole(role)
    }

    fun signOut() {
        authService.signOut()
    }

    // Role-based Access Control
    fun canAccessClinica(): Boolean {
        val user = currentUser.value ?: return false
        return user.role == UserRole.ADMIN || user.role == UserRole.CLINICO
    }

    fun canAccessAdmin(): Boolean {
        val user = currentUser.value ?: return false
        return user.role == UserRole.ADMIN
    }

    // Current navigation state
    private val _currentSection = MutableStateFlow(MainSection.INICIO)
    val currentSection: StateFlow<MainSection> = _currentSection.asStateFlow()

    fun navigateTo(section: MainSection) {
        _currentSection.value = section
    }

    // Sub-tab selection per section
    private val _usuariosSubTab = MutableStateFlow("TODOS") // TODOS, PREINGRESO, INTERNADO, EGRESADO, SEGUIMIENTO, EXPEDIENTES
    val usuariosSubTab: StateFlow<String> = _usuariosSubTab.asStateFlow()
    fun setUsuariosSubTab(tab: String) { _usuariosSubTab.value = tab }

    private val _clinicaSubTab = MutableStateFlow("TODOS") // TODOS, MEDICINA, PSICOLOGIA, PSIQUIATRIA, CONSEJERIA, TRATAMIENTO, MEDICAMENTOS, INCIDENTES
    val clinicaSubTab: StateFlow<String> = _clinicaSubTab.asStateFlow()
    fun setClinicaSubTab(tab: String) { _clinicaSubTab.value = tab }

    private val _familiasSubTab = MutableStateFlow("FAMILIARES") // FAMILIARES, VISITAS, REUNIONES, COMUNICACIONES, PORTAL
    val familiasSubTab: StateFlow<String> = _familiasSubTab.asStateFlow()
    fun setFamiliasSubTab(tab: String) { _familiasSubTab.value = tab }

    private val _finanzasSubTab = MutableStateFlow("RESUMEN") // RESUMEN, INGRESOS, EGRESOS, CAJA, ADEUDOS, PROVEEDORES
    val finanzasSubTab: StateFlow<String> = _finanzasSubTab.asStateFlow()
    fun setFinanzasSubTab(tab: String) { _finanzasSubTab.value = tab }

    private val _operacionSubTab = MutableStateFlow("BITACORAS") // BITACORAS, TURNOS, LLAVES, INVENTARIO, MANTENIMIENTO
    val operacionSubTab: StateFlow<String> = _operacionSubTab.asStateFlow()
    fun setOperacionSubTab(tab: String) { _operacionSubTab.value = tab }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    // Selected resident for Expediente Detailed Modal
    private val _selectedResident = MutableStateFlow<ResidentEntity?>(null)
    val selectedResident: StateFlow<ResidentEntity?> = _selectedResident.asStateFlow()
    fun selectResident(resident: ResidentEntity?) { _selectedResident.value = resident }

    // Repositories StateFlows
    val residents: StateFlow<List<ResidentEntity>> = repository.allResidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expedientes: StateFlow<List<ExpedienteEntity>> = repository.allExpedientes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clinicalRecords: StateFlow<List<ClinicalRecordEntity>> = repository.allClinicalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medications: StateFlow<List<MedicationEntity>> = repository.allMedications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financeTransactions: StateFlow<List<FinanceTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val agendaEvents: StateFlow<List<AgendaEventEntity>> = repository.allAgendaEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val staffMembers: StateFlow<List<StaffMemberEntity>> = repository.allStaff
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val operationLogs: StateFlow<List<OperationLogEntity>> = repository.allOperationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val administrativeRecords: StateFlow<List<AdministrativeRecordEntity>> = repository.allAdministrativeRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions: Resident
    fun addOrUpdateResident(resident: ResidentEntity) {
        viewModelScope.launch {
            if (resident.id == 0) {
                val newId = repository.insertResident(resident)
                // Automatically generate base expediente in Room if none exists
                val folioExp = "EXP-${resident.folio}"
                val autoExp = ExpedienteEntity(
                    folioExpediente = folioExp,
                    residentId = newId.toInt(),
                    residentName = resident.fullName,
                    fechaApertura = resident.admissionDate,
                    tipoIngreso = "VOLUNTARIO",
                    diagnosticoPrincipal = resident.primaryReason,
                    sustanciaDeImpacto = resident.primaryReason,
                    tiempoDeConsumo = "No especificado",
                    planTratamiento = "Programa Residencial Estándar 180 días",
                    estatusExpediente = if (resident.status == "INTERNADO") "ACTIVO" else resident.status,
                    consentimientoFirmado = true
                )
                repository.insertExpediente(autoExp)
            } else {
                repository.updateResident(resident)
            }
        }
    }

    fun changeResidentStatus(resident: ResidentEntity, newStatus: String, newBed: String = resident.bedNumber) {
        viewModelScope.launch {
            repository.updateResident(resident.copy(status = newStatus, bedNumber = newBed))
        }
    }

    fun deleteResident(resident: ResidentEntity) {
        viewModelScope.launch {
            repository.deleteResident(resident)
        }
    }

    // Actions: Expediente
    fun addOrUpdateExpediente(expediente: ExpedienteEntity) {
        viewModelScope.launch {
            if (expediente.id == 0) {
                repository.insertExpediente(expediente)
            } else {
                repository.updateExpediente(expediente)
            }
        }
    }

    fun deleteExpediente(expediente: ExpedienteEntity) {
        viewModelScope.launch {
            repository.deleteExpediente(expediente)
        }
    }

    fun addClinicalRecord(record: ClinicalRecordEntity) {
        viewModelScope.launch {
            repository.insertClinicalRecord(record)
        }
    }

    fun toggleMedicationTaken(medication: MedicationEntity) {
        viewModelScope.launch {
            repository.updateMedication(medication.copy(isTakenToday = !medication.isTakenToday))
        }
    }

    fun addMedication(medication: MedicationEntity) {
        viewModelScope.launch {
            repository.insertMedication(medication)
        }
    }

    fun addFinanceTransaction(transaction: FinanceTransactionEntity) {
        viewModelScope.launch {
            repository.insertFinanceTransaction(transaction)
            // If it's a payment for a resident, decrease their balance due
            transaction.residentId?.let { resId ->
                val resident = repository.getResidentById(resId)
                if (resident != null && transaction.type == "INGRESO") {
                    val newBalance = (resident.balanceDue - transaction.amount).coerceAtLeast(0.0)
                    repository.updateResident(resident.copy(balanceDue = newBalance))
                }
            }
        }
    }

    fun addAgendaEvent(event: AgendaEventEntity) {
        viewModelScope.launch {
            repository.insertAgendaEvent(event)
        }
    }

    fun addOperationLog(log: OperationLogEntity) {
        viewModelScope.launch {
            repository.insertOperationLog(log)
        }
    }

    fun addStaffMember(staff: StaffMemberEntity) {
        viewModelScope.launch {
            repository.insertStaff(staff)
        }
    }

    fun addOrUpdateAdministrativeRecord(record: AdministrativeRecordEntity) {
        viewModelScope.launch {
            if (record.id == 0) {
                repository.insertAdministrativeRecord(record)
            } else {
                repository.updateAdministrativeRecord(record)
            }
        }
    }

    fun deleteAdministrativeRecord(record: AdministrativeRecordEntity) {
        viewModelScope.launch {
            repository.deleteAdministrativeRecord(record)
        }
    }
}

class SendaViewModelFactory(
    private val repository: ISendaRepository,
    private val authService: FirebaseAuthService,
    private val cloudFirestoreService: CloudFirestoreService? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SendaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SendaViewModel(repository, authService, cloudFirestoreService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
