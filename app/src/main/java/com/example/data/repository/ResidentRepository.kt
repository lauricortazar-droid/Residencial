package com.example.data.repository

import com.example.data.local.SendaDao
import com.example.data.model.ResidentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Clean repository abstraction for Residents / Usuarios in Room.
 */
interface ResidentRepository {
    val allResidents: Flow<List<ResidentEntity>>
    fun getResidentsByStatus(status: String): Flow<List<ResidentEntity>>
    suspend fun getResidentById(id: Int): ResidentEntity?
    suspend fun insertResident(resident: ResidentEntity): Long
    suspend fun updateResident(resident: ResidentEntity)
    suspend fun deleteResident(resident: ResidentEntity)
}

/**
 * Default implementation of ResidentRepository accessing Room DAOs.
 */
class ResidentRepositoryImpl(
    private val sendaDao: SendaDao
) : ResidentRepository {

    override val allResidents: Flow<List<ResidentEntity>> =
        sendaDao.getAllResidents()

    override fun getResidentsByStatus(status: String): Flow<List<ResidentEntity>> =
        sendaDao.getResidentsByStatus(status)

    override suspend fun getResidentById(id: Int): ResidentEntity? =
        sendaDao.getResidentById(id)

    override suspend fun insertResident(resident: ResidentEntity): Long =
        sendaDao.insertResident(resident)

    override suspend fun updateResident(resident: ResidentEntity) =
        sendaDao.updateResident(resident)

    override suspend fun deleteResident(resident: ResidentEntity) =
        sendaDao.deleteResident(resident)
}
