package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "administrative_records")
data class AdministrativeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folio: String, // e.g. "ADM-2024-001"
    val category: String, // CONTRATO_INGRESO, CONSENTIMIENTO_TUTOR, RESGUARDO_VALORES, ACTA_ADMINISTRATIVA, BAJA_VOLUNTARIA, SUPERVISION_OFICIAL
    val residentId: Int? = null,
    val residentName: String,
    val title: String,
    val description: String,
    val responsibleStaff: String,
    val date: String,
    val status: String = "FIRMADO", // FIRMADO, PENDIENTE, VIGENTE, ARCHIVADO
    val documentNumber: String = "",
    val notes: String = ""
)
