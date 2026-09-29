package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ResidentStatus(val label: String) {
    PREINGRESO("Preingreso"),
    INTERNADO("Internado"),
    EGRESADO("Egresado"),
    SEGUIMIENTO("Seguimiento")
}

@Entity(tableName = "residents")
data class ResidentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val folio: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val status: String = ResidentStatus.INTERNADO.name,
    val admissionDate: String,
    val dischargeDate: String = "",
    val bedNumber: String,
    val roomName: String,
    val primaryReason: String,
    val currentPhase: String = "1. Desintoxicación",
    val tutorName: String,
    val tutorPhone: String,
    val tutorRelationship: String,
    val monthlyFee: Double = 8500.0,
    val balanceDue: Double = 0.0,
    val bloodType: String = "O+",
    val allergies: String = "Ninguna conocida",
    val clinicalNotes: String = "",
    val counselorAssigned: String = "Lic. Carlos Méndez"
)

@Entity(tableName = "clinical_records")
data class ClinicalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val residentId: Int,
    val residentName: String,
    val category: String, // MEDICINA, PSICOLOGIA, PSIQUIATRIA, CONSEJERIA, INCIDENTE
    val title: String,
    val notes: String,
    val professionalName: String,
    val date: String,
    val severity: String = "NORMAL" // NORMAL, MODERADO, URGENTE
)

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val residentId: Int,
    val residentName: String,
    val medicationName: String,
    val dosage: String,
    val scheduleTime: String, // 08:00 AM, 02:00 PM, 08:00 PM
    val instructions: String,
    val isTakenToday: Boolean = false,
    val prescribedBy: String
)

@Entity(tableName = "finance_transactions")
data class FinanceTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // INGRESO, EGRESO
    val category: String, // PAGO_MENSUALIDAD, CUOTA_INGRESO, ALIMENTOS, FARMACIA, HONORARIOS, SERVICIOS, OTROS
    val concept: String,
    val amount: Double,
    val residentId: Int? = null,
    val residentName: String? = null,
    val date: String,
    val paymentMethod: String = "TRANSFERENCIA", // EFECTIVO, TRANSFERENCIA, TARJETA
    val receiptNumber: String
)

@Entity(tableName = "agenda_events")
data class AgendaEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // CITA_MEDICA, VISITA_FAMILIAR, REUNION, ACTIVIDAD_GRUPO, EVENTO
    val date: String,
    val time: String,
    val location: String,
    val description: String,
    val personInvolved: String
)

@Entity(tableName = "staff_members")
data class StaffMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val role: String, // Director, Médico, Psiquiatra, Psicólogo, Consejero, Enfermero, Cocinero, Vigilante
    val category: String, // SERVIDOR, PROFESIONAL, EMPLEADO
    val phone: String,
    val email: String,
    val shift: String, // Matutino, Vespertino, Nocturno, Mixto
    val cedulaProf: String = ""
)

@Entity(tableName = "operation_logs")
data class OperationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val logType: String, // BITACORA_GUARDIA, TURNO, CONTROL_LLAVES, INVENTARIO, MANTENIMIENTO, TAREA
    val title: String,
    val staffName: String,
    val timestamp: String,
    val shift: String,
    val details: String,
    val status: String = "OK" // OK, PENDIENTE, RESUELTO
)
