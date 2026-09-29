package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ClinicalRecordEntity
import com.example.data.model.ExpedienteEntity
import com.example.data.model.MedicationEntity
import com.example.data.model.ResidentEntity
import com.example.data.model.ResidentStatus
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.StatusChip
import com.example.ui.theme.*

@Composable
fun UsuariosScreen(
    residents: List<ResidentEntity>,
    clinicalRecords: List<ClinicalRecordEntity>,
    medications: List<MedicationEntity>,
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSaveResident: (ResidentEntity) -> Unit,
    onChangeStatus: (ResidentEntity, String, String) -> Unit,
    onDeleteResident: (ResidentEntity) -> Unit,
    expedientes: List<ExpedienteEntity> = emptyList(),
    onSaveExpediente: (ExpedienteEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var residentToEdit by remember { mutableStateOf<ResidentEntity?>(null) }
    var residentForExpediente by remember { mutableStateOf<ResidentEntity?>(null) }
    var residentForStatusChange by remember { mutableStateOf<ResidentEntity?>(null) }
    var expedienteToEdit by remember { mutableStateOf<ExpedienteEntity?>(null) }
    var showExpedienteEditor by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "TODOS" to "Todos",
        ResidentStatus.PREINGRESO.name to "Preingreso",
        ResidentStatus.INTERNADO.name to "Internados",
        ResidentStatus.EGRESADO.name to "Egresados",
        ResidentStatus.SEGUIMIENTO.name to "Seguimiento",
        "EXPEDIENTES" to "Expedientes"
    )

    val filteredExpedientes = expedientes.filter { exp ->
        searchQuery.isBlank() ||
                exp.folioExpediente.contains(searchQuery, ignoreCase = true) ||
                exp.residentName.contains(searchQuery, ignoreCase = true) ||
                exp.diagnosticoPrincipal.contains(searchQuery, ignoreCase = true) ||
                exp.medicoTratante.contains(searchQuery, ignoreCase = true)
    }

    val filteredList = residents.filter { res ->
        val matchesTab = when (selectedSubTab) {
            "TODOS", "EXPEDIENTES" -> true
            else -> res.status.equals(selectedSubTab, ignoreCase = true)
        }
        val matchesSearch = searchQuery.isBlank() ||
                res.fullName.contains(searchQuery, ignoreCase = true) ||
                res.folio.contains(searchQuery, ignoreCase = true) ||
                res.primaryReason.contains(searchQuery, ignoreCase = true) ||
                res.tutorName.contains(searchQuery, ignoreCase = true)

        matchesTab && matchesSearch
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedSubTab == "EXPEDIENTES") {
                        expedienteToEdit = null
                        showExpedienteEditor = true
                    } else {
                        residentToEdit = null
                        showAddDialog = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selectedSubTab == "EXPEDIENTES") Icons.Default.PostAdd else Icons.Default.PersonAdd,
                        contentDescription = null
                    )
                },
                text = { Text(if (selectedSubTab == "EXPEDIENTES") "Nuevo Expediente" else "Nuevo Usuario") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_resident_fab")
            )
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

            // Search Bar
            SearchBarWidget(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = if (selectedSubTab == "EXPEDIENTES") "Buscar expediente por folio, residente, diagnóstico o médico..." else "Buscar por nombre, folio SND, sustancia o tutor..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs Filter Bar
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subTabs) { (key, label) ->
                    val isSelected = selectedSubTab == key
                    val count = when (key) {
                        "TODOS" -> residents.size
                        "EXPEDIENTES" -> expedientes.size
                        else -> residents.count { it.status.equals(key, ignoreCase = true) }
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSubTabSelected(key) },
                        label = { Text("$label ($count)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List Content: Expedientes or Residents
            if (selectedSubTab == "EXPEDIENTES") {
                if (filteredExpedientes.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FolderShared,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No hay expedientes clínicos registrados.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                expedienteToEdit = null
                                showExpedienteEditor = true
                            }) {
                                Icon(Icons.Default.PostAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Crear Primer Expediente en Room")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredExpedientes, key = { it.id }) { exp ->
                            ExpedienteCard(
                                expediente = exp,
                                onEdit = {
                                    expedienteToEdit = exp
                                    showExpedienteEditor = true
                                }
                            )
                        }
                    }
                }
            } else {
                if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No se encontraron usuarios en esta categoría.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredList, key = { it.id }) { resident ->
                            ResidentCard(
                                resident = resident,
                                onOpenExpediente = { residentForExpediente = resident },
                                onEdit = {
                                    residentToEdit = resident
                                    showAddDialog = true
                                },
                                onChangeStatus = { residentForStatusChange = resident },
                                onCallTutor = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${resident.tutorPhone}"))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Resident Dialog
    if (showAddDialog) {
        ResidentFormDialog(
            initialResident = residentToEdit,
            onDismiss = { showAddDialog = false },
            onSave = {
                onSaveResident(it)
                showAddDialog = false
            }
        )
    }

    // Expediente Full Modal
    residentForExpediente?.let { res ->
        val resClinical = clinicalRecords.filter { it.residentId == res.id }
        val resMeds = medications.filter { it.residentId == res.id }
        ExpedienteDetailDialog(
            resident = res,
            clinicalRecords = resClinical,
            medications = resMeds,
            onDismiss = { residentForExpediente = null }
        )
    }

    // Expediente Room Editor Dialog
    if (showExpedienteEditor) {
        ExpedienteEditorDialog(
            initialExpediente = expedienteToEdit,
            residents = residents,
            onDismiss = { showExpedienteEditor = false },
            onSave = {
                onSaveExpediente(it)
                showExpedienteEditor = false
            }
        )
    }

    // Change Status Modal
    residentForStatusChange?.let { res ->
        ChangeStatusDialog(
            resident = res,
            onDismiss = { residentForStatusChange = null },
            onConfirm = { newStatus, newBed ->
                onChangeStatus(res, newStatus, newBed)
                residentForStatusChange = null
            }
        )
    }
}

@Composable
fun ResidentCard(
    resident: ResidentEntity,
    onOpenExpediente: () -> Unit,
    onEdit: () -> Unit,
    onChangeStatus: () -> Unit,
    onCallTutor: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Folio, Status Chip, Edit Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = resident.fullName.take(1),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = resident.folio,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "${resident.age} años • ${resident.gender}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status = resident.status)
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar usuario",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Full Name & Reason
            Text(
                text = resident.fullName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = resident.primaryReason,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bed and Phase Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Bed, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${resident.bedNumber} (${resident.roomName})",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = resident.currentPhase,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tutor & Financial indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tutor: ${resident.tutorName} (${resident.tutorRelationship})",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = if (resident.balanceDue <= 0) "Al corriente ($0.00)" else "Adeudo: $${String.format("%,.0f", resident.balanceDue)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (resident.balanceDue <= 0) StatusActiveGreen else AlertCriticalRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                IconButton(onClick = onCallTutor) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Llamar a tutor",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onChangeStatus,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Cambiar Estado", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onOpenExpediente,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.FolderShared, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Expediente", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ResidentFormDialog(
    initialResident: ResidentEntity?,
    onDismiss: () -> Unit,
    onSave: (ResidentEntity) -> Unit
) {
    var fullName by remember { mutableStateOf(initialResident?.fullName ?: "") }
    var ageStr by remember { mutableStateOf(initialResident?.age?.toString() ?: "28") }
    var gender by remember { mutableStateOf(initialResident?.gender ?: "Masculino") }
    var status by remember { mutableStateOf(initialResident?.status ?: ResidentStatus.INTERNADO.name) }
    var bedNumber by remember { mutableStateOf(initialResident?.bedNumber ?: "Cama 05") }
    var roomName by remember { mutableStateOf(initialResident?.roomName ?: "Dormitorio A - Esperanza") }
    var primaryReason by remember { mutableStateOf(initialResident?.primaryReason ?: "Dependencia a Sustancias") }
    var tutorName by remember { mutableStateOf(initialResident?.tutorName ?: "") }
    var tutorPhone by remember { mutableStateOf(initialResident?.tutorPhone ?: "") }
    var tutorRelationship by remember { mutableStateOf(initialResident?.tutorRelationship ?: "Madre") }
    var monthlyFeeStr by remember { mutableStateOf(initialResident?.monthlyFee?.toString() ?: "8500") }
    var counselorAssigned by remember { mutableStateOf(initialResident?.counselorAssigned ?: "Lic. Carlos Méndez") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (initialResident == null) "Nuevo Ingreso / Preingreso" else "Editar Usuario",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo del Usuario") },
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
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text("Edad") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text("Género") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Text("Estado del Usuario:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(ResidentStatus.PREINGRESO, ResidentStatus.INTERNADO, ResidentStatus.EGRESADO).forEach { s ->
                            FilterChip(
                                selected = status == s.name,
                                onClick = { status = s.name },
                                label = { Text(s.label, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = primaryReason,
                        onValueChange = { primaryReason = it },
                        label = { Text("Sustancia / Motivo de Tratamiento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bedNumber,
                            onValueChange = { bedNumber = it },
                            label = { Text("Cama") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = roomName,
                            onValueChange = { roomName = it },
                            label = { Text("Dormitorio") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = tutorName,
                        onValueChange = { tutorName = it },
                        label = { Text("Nombre del Tutor / Familiar Responsable") },
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
                            value = tutorPhone,
                            onValueChange = { tutorPhone = it },
                            label = { Text("Teléfono de Contacto") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tutorRelationship,
                            onValueChange = { tutorRelationship = it },
                            label = { Text("Parentesco") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = monthlyFeeStr,
                            onValueChange = { monthlyFeeStr = it },
                            label = { Text("Cuota Mensual ($)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = counselorAssigned,
                            onValueChange = { counselorAssigned = it },
                            label = { Text("Consejero Asignado") },
                            modifier = Modifier.weight(1.5f),
                            singleLine = true
                        )
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
                                if (fullName.isNotBlank()) {
                                    val randomFolio = initialResident?.folio ?: "SND-2024-${(100..999).random()}"
                                    val newRes = ResidentEntity(
                                        id = initialResident?.id ?: 0,
                                        folio = randomFolio,
                                        fullName = fullName.trim(),
                                        age = ageStr.toIntOrNull() ?: 28,
                                        gender = gender,
                                        status = status,
                                        admissionDate = initialResident?.admissionDate ?: "2024-09-28",
                                        dischargeDate = initialResident?.dischargeDate ?: "",
                                        bedNumber = bedNumber,
                                        roomName = roomName,
                                        primaryReason = primaryReason,
                                        currentPhase = initialResident?.currentPhase ?: "1. Desintoxicación",
                                        tutorName = tutorName,
                                        tutorPhone = tutorPhone,
                                        tutorRelationship = tutorRelationship,
                                        monthlyFee = monthlyFeeStr.toDoubleOrNull() ?: 8500.0,
                                        balanceDue = initialResident?.balanceDue ?: 0.0,
                                        bloodType = initialResident?.bloodType ?: "O+",
                                        allergies = initialResident?.allergies ?: "Ninguna",
                                        clinicalNotes = initialResident?.clinicalNotes ?: "",
                                        counselorAssigned = counselorAssigned
                                    )
                                    onSave(newRes)
                                }
                            }
                        ) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpedienteDetailDialog(
    resident: ResidentEntity,
    clinicalRecords: List<ClinicalRecordEntity>,
    medications: List<MedicationEntity>,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableStateOf("GENERAL") } // GENERAL, CLINICA, FARMACOS, FAMILIA

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXPEDIENTE CLÍNICO INTEGRAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Text(
                            text = resident.fullName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = "Folio: ${resident.folio} • ${resident.status}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Tab Switcher
                TabRow(
                    selectedTabIndex = when (activeTab) {
                        "GENERAL" -> 0
                        "CLINICA" -> 1
                        "FARMACOS" -> 2
                        else -> 3
                    }
                ) {
                    Tab(
                        selected = activeTab == "GENERAL",
                        onClick = { activeTab = "GENERAL" },
                        text = { Text("General", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == "CLINICA",
                        onClick = { activeTab = "CLINICA" },
                        text = { Text("Clínica", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == "FARMACOS",
                        onClick = { activeTab = "FARMACOS" },
                        text = { Text("Fármacos", fontSize = 12.sp) }
                    )
                    Tab(
                        selected = activeTab == "FAMILIA",
                        onClick = { activeTab = "FAMILIA" },
                        text = { Text("Familia", fontSize = 12.sp) }
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (activeTab) {
                        "GENERAL" -> {
                            item {
                                ExpedienteInfoRow("Fecha de Ingreso", resident.admissionDate)
                                ExpedienteInfoRow("Cama / Ubicación", "${resident.bedNumber} - ${resident.roomName}")
                                ExpedienteInfoRow("Fase de Tratamiento", resident.currentPhase)
                                ExpedienteInfoRow("Motivo Principal", resident.primaryReason)
                                ExpedienteInfoRow("Consejero a Cargo", resident.counselorAssigned)
                                ExpedienteInfoRow("Tipo de Sangre", resident.bloodType)
                                ExpedienteInfoRow("Alergias Conocidas", resident.allergies)
                                ExpedienteInfoRow("Observaciones", resident.clinicalNotes.ifBlank { "Sin observaciones adicionales registradas." })
                            }
                        }
                        "CLINICA" -> {
                            if (clinicalRecords.isEmpty()) {
                                item {
                                    Text("No hay notas clínicas registradas para este usuario aún.")
                                }
                            } else {
                                items(clinicalRecords) { rec ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = rec.category,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                                Text(text = rec.date, style = MaterialTheme.typography.labelSmall)
                                            }
                                            Text(
                                                text = rec.title,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = rec.notes,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Profesional: ${rec.professionalName}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        "FARMACOS" -> {
                            if (medications.isEmpty()) {
                                item {
                                    Text("El usuario no tiene medicamentos asignados en este momento.")
                                }
                            } else {
                                items(medications) { med ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = med.medicationName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Dosis: ${med.dosage} • Horario: ${med.scheduleTime}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "Indicaciones: ${med.instructions}",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                text = "Prescrito por: ${med.prescribedBy}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        "FAMILIA" -> {
                            item {
                                ExpedienteInfoRow("Tutor Principal", resident.tutorName)
                                ExpedienteInfoRow("Parentesco", resident.tutorRelationship)
                                ExpedienteInfoRow("Teléfono", resident.tutorPhone)
                                ExpedienteInfoRow("Cuota Mensual", "$${String.format("%,.0f", resident.monthlyFee)} MXN")
                                ExpedienteInfoRow("Adeudo Acumulado", "$${String.format("%,.0f", resident.balanceDue)} MXN")
                            }
                        }
                    }
                }

                // Footer
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = onDismiss) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpedienteInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), thickness = 0.5.dp)
    }
}

@Composable
fun ChangeStatusDialog(
    resident: ResidentEntity,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(resident.status) }
    var bedNumber by remember { mutableStateOf(resident.bedNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transición de Estado del Usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Selecciona el nuevo estatus para ${resident.fullName}:",
                    style = MaterialTheme.typography.bodyMedium
                )
                listOf(
                    ResidentStatus.PREINGRESO.name to "Preingreso (En evaluación)",
                    ResidentStatus.INTERNADO.name to "Internado (Residencial)",
                    ResidentStatus.EGRESADO.name to "Egresado (Alta)",
                    ResidentStatus.SEGUIMIENTO.name to "Seguimiento (Ambulatorio)"
                ).forEach { (st, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = st }
                    ) {
                        RadioButton(
                            selected = selectedStatus == st,
                            onClick = { selectedStatus = st }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (selectedStatus == ResidentStatus.INTERNADO.name) {
                    OutlinedTextField(
                        value = bedNumber,
                        onValueChange = { bedNumber = it },
                        label = { Text("Asignar Cama") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedStatus, bedNumber) }) {
                Text("Guardar Cambio")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ExpedienteCard(
    expediente: ExpedienteEntity,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Folio & Estatus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderShared,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = expediente.folioExpediente,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Apertura: ${expediente.fechaApertura}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Surface(
                    color = when (expediente.estatusExpediente) {
                        "ACTIVO" -> StatusActiveGreen.copy(alpha = 0.15f)
                        "EN_REVISION" -> Color(0xFFFFA000).copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = expediente.estatusExpediente,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (expediente.estatusExpediente) {
                                "ACTIVO" -> StatusActiveGreen
                                "EN_REVISION" -> Color(0xFFD68100)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = expediente.residentName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Diagnóstico Principal:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = expediente.diagnosticoPrincipal,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sustancia de impacto: ${expediente.sustanciaDeImpacto} (${expediente.tiempoDeConsumo})",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Médico: ${expediente.medicoTratante}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "Psicólogo: ${expediente.psicologoResponsable}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (expediente.consentimientoFirmado) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (expediente.consentimientoFirmado) StatusActiveGreen else Color(0xFFFFA000),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (expediente.consentimientoFirmado) "Consentimiento OK" else "Pendiente Firma",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar en Room")
                }
            }
        }
    }
}

@Composable
fun ExpedienteEditorDialog(
    initialExpediente: ExpedienteEntity?,
    residents: List<ResidentEntity>,
    onDismiss: () -> Unit,
    onSave: (ExpedienteEntity) -> Unit
) {
    var folio by remember { mutableStateOf(initialExpediente?.folioExpediente ?: "EXP-SND-${(100..999).random()}") }
    var residentName by remember { mutableStateOf(initialExpediente?.residentName ?: (residents.firstOrNull()?.fullName ?: "")) }
    var residentId by remember { mutableStateOf(initialExpediente?.residentId ?: (residents.firstOrNull()?.id ?: 1)) }
    var fechaApertura by remember { mutableStateOf(initialExpediente?.fechaApertura ?: "2024-03-01") }
    var tipoIngreso by remember { mutableStateOf(initialExpediente?.tipoIngreso ?: "VOLUNTARIO") }
    var diagnostico by remember { mutableStateOf(initialExpediente?.diagnosticoPrincipal ?: "Trastorno por Consumo de Sustancias Severo") }
    var sustancia by remember { mutableStateOf(initialExpediente?.sustanciaDeImpacto ?: "Metanfetamina / Cristal") }
    var tiempoConsumo by remember { mutableStateOf(initialExpediente?.tiempoDeConsumo ?: "2 años") }
    var planTratamiento by remember { mutableStateOf(initialExpediente?.planTratamiento ?: "Programa Residencial de 6 meses - Modelo Comunidad Terapéutica") }
    var medico by remember { mutableStateOf(initialExpediente?.medicoTratante ?: "Dr. Armando Valdés Soto") }
    var psicologo by remember { mutableStateOf(initialExpediente?.psicologoResponsable ?: "Psic. Mariana Flores Peñaloza") }
    var estatus by remember { mutableStateOf(initialExpediente?.estatusExpediente ?: "ACTIVO") }
    var consentimiento by remember { mutableStateOf(initialExpediente?.consentimientoFirmado ?: true) }
    var notas by remember { mutableStateOf(initialExpediente?.notasIngreso ?: "Valoración inicial completa, signos estables.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FolderShared, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialExpediente == null) "Nuevo Expediente Clínico (Room)" else "Editar Expediente",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = folio,
                        onValueChange = { folio = it },
                        label = { Text("Folio Expediente") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = residentName,
                        onValueChange = { residentName = it },
                        label = { Text("Nombre del Residente") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = diagnostico,
                        onValueChange = { diagnostico = it },
                        label = { Text("Diagnóstico Principal (CIE-11 / DSM-5)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = sustancia,
                            onValueChange = { sustancia = it },
                            label = { Text("Sustancia") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = tiempoConsumo,
                            onValueChange = { tiempoConsumo = it },
                            label = { Text("Tiempo Consumo") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = planTratamiento,
                        onValueChange = { planTratamiento = it },
                        label = { Text("Plan de Tratamiento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = medico,
                        onValueChange = { medico = it },
                        label = { Text("Médico Titular Responsable") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = psicologo,
                        onValueChange = { psicologo = it },
                        label = { Text("Psicólogo Asignado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = consentimiento,
                            onCheckedChange = { consentimiento = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Consentimiento Informado Firmado", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                item {
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas de Ingreso y Observaciones") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val exp = ExpedienteEntity(
                        id = initialExpediente?.id ?: 0,
                        folioExpediente = folio,
                        residentId = residentId,
                        residentName = residentName,
                        fechaApertura = fechaApertura,
                        tipoIngreso = tipoIngreso,
                        diagnosticoPrincipal = diagnostico,
                        sustanciaDeImpacto = sustancia,
                        tiempoDeConsumo = tiempoConsumo,
                        planTratamiento = planTratamiento,
                        medicoTratante = medico,
                        psicologoResponsable = psicologo,
                        estatusExpediente = estatus,
                        consentimientoFirmado = consentimiento,
                        notasIngreso = notas
                    )
                    onSave(exp)
                }
            ) {
                Text("Guardar en Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

