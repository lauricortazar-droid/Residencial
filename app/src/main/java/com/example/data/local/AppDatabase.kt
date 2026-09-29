package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ResidentEntity::class,
        ClinicalRecordEntity::class,
        MedicationEntity::class,
        FinanceTransactionEntity::class,
        AgendaEventEntity::class,
        StaffMemberEntity::class,
        OperationLogEntity::class,
        ExpedienteEntity::class,
        AdministrativeRecordEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sendaDao(): SendaDao
    abstract fun expedienteDao(): ExpedienteDao
    abstract fun administrativeRecordDao(): AdministrativeRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "senda_clinical_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            seedInitialData()
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            seedInitialData()
        }

        private fun seedInitialData() {
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.sendaDao()
                    InitialData.residents.forEach { dao.insertResident(it) }
                    InitialData.clinicalRecords.forEach { dao.insertClinicalRecord(it) }
                    InitialData.medications.forEach { dao.insertMedication(it) }
                    InitialData.transactions.forEach { dao.insertFinanceTransaction(it) }
                    InitialData.agendaEvents.forEach { dao.insertAgendaEvent(it) }
                    InitialData.staff.forEach { dao.insertStaff(it) }
                    InitialData.operationLogs.forEach { dao.insertOperationLog(it) }
                    
                    val expDao = database.expedienteDao()
                    InitialData.expedientes.forEach { expDao.insertExpediente(it) }

                    val adminDao = database.administrativeRecordDao()
                    InitialData.administrativeRecords.forEach { adminDao.insertRecord(it) }
                }
            }
        }
    }
}
