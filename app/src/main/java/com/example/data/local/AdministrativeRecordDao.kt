package com.example.data.local

import androidx.room.*
import com.example.data.model.AdministrativeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdministrativeRecordDao {

    @Query("SELECT * FROM administrative_records ORDER BY id DESC")
    fun getAllAdministrativeRecords(): Flow<List<AdministrativeRecordEntity>>

    @Query("SELECT * FROM administrative_records WHERE category = :category ORDER BY date DESC")
    fun getRecordsByCategory(category: String): Flow<List<AdministrativeRecordEntity>>

    @Query("SELECT * FROM administrative_records WHERE residentId = :residentId ORDER BY date DESC")
    fun getRecordsForResident(residentId: Int): Flow<List<AdministrativeRecordEntity>>

    @Query("SELECT * FROM administrative_records WHERE residentName LIKE '%' || :query || '%' OR folio LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%'")
    fun searchAdministrativeRecords(query: String): Flow<List<AdministrativeRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AdministrativeRecordEntity): Long

    @Update
    suspend fun updateRecord(record: AdministrativeRecordEntity)

    @Delete
    suspend fun deleteRecord(record: AdministrativeRecordEntity)
}
