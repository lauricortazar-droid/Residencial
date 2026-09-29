package com.example.data.repository

import com.example.data.local.ExpedienteDao
import com.example.data.local.SendaDao
import com.example.data.model.ClinicalRecordEntity
import com.example.data.model.ExpedienteEntity
import com.example.data.model.MedicationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Clean repository abstraction for Clinical & Medical data in Room.
 * Decouples ViewModels from direct SQLite / DAO operations.
 */
interface ClinicalRepository {
    // Expedientes Médicos
    val allExpedientes: Flow<List<ExpedienteEntity>>
    fun getExpedienteByResidentId(residentId: Int): Flow<ExpedienteEntity?>
    suspend fun getExpedienteById(id: Int): ExpedienteEntity?
    fun searchExpedientes(query: String): Flow<List<ExpedienteEntity>>
    suspend fun insertExpediente(expediente: ExpedienteEntity): Long
    suspend fun updateExpediente(expediente: ExpedienteEntity)
    suspend fun deleteExpediente(expediente: ExpedienteEntity)

    // Notas de Evolución Clínica y Registros Médicos
    val allClinicalRecords: Flow<List<ClinicalRecordEntity>>
    fun getClinicalRecordsForResident(residentId: Int): Flow<List<ClinicalRecordEntity>>
    suspend fun insertClinicalRecord(record: ClinicalRecordEntity)
    suspend fun deleteClinicalRecord(record: ClinicalRecordEntity)

    // Medicación y Prescripciones Farmacológicas
    val allMedications: Flow<List<MedicationEntity>>
    fun getMedicationsForResident(residentId: Int): Flow<List<MedicationEntity>>
    suspend fun insertMedication(medication: MedicationEntity)
    suspend fun updateMedication(medication: MedicationEntity)
    suspend fun deleteMedication(medication: MedicationEntity)
}

/**
 * Default implementation of ClinicalRepository accessing Room DAOs.
 */
class ClinicalRepositoryImpl(
    private val expedienteDao: ExpedienteDao,
    private val sendaDao: SendaDao
) : ClinicalRepository {

    override val allExpedientes: Flow<List<ExpedienteEntity>> =
        expedienteDao.getAllExpedientes()

    override fun getExpedienteByResidentId(residentId: Int): Flow<ExpedienteEntity?> =
        expedienteDao.getExpedienteByResidentId(residentId)

    override suspend fun getExpedienteById(id: Int): ExpedienteEntity? =
        expedienteDao.getExpedienteById(id)

    override fun searchExpedientes(query: String): Flow<List<ExpedienteEntity>> =
        expedienteDao.searchExpedientes(query)

    override suspend fun insertExpediente(expediente: ExpedienteEntity): Long =
        expedienteDao.insertExpediente(expediente)

    override suspend fun updateExpediente(expediente: ExpedienteEntity) =
        expedienteDao.updateExpediente(expediente)

    override suspend fun deleteExpediente(expediente: ExpedienteEntity) =
        expedienteDao.deleteExpediente(expediente)

    override val allClinicalRecords: Flow<List<ClinicalRecordEntity>> =
        sendaDao.getAllClinicalRecords()

    override fun getClinicalRecordsForResident(residentId: Int): Flow<List<ClinicalRecordEntity>> =
        sendaDao.getClinicalRecordsForResident(residentId)

    override suspend fun insertClinicalRecord(record: ClinicalRecordEntity) =
        sendaDao.insertClinicalRecord(record)

    override suspend fun deleteClinicalRecord(record: ClinicalRecordEntity) =
        sendaDao.deleteClinicalRecord(record)

    override val allMedications: Flow<List<MedicationEntity>> =
        sendaDao.getAllMedications()

    override fun getMedicationsForResident(residentId: Int): Flow<List<MedicationEntity>> =
        sendaDao.getMedicationsForResident(residentId)

    override suspend fun insertMedication(medication: MedicationEntity) =
        sendaDao.insertMedication(medication)

    override suspend fun updateMedication(medication: MedicationEntity) =
        sendaDao.updateMedication(medication)

    override suspend fun deleteMedication(medication: MedicationEntity) =
        sendaDao.deleteMedication(medication)
}
