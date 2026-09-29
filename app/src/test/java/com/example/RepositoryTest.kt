package com.example

import com.example.data.model.*
import com.example.data.repository.AdministrativeRepository
import com.example.data.repository.ClinicalRepository
import com.example.data.repository.ISendaRepository
import com.example.data.repository.ResidentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests verifying the Repository pattern implementation and data source abstraction.
 */
class RepositoryTest {

    // Test Fake Repository verifying clean abstraction without requiring SQLite Room driver in fast JVM tests
    class FakeSendaRepository : ISendaRepository {
        private val expedientesList = mutableListOf(
            ExpedienteEntity(
                id = 1,
                folioExpediente = "EXP-TEST-001",
                residentId = 1,
                residentName = "Juan Pérez",
                fechaApertura = "2024-01-01",
                tipoIngreso = "VOLUNTARIO",
                diagnosticoPrincipal = "F10.2 Trastorno por consumo de alcohol",
                antecedentesPatologicos = "Ninguno",
                sustanciaDeImpacto = "Alcohol",
                tiempoDeConsumo = "5 años",
                planTratamiento = "Terapia Cognitivo-Conductual",
                medicoTratante = "Dr. Valdés",
                psicologoResponsable = "Psic. Flores",
                estatusExpediente = "ACTIVO",
                consentimientoFirmado = true,
                notasIngreso = "Ingreso programado"
            )
        )
        private val expedientesFlow = MutableStateFlow(expedientesList.toList())

        private val adminRecordsList = mutableListOf(
            AdministrativeRecordEntity(
                id = 1,
                folio = "ADM-2024-001",
                category = "CONTRATO_INGRESO",
                residentId = 1,
                residentName = "Juan Pérez",
                title = "Contrato Residencial",
                description = "Contrato de 180 días",
                responsibleStaff = "Director General",
                date = "2024-01-01",
                status = "FIRMADO"
            )
        )
        private val adminRecordsFlow = MutableStateFlow(adminRecordsList.toList())

        private val residentsList = mutableListOf(
            ResidentEntity(
                id = 1,
                folio = "SND-001",
                fullName = "Juan Pérez",
                age = 32,
                gender = "M",
                admissionDate = "2024-01-01",
                bedNumber = "1A",
                roomName = "Dormitorio Central",
                primaryReason = "Alcoholismo",
                tutorName = "María Pérez",
                tutorPhone = "555-1234",
                tutorRelationship = "Madre"
            )
        )
        private val residentsFlow = MutableStateFlow(residentsList.toList())

        // Clinical Repository
        override val allExpedientes: Flow<List<ExpedienteEntity>> = expedientesFlow
        override fun getExpedienteByResidentId(residentId: Int): Flow<ExpedienteEntity?> =
            MutableStateFlow(expedientesList.find { it.residentId == residentId })
        override suspend fun getExpedienteById(id: Int): ExpedienteEntity? =
            expedientesList.find { it.id == id }
        override fun searchExpedientes(query: String): Flow<List<ExpedienteEntity>> =
            MutableStateFlow(expedientesList.filter { it.residentName.contains(query, ignoreCase = true) })
        override suspend fun insertExpediente(expediente: ExpedienteEntity): Long {
            expedientesList.add(expediente)
            expedientesFlow.value = expedientesList.toList()
            return expediente.id.toLong()
        }
        override suspend fun updateExpediente(expediente: ExpedienteEntity) {
            val index = expedientesList.indexOfFirst { it.id == expediente.id }
            if (index != -1) expedientesList[index] = expediente
            expedientesFlow.value = expedientesList.toList()
        }
        override suspend fun deleteExpediente(expediente: ExpedienteEntity) {
            expedientesList.removeAll { it.id == expediente.id }
            expedientesFlow.value = expedientesList.toList()
        }

        override val allClinicalRecords: Flow<List<ClinicalRecordEntity>> = MutableStateFlow(emptyList())
        override fun getClinicalRecordsForResident(residentId: Int): Flow<List<ClinicalRecordEntity>> = MutableStateFlow(emptyList())
        override suspend fun insertClinicalRecord(record: ClinicalRecordEntity) {}
        override suspend fun deleteClinicalRecord(record: ClinicalRecordEntity) {}

        override val allMedications: Flow<List<MedicationEntity>> = MutableStateFlow(emptyList())
        override fun getMedicationsForResident(residentId: Int): Flow<List<MedicationEntity>> = MutableStateFlow(emptyList())
        override suspend fun insertMedication(medication: MedicationEntity) {}
        override suspend fun updateMedication(medication: MedicationEntity) {}
        override suspend fun deleteMedication(medication: MedicationEntity) {}

