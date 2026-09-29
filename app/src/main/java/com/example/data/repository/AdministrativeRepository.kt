package com.example.data.repository

import com.example.data.local.AdministrativeRecordDao
import com.example.data.local.SendaDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Clean repository abstraction for Administrative, Financial & Operational data in Room.
 * Decouples ViewModels from direct SQLite / DAO operations.
 */
interface AdministrativeRepository {
    // Registros Administrativos (Contratos, Consentimientos, Resguardos, Supervisiones)
    val allAdministrativeRecords: Flow<List<AdministrativeRecordEntity>>
    fun getAdministrativeRecordsByCategory(category: String): Flow<List<AdministrativeRecordEntity>>
    fun getAdministrativeRecordsForResident(residentId: Int): Flow<List<AdministrativeRecordEntity>>
    fun searchAdministrativeRecords(query: String): Flow<List<AdministrativeRecordEntity>>
    suspend fun insertAdministrativeRecord(record: AdministrativeRecordEntity): Long
    suspend fun updateAdministrativeRecord(record: AdministrativeRecordEntity)
    suspend fun deleteAdministrativeRecord(record: AdministrativeRecordEntity)

    // Gestión Financiera (Ingresos, Egresos, Caja Chica)
    val allTransactions: Flow<List<FinanceTransactionEntity>>
    suspend fun insertFinanceTransaction(transaction: FinanceTransactionEntity)
    suspend fun deleteFinanceTransaction(transaction: FinanceTransactionEntity)

    // Bitácoras y Registros de Operación
    val allOperationLogs: Flow<List<OperationLogEntity>>
    suspend fun insertOperationLog(log: OperationLogEntity)
    suspend fun updateOperationLog(log: OperationLogEntity)
    suspend fun deleteOperationLog(log: OperationLogEntity)

    // Plantilla de Personal y Servidores
    val allStaff: Flow<List<StaffMemberEntity>>
    suspend fun insertStaff(staff: StaffMemberEntity)
    suspend fun deleteStaff(staff: StaffMemberEntity)

    // Agenda Institucional
    val allAgendaEvents: Flow<List<AgendaEventEntity>>
    suspend fun insertAgendaEvent(event: AgendaEventEntity)
    suspend fun deleteAgendaEvent(event: AgendaEventEntity)
}

/**
 * Default implementation of AdministrativeRepository accessing Room DAOs.
 */
class AdministrativeRepositoryImpl(
    private val administrativeRecordDao: AdministrativeRecordDao,
    private val sendaDao: SendaDao
) : AdministrativeRepository {

    override val allAdministrativeRecords: Flow<List<AdministrativeRecordEntity>> =
        administrativeRecordDao.getAllAdministrativeRecords()

    override fun getAdministrativeRecordsByCategory(category: String): Flow<List<AdministrativeRecordEntity>> =
        administrativeRecordDao.getRecordsByCategory(category)

    override fun getAdministrativeRecordsForResident(residentId: Int): Flow<List<AdministrativeRecordEntity>> =
        administrativeRecordDao.getRecordsForResident(residentId)

    override fun searchAdministrativeRecords(query: String): Flow<List<AdministrativeRecordEntity>> =
        administrativeRecordDao.searchAdministrativeRecords(query)

    override suspend fun insertAdministrativeRecord(record: AdministrativeRecordEntity): Long =
        administrativeRecordDao.insertRecord(record)

    override suspend fun updateAdministrativeRecord(record: AdministrativeRecordEntity) =
        administrativeRecordDao.updateRecord(record)

    override suspend fun deleteAdministrativeRecord(record: AdministrativeRecordEntity) =
        administrativeRecordDao.deleteRecord(record)

    override val allTransactions: Flow<List<FinanceTransactionEntity>> =
        sendaDao.getAllFinanceTransactions()

    override suspend fun insertFinanceTransaction(transaction: FinanceTransactionEntity) =
        sendaDao.insertFinanceTransaction(transaction)

    override suspend fun deleteFinanceTransaction(transaction: FinanceTransactionEntity) =
        sendaDao.deleteFinanceTransaction(transaction)

    override val allOperationLogs: Flow<List<OperationLogEntity>> =
        sendaDao.getAllOperationLogs()

    override suspend fun insertOperationLog(log: OperationLogEntity) =
        sendaDao.insertOperationLog(log)

    override suspend fun updateOperationLog(log: OperationLogEntity) =
        sendaDao.updateOperationLog(log)

    override suspend fun deleteOperationLog(log: OperationLogEntity) =
        sendaDao.deleteOperationLog(log)

    override val allStaff: Flow<List<StaffMemberEntity>> =
        sendaDao.getAllStaff()

    override suspend fun insertStaff(staff: StaffMemberEntity) =
        sendaDao.insertStaff(staff)

    override suspend fun deleteStaff(staff: StaffMemberEntity) =
        sendaDao.deleteStaff(staff)

    override val allAgendaEvents: Flow<List<AgendaEventEntity>> =
        sendaDao.getAllAgendaEvents()

    override suspend fun insertAgendaEvent(event: AgendaEventEntity) =
        sendaDao.insertAgendaEvent(event)

    override suspend fun deleteAgendaEvent(event: AgendaEventEntity) =
        sendaDao.deleteAgendaEvent(event)
}
