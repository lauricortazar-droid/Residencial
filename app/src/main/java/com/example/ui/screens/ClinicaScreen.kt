package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusChip
import com.example.ui.theme.*

@Composable
fun ClinicaScreen(
    residents: List<ResidentEntity>,
    clinicalRecords: List<ClinicalRecordEntity>,
    medications: List<MedicationEntity>,
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleMedication: (MedicationEntity) -> Unit,
    onAddClinicalRecord: (ClinicalRecordEntity) -> Unit,
    onAddMedication: (MedicationEntity) -> Unit,
    expedientes: List<ExpedienteEntity> = emptyList(),
    onSaveExpediente: (ExpedienteEntity) -> Unit = {},
    onDeleteExpediente: (ExpedienteEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddMedDialog by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "TODOS" to "Todos",
        "EXPEDIENTES" to "Expedientes Clínicos (${expedientes.size})",
        "MEDICINA" to "Medicina",
        "PSICOLOGIA" to "Psicología",
        "PSIQUIATRIA" to "Psiquiatría",
        "CONSEJERIA" to "Consejería",
        "TRATAMIENTO" to "Tratamiento",
        "MEDICAMENTOS" to "Medicamentos",
        "INCIDENTES" to "Incidentes"
    )

    Scaffold(
        floatingActionButton = {
            if (selectedSubTab != "EXPEDIENTES") {
                Column(horizontalAlignment = Alignment.End) {
                    SmallFloatingActionButton(
                        onClick = { showAddMedDialog = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.testTag("add_medication_fab")
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = "Nuevo Medicamento")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ExtendedFloatingActionButton(
                        onClick = { showAddNoteDialog = true },
                        icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                        text = { Text("Nota / Incidente") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("add_clinical_record_fab")
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SearchBarWidget(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = "Buscar en notas clínicas, médicos o medicamentos..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subTabs) { (key, label) ->
                    val isSelected = selectedSubTab == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSubTabSelected(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedSubTab == "EXPEDIENTES") {
                ExpedientesMedicosScreen(
                    expedientes = expedientes,
                    residents = residents,
                    onSaveExpediente = onSaveExpediente,
                    onDeleteExpediente = onDeleteExpediente,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                // Treatment Phases Overview if on TODOS or TRATAMIENTO
                if (selectedSubTab == "TODOS" || selectedSubTab == "TRATAMIENTO") {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "PROGRAMA DE TRATAMIENTO RESIDENCIAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Modelo Terapéutico en 4 Fases Progresivas",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                val phases = listOf(
                                    "Fase 1: Desintoxicación y Valoración" to residents.count { it.currentPhase.contains("1") },
                                    "Fase 2: Deshabituación y Hábitos" to residents.count { it.currentPhase.contains("2") },
                                    "Fase 3: Reestructuración Cognitiva" to residents.count { it.currentPhase.contains("3") },
                                    "Fase 4: Reinserción y Proyecto de Vida" to residents.count { it.currentPhase.contains("4") }
                                )

                                phases.forEachIndexed { idx, (phaseName, count) ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = phaseName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                            Text(text = "$count residentes", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val progress = if (residents.isNotEmpty()) count.toFloat() / residents.size.toFloat() else 0f
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = when (idx) {
                                                0 -> AlertCriticalRed
                                                1 -> StatusPreingresoAmber
                                                2 -> StatusActiveGreen
                                                else -> StatusEgresadoBlue
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Medications Section if on TODOS or MEDICAMENTOS
                if (selectedSubTab == "TODOS" || selectedSubTab == "MEDICAMENTOS") {
                    item {
                        SectionHeader(
                            title = "Kardex de Medicamentos",
                            subtitle = "Control de administración diaria por turnos"
                        )
                    }

                    val filteredMeds = medications.filter { med ->
                        searchQuery.isBlank() ||
                                med.medicationName.contains(searchQuery, ignoreCase = true) ||
                                med.residentName.contains(searchQuery, ignoreCase = true)
                    }

                    if (filteredMeds.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text("No hay medicamentos registrados.", modifier = Modifier.padding(14.dp))
                            }
                        }
                    } else {
                        items(filteredMeds, key = { it.id }) { med ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = med.isTakenToday,
                                        onCheckedChange = { onToggleMedication(med) }
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = med.medicationName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Residente: ${med.residentName} • Dosis: ${med.dosage}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                        )
                                        Text(
                                            text = "Horario: ${med.scheduleTime} • Prescrito: ${med.prescribedBy}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        if (med.instructions.isNotBlank()) {
                                            Text(
                                                text = "Indicación: ${med.instructions}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                    Surface(
                                        color = if (med.isTakenToday) StatusActiveGreen.copy(alpha = 0.15f) else AlertCriticalRed.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (med.isTakenToday) "Suministrado" else "Pendiente",
                                            color = if (med.isTakenToday) StatusActiveGreen else AlertCriticalRed,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Clinical Records and Incidents
                val filteredRecords = clinicalRecords.filter { rec ->
                    val matchesTab = when (selectedSubTab) {
                        "TODOS", "TRATAMIENTO", "MEDICAMENTOS" -> true
                        "INCIDENTES" -> rec.category == "INCIDENTE"
                        else -> rec.category.equals(selectedSubTab, ignoreCase = true)
                    }
                    val matchesSearch = searchQuery.isBlank() ||
                            rec.title.contains(searchQuery, ignoreCase = true) ||
                            rec.notes.contains(searchQuery, ignoreCase = true) ||
                            rec.residentName.contains(searchQuery, ignoreCase = true) ||
                            rec.professionalName.contains(searchQuery, ignoreCase = true)

                    matchesTab && matchesSearch
                }

                if (selectedSubTab != "MEDICAMENTOS") {
                    item {
                        SectionHeader(
                            title = "Notas de Evolución e Incidentes",
                            subtitle = "Registro cronológico interdisciplinario"
                        )
                    }

                    if (filteredRecords.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text("No hay registros en esta categoría.", modifier = Modifier.padding(14.dp))
                            }
                        }
                    } else {
                        items(filteredRecords, key = { it.id }) { record ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = record.category,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            StatusChip(status = record.severity)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = record.date,
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = record.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Usuario: ${record.residentName} • Profesional: ${record.professionalName}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = record.notes,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }
        }
    }

    // Add Clinical Record Dialog
    if (showAddNoteDialog) {
        AddClinicalRecordDialog(
            residents = residents,
            onDismiss = { showAddNoteDialog = false },
            onSave = {
                onAddClinicalRecord(it)
                showAddNoteDialog = false
            }
        )
    }

    // Add Medication Dialog
    if (showAddMedDialog) {
        AddMedicationDialog(
            residents = residents,
            onDismiss = { showAddMedDialog = false },
            onSave = {
                onAddMedication(it)
                showAddMedDialog = false
            }
        )
    }
}

@Composable
fun AddClinicalRecordDialog(
    residents: List<ResidentEntity>,
    onDismiss: () -> Unit,
    onSave: (ClinicalRecordEntity) -> Unit
) {
    var selectedResident by remember { mutableStateOf(residents.firstOrNull()) }
    var category by remember { mutableStateOf("PSICOLOGIA") }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var professionalName by remember { mutableStateOf("Psic. Mariana Flores") }
    var severity by remember { mutableStateOf("NORMAL") }

    val categories = listOf("MEDICINA", "PSICOLOGIA", "PSIQUIATRIA", "CONSEJERIA", "INCIDENTE")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Nueva Nota Clínica / Incidente",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item {
                    Text("Área / Especialidad:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Text("Usuario a Evaluar:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(residents) { res ->
                            FilterChip(
                                selected = selectedResident?.id == res.id,
                                onClick = { selectedResident = res },
                                label = { Text(res.fullName.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título de la Nota / Diagnóstico") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Detalle de Evolución / Hechos") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }

                item {
                    OutlinedTextField(
                        value = professionalName,
                        onValueChange = { professionalName = it },
                        label = { Text("Especialista / Consejero") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("Severidad:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("NORMAL", "MODERADO", "URGENTE").forEach { sev ->
                            FilterChip(
                                selected = severity == sev,
                                onClick = { severity = sev },
                                label = { Text(sev, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank() && selectedResident != null) {
                                    val record = ClinicalRecordEntity(
                                        residentId = selectedResident!!.id,
                                        residentName = selectedResident!!.fullName,
                                        category = category,
                                        title = title.trim(),
                                        notes = notes.trim(),
                                        professionalName = professionalName.trim(),
                                        date = "2024-09-28",
                                        severity = severity
                                    )
                                    onSave(record)
                                }
                            }
                        ) {
                            Text("Guardar Nota")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMedicationDialog(
    residents: List<ResidentEntity>,
    onDismiss: () -> Unit,
    onSave: (MedicationEntity) -> Unit
) {
    var selectedResident by remember { mutableStateOf(residents.firstOrNull()) }
    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("1 tableta") }
    var scheduleTime by remember { mutableStateOf("08:00 AM") }
    var instructions by remember { mutableStateOf("Tomar con alimentos") }
    var prescribedBy by remember { mutableStateOf("Dr. Guillermo Garza") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Registrar Medicamento en Kardex",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item {
                    Text("Usuario:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(residents) { res ->
                            FilterChip(
                                selected = selectedResident?.id == res.id,
                                onClick = { selectedResident = res },
                                label = { Text(res.fullName.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = medName,
                        onValueChange = { medName = it },
                        label = { Text("Nombre del Medicamento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { dosage = it },
                            label = { Text("Dosis") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = scheduleTime,
                            onValueChange = { scheduleTime = it },
                            label = { Text("Horario (ej. 08:00 AM)") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Indicaciones de Toma") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = prescribedBy,
                        onValueChange = { prescribedBy = it },
                        label = { Text("Médico que Prescribe") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (medName.isNotBlank() && selectedResident != null) {
                                    val med = MedicationEntity(
                                        residentId = selectedResident!!.id,
                                        residentName = selectedResident!!.fullName,
                                        medicationName = medName.trim(),
                                        dosage = dosage.trim(),
                                        scheduleTime = scheduleTime.trim(),
                                        instructions = instructions.trim(),
                                        isTakenToday = false,
                                        prescribedBy = prescribedBy.trim()
                                    )
                                    onSave(med)
                                }
                            }
                        ) {
                            Text("Guardar en Kardex")
                        }
                    }
                }
            }
        }
    }
}
