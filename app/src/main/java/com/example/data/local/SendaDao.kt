package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SendaDao {

    // Residents
    @Query("SELECT * FROM residents ORDER BY id DESC")
    fun getAllResidents(): Flow<List<ResidentEntity>>

    @Query("SELECT * FROM residents WHERE status = :status ORDER BY fullName ASC")
    fun getResidentsByStatus(status: String): Flow<List<ResidentEntity>>

    @Query("SELECT * FROM residents WHERE id = :id LIMIT 1")
    suspend fun getResidentById(id: Int): ResidentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResident(resident: ResidentEntity): Long

    @Update
    suspend fun updateResident(resident: ResidentEntity)

    @Delete
    suspend fun deleteResident(resident: ResidentEntity)

    // Clinical Records
    @Query("SELECT * FROM clinical_records ORDER BY date DESC, id DESC")
    fun getAllClinicalRecords(): Flow<List<ClinicalRecordEntity>>

    @Query("SELECT * FROM clinical_records WHERE residentId = :residentId ORDER BY date DESC")
    fun getClinicalRecordsForResident(residentId: Int): Flow<List<ClinicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClinicalRecord(record: ClinicalRecordEntity)

    @Delete
    suspend fun deleteClinicalRecord(record: ClinicalRecordEntity)

    // Medications
    @Query("SELECT * FROM medications ORDER BY scheduleTime ASC")
    fun getAllMedications(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications WHERE residentId = :residentId")
    fun getMedicationsForResident(residentId: Int): Flow<List<MedicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity)

    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    @Delete
    suspend fun deleteMedication(medication: MedicationEntity)

    // Finance
    @Query("SELECT * FROM finance_transactions ORDER BY date DESC, id DESC")
    fun getAllFinanceTransactions(): Flow<List<FinanceTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinanceTransaction(transaction: FinanceTransactionEntity)

    @Delete
    suspend fun deleteFinanceTransaction(transaction: FinanceTransactionEntity)

    // Agenda Events
    @Query("SELECT * FROM agenda_events ORDER BY date ASC, time ASC")
    fun getAllAgendaEvents(): Flow<List<AgendaEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgendaEvent(event: AgendaEventEntity)

    @Delete
    suspend fun deleteAgendaEvent(event: AgendaEventEntity)

    // Staff
    @Query("SELECT * FROM staff_members ORDER BY fullName ASC")
    fun getAllStaff(): Flow<List<StaffMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaff(staff: StaffMemberEntity)

    @Delete
    suspend fun deleteStaff(staff: StaffMemberEntity)

    // Operation Logs
    @Query("SELECT * FROM operation_logs ORDER BY id DESC")
    fun getAllOperationLogs(): Flow<List<OperationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperationLog(log: OperationLogEntity)

    @Update
    suspend fun updateOperationLog(log: OperationLogEntity)

    @Delete
    suspend fun deleteOperationLog(log: OperationLogEntity)
}
