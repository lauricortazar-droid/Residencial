package com.example.data.repository

import com.example.data.local.AdministrativeRecordDao
import com.example.data.local.ExpedienteDao
import com.example.data.local.SendaDao

/**
 * Composite Repository Interface combining Clinical, Administrative and Resident domains.
 * Provides a clean, abstracted API for ViewModels and domain interactors.
 */
interface ISendaRepository : ClinicalRepository, AdministrativeRepository, ResidentRepository

/**
 * Primary Repository implementation using Kotlin interface delegation.
 * Decouples the UI layer / ViewModels from direct Room DAO interactions.
 */
class SendaRepository(
    private val dao: SendaDao,
    private val expedienteDao: ExpedienteDao,
    private val administrativeRecordDao: AdministrativeRecordDao,
    private val clinicalRepo: ClinicalRepository = ClinicalRepositoryImpl(expedienteDao, dao),
    private val administrativeRepo: AdministrativeRepository = AdministrativeRepositoryImpl(administrativeRecordDao, dao),
    private val residentRepo: ResidentRepository = ResidentRepositoryImpl(dao)
) : ISendaRepository,
    ClinicalRepository by clinicalRepo,
    AdministrativeRepository by administrativeRepo,
    ResidentRepository by residentRepo