        // Administrative Repository
        override val allAdministrativeRecords: Flow<List<AdministrativeRecordEntity>> = adminRecordsFlow
        override fun getAdministrativeRecordsByCategory(category: String): Flow<List<AdministrativeRecordEntity>> =
            MutableStateFlow(adminRecordsList.filter { it.category == category })
        override fun getAdministrativeRecordsForResident(residentId: Int): Flow<List<AdministrativeRecordEntity>> =
            MutableStateFlow(adminRecordsList.filter { it.residentId == residentId })
        override fun searchAdministrativeRecords(query: String): Flow<List<AdministrativeRecordEntity>> =
            MutableStateFlow(adminRecordsList.filter { it.title.contains(query, ignoreCase = true) })
        override suspend fun insertAdministrativeRecord(record: AdministrativeRecordEntity): Long {
            adminRecordsList.add(record)
            adminRecordsFlow.value = adminRecordsList.toList()
            return record.id.toLong()
        }
        override suspend fun updateAdministrativeRecord(record: AdministrativeRecordEntity) {
            val index = adminRecordsList.indexOfFirst { it.id == record.id }
            if (index != -1) adminRecordsList[index] = record
            adminRecordsFlow.value = adminRecordsList.toList()
        }
        override suspend fun deleteAdministrativeRecord(record: AdministrativeRecordEntity) {
            adminRecordsList.removeAll { it.id == record.id }
            adminRecordsFlow.value = adminRecordsList.toList()
        }

        override val allTransactions: Flow<List<FinanceTransactionEntity>> = MutableStateFlow(emptyList())
        override suspend fun insertFinanceTransaction(transaction: FinanceTransactionEntity) {}
        override suspend fun deleteFinanceTransaction(transaction: FinanceTransactionEntity) {}

        override val allOperationLogs: Flow<List<OperationLogEntity>> = MutableStateFlow(emptyList())
        override suspend fun insertOperationLog(log: OperationLogEntity) {}
        override suspend fun updateOperationLog(log: OperationLogEntity) {}
        override suspend fun deleteOperationLog(log: OperationLogEntity) {}

        override val allStaff: Flow<List<StaffMemberEntity>> = MutableStateFlow(emptyList())
        override suspend fun insertStaff(staff: StaffMemberEntity) {}
        override suspend fun deleteStaff(staff: StaffMemberEntity) {}

        override val allAgendaEvents: Flow<List<AgendaEventEntity>> = MutableStateFlow(emptyList())
        override suspend fun insertAgendaEvent(event: AgendaEventEntity) {}
        override suspend fun deleteAgendaEvent(event: AgendaEventEntity) {}

        // Resident Repository
        override val allResidents: Flow<List<ResidentEntity>> = residentsFlow
        override fun getResidentsByStatus(status: String): Flow<List<ResidentEntity>> =
            MutableStateFlow(residentsList.filter { it.status == status })
        override suspend fun getResidentById(id: Int): ResidentEntity? =
            residentsList.find { it.id == id }
        override suspend fun insertResident(resident: ResidentEntity): Long {
            residentsList.add(resident)
            residentsFlow.value = residentsList.toList()
            return resident.id.toLong()
        }
        override suspend fun updateResident(resident: ResidentEntity) {
            val index = residentsList.indexOfFirst { it.id == resident.id }
            if (index != -1) residentsList[index] = resident
            residentsFlow.value = residentsList.toList()
        }
        override suspend fun deleteResident(resident: ResidentEntity) {
            residentsList.removeAll { it.id == resident.id }
            residentsFlow.value = residentsList.toList()
        }
    }

    @Test
    fun `test clinical repository abstraction`() = runBlocking {
        val fakeRepo = FakeSendaRepository()
        val clinicalRepo: ClinicalRepository = fakeRepo

        val expedientes = clinicalRepo.allExpedientes.first()
        assertEquals(1, expedientes.size)
        assertEquals("EXP-TEST-001", expedientes.first().folioExpediente)

        val newExpediente = ExpedienteEntity(
            id = 2,
            folioExpediente = "EXP-TEST-002",
            residentId = 2,
            residentName = "Laura Salas",
            fechaApertura = "2024-02-01",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "Trastorno adaptativo",
            antecedentesPatologicos = "Ninguno",
            sustanciaDeImpacto = "Sedantes",
            tiempoDeConsumo = "1 año",
            planTratamiento = "Psicoterapia",
            medicoTratante = "Dr. Valdés",
            psicologoResponsable = "Psic. Flores",
            estatusExpediente = "ACTIVO",
            consentimientoFirmado = true,
            notasIngreso = "Nota inicial"
        )
        clinicalRepo.insertExpediente(newExpediente)

        val updatedList = clinicalRepo.allExpedientes.first()
        assertEquals(2, updatedList.size)
    }

    @Test
    fun `test administrative repository abstraction`() = runBlocking {
        val fakeRepo = FakeSendaRepository()
        val adminRepo: AdministrativeRepository = fakeRepo

        val adminRecords = adminRepo.allAdministrativeRecords.first()
        assertEquals(1, adminRecords.size)
        assertEquals("ADM-2024-001", adminRecords.first().folio)

        val newRecord = AdministrativeRecordEntity(
            id = 2,
            folio = "ADM-2024-002",
            category = "RESGUARDO_VALORES",
            residentId = 1,
            residentName = "Juan Pérez",
            title = "Resguardo de Celular",
            description = "Celular Samsung",
            responsibleStaff = "Consejero Silva",
            date = "2024-01-02",
            status = "VIGENTE"
        )
        adminRepo.insertAdministrativeRecord(newRecord)

        val updatedList = adminRepo.allAdministrativeRecords.first()
        assertEquals(2, updatedList.size)
    }
}
