package com.example.data.local

import androidx.room.*
import com.example.data.model.ExpedienteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpedienteDao {

    @Query("SELECT * FROM expedientes ORDER BY id DESC")
    fun getAllExpedientes(): Flow<List<ExpedienteEntity>>

    @Query("SELECT * FROM expedientes WHERE residentId = :residentId LIMIT 1")
    fun getExpedienteByResidentId(residentId: Int): Flow<ExpedienteEntity?>

    @Query("SELECT * FROM expedientes WHERE id = :id LIMIT 1")
    suspend fun getExpedienteById(id: Int): ExpedienteEntity?

    @Query("SELECT * FROM expedientes WHERE estatusExpediente = :status ORDER BY fechaApertura DESC")
    fun getExpedientesByStatus(status: String): Flow<List<ExpedienteEntity>>

    @Query("SELECT * FROM expedientes WHERE residentName LIKE '%' || :query || '%' OR folioExpediente LIKE '%' || :query || '%'")
    fun searchExpedientes(query: String): Flow<List<ExpedienteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpediente(expediente: ExpedienteEntity): Long

    @Update
    suspend fun updateExpediente(expediente: ExpedienteEntity)

    @Delete
    suspend fun deleteExpediente(expediente: ExpedienteEntity)
}
