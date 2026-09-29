package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.components.StatusChip
import com.example.ui.navigation.MainSection
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    residents: List<ResidentEntity>,
    clinicalRecords: List<ClinicalRecordEntity>,
    medications: List<MedicationEntity>,
    transactions: List<FinanceTransactionEntity>,
    agendaEvents: List<AgendaEventEntity>,
    onNavigate: (MainSection) -> Unit,
    onToggleMedication: (MedicationEntity) -> Unit,
    onOpenAddResident: () -> Unit,
    onOpenAddPayment: () -> Unit,
    onOpenAddBitacora: () -> Unit,
    modifier: Modifier = Modifier
) {
    val internados = residents.filter { it.status == ResidentStatus.INTERNADO.name }
    val preingresos = residents.filter { it.status == ResidentStatus.PREINGRESO.name }
    val egresados = residents.filter { it.status == ResidentStatus.EGRESADO.name }
    val seguimiento = residents.filter { it.status == ResidentStatus.SEGUIMIENTO.name }

    val pendingMeds = medications.filter { !it.isTakenToday }
    val totalIngresos = transactions.filter { it.type == "INGRESO" }.sumOf { it.amount }
    val totalEgresos = transactions.filter { it.type == "EGRESO" }.sumOf { it.amount }
    val balanceCaja = totalIngresos - totalEgresos

    val capacityMax = 35
    val currentOccupancy = internados.size
    val occupancyPercent = (currentOccupancy.toFloat() / capacityMax.toFloat()).coerceIn(0f, 1f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner & Facility Status
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Comunidad Senda",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                            Text(
                                text = "Panel de Control Operativo y Clínico",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "EN SERVICIO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Occupancy Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ocupación de Camas:",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "$currentOccupancy de $capacityMax camas (${(occupancyPercent * 100).toInt()}%)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { occupancyPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Internados",
                    value = "${internados.size}",
                    subtitle = "${preingresos.size} preingresos",
                    icon = Icons.Default.Bed,
                    iconColor = StatusActiveGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(MainSection.USUARIOS) }
                )
                StatCard(
                    title = "Medicamentos",
                    value = "${pendingMeds.size}",
                    subtitle = "pendientes hoy",
                    icon = Icons.Default.Medication,
                    iconColor = AlertCriticalRed,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(MainSection.CLINICA) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Saldo en Caja",
                    value = "$${String.format("%,.0f", balanceCaja)}",
                    subtitle = "$${String.format("%,.0f", totalIngresos)} ingresos",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconColor = TertiaryAmber,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(MainSection.FINANZAS) }
                )
                StatCard(
                    title = "Egresados",
                    value = "${egresados.size}",
                    subtitle = "${seguimiento.size} en seguimiento",
                    icon = Icons.Default.Verified,
                    iconColor = StatusEgresadoBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(MainSection.USUARIOS) }
                )
            }
        }

        // Quick Actions Row
        item {
            SectionHeader(
                title = "Acciones Rápidas",
                subtitle = "Operaciones frecuentes de guardia"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onOpenAddResident,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Ingreso", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(
                    onClick = onOpenAddPayment,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Pago", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                FilledTonalButton(
                    onClick = onOpenAddBitacora,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Bitácora", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Medication Daily Administration Card
        item {
            SectionHeader(
                title = "Administración de Medicamentos Hoy",
                subtitle = "Kardex de fármacos prescritos",
                actionText = "Ver Clínica",
                onActionClick = { onNavigate(MainSection.CLINICA) }
            )

            if (medications.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No hay medicamentos registrados en el kardex.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        medications.forEachIndexed { index, med ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = med.isTakenToday,
                                    onCheckedChange = { onToggleMedication(med) },
                                    modifier = Modifier.testTag("med_check_${med.id}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${med.medicationName} (${med.dosage})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${med.residentName} • ${med.scheduleTime}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                Surface(
                                    color = if (med.isTakenToday) StatusActiveGreen.copy(alpha = 0.15f) else AlertCriticalRed.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (med.isTakenToday) "Suministrado" else "Pendiente",
                                        color = if (med.isTakenToday) StatusActiveGreen else AlertCriticalRed,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (index < medications.size - 1) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Agenda & Events Upcoming
        item {
            SectionHeader(
                title = "Próximas Actividades y Visitas",
                subtitle = "Agenda general del centro",
                actionText = "Ver Agenda",
                onActionClick = { onNavigate(MainSection.AGENDA) }
            )

            agendaEvents.take(3).forEach { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (event.type) {
                                    "VISITA_FAMILIAR" -> Icons.Default.FamilyRestroom
                                    "CITA_MEDICA" -> Icons.Default.MedicalServices
                                    "REUNION" -> Icons.Default.Groups
                                    else -> Icons.Default.Event
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${event.date} • ${event.time} | ${event.location}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Recent Clinical Records & Incidents
        item {
            SectionHeader(
                title = "Últimas Notas Clínicas e Incidentes",
                subtitle = "Medicina, Psicología y Consejería",
                actionText = "Ver Todo",
                onActionClick = { onNavigate(MainSection.CLINICA) }
            )

            clinicalRecords.take(3).forEach { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusChip(status = record.severity)
                            Text(
                                text = "${record.category} • ${record.date}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = record.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Residente: ${record.residentName} (${record.professionalName})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = record.notes,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
