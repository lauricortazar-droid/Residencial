package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expedientes")
data class ExpedienteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folioExpediente: String, // e.g. "EXP-SND-2024-001"
    val residentId: Int,
    val residentName: String,
    val fechaApertura: String,
    val tipoIngreso: String = "VOLUNTARIO", // VOLUNTARIO, INVOLUNTARIO_FAMILIAR, OBLIGATORIO_JUDICIAL
    val diagnosticoPrincipal: String,
    val antecedentesPatologicos: String = "Sin antecedentes crónicos reportados",
    val sustanciaDeImpacto: String,
    val tiempoDeConsumo: String = "3 años",
    val planTratamiento: String = "Programa Residencial de 6 meses - Modelo Comunidad Terapéutica",
    val medicoTratante: String = "Dr. Armando Valdés Soto",
    val psicologoResponsable: String = "Psic. Mariana Flores Peñaloza",
    val estatusExpediente: String = "ACTIVO", // ACTIVO, EN_REVISION, CONCLUIDO, ARCHIVADO
    val consentimientoFirmado: Boolean = true,
    val notasIngreso: String = "Ingreso voluntario con acompañamiento de tutor. Valoración médica y psicológica inicial completada."
)
