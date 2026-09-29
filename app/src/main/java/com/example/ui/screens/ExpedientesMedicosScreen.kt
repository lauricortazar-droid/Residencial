package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.ExpedienteEntity
import com.example.data.model.ResidentEntity
import com.example.ui.components.ExpedienteFormDialog
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

/**
 * Pantalla completa en Jetpack Compose para visualizar y administrar la lista
 * de expedientes médicos y clínicos almacenados en la base de datos local Room.
 */
@Composable
fun ExpedientesMedicosScreen(
    expedientes: List<ExpedienteEntity>,
    residents: List<ResidentEntity> = emptyList(),
    onSaveExpediente: (ExpedienteEntity) -> Unit = {},
    onDeleteExpediente: (ExpedienteEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("TODOS") }
    var selectedTipoIngresoFilter by remember { mutableStateOf("TODOS") }

    var selectedExpedienteForDetail by remember { mutableStateOf<ExpedienteEntity?>(null) }
    var expedienteToEdit by remember { mutableStateOf<ExpedienteEntity?>(null) }
    var showEditorDialog by remember { mutableStateOf(false) }

    // Filtrado reactivo en tiempo real
    val filteredExpedientes = remember(expedientes, searchQuery, selectedStatusFilter, selectedTipoIngresoFilter) {
        expedientes.filter { exp ->
            val matchesSearch = searchQuery.isBlank() ||
                    exp.folioExpediente.contains(searchQuery, ignoreCase = true) ||
                    exp.residentName.contains(searchQuery, ignoreCase = true) ||
                    exp.diagnosticoPrincipal.contains(searchQuery, ignoreCase = true) ||
                    exp.sustanciaDeImpacto.contains(searchQuery, ignoreCase = true) ||
                    exp.medicoTratante.contains(searchQuery, ignoreCase = true) ||
                    exp.psicologoResponsable.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "TODOS" -> true
                else -> exp.estatusExpediente.equals(selectedStatusFilter, ignoreCase = true)
            }

            val matchesTipo = when (selectedTipoIngresoFilter) {
                "TODOS" -> true
                else -> exp.tipoIngreso.equals(selectedTipoIngresoFilter, ignoreCase = true)
            }

            matchesSearch && matchesStatus && matchesTipo
        }
    }

    // Métricas para el resumen superior
    val totalExpedientes = expedientes.size
    val activosCount = expedientes.count { it.estatusExpediente.equals("ACTIVO", ignoreCase = true) }
    val revisionCount = expedientes.count { it.estatusExpediente.equals("EN_REVISION", ignoreCase = true) }
    val consentimientoCount = expedientes.count { it.consentimientoFirmado }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    expedienteToEdit = null
                    showEditorDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nuevo Expediente", fontWeight = FontWeight.SemiBold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_expediente_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Cabecera principal
            SectionHeader(
                title = "Expedientes Médicos y Clínicos",
                subtitle = "Historial clínico, diagnósticos CIE/DSM y tratamiento en Room"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjetas de Métricas Rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpedienteMetricCard(
                    title = "Total",
                    count = totalExpedientes.toString(),
                    icon = Icons.Default.FolderShared,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                ExpedienteMetricCard(
                    title = "Activos",
                    count = activosCount.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = StatusActiveGreen,
                    modifier = Modifier.weight(1f)
                )
                ExpedienteMetricCard(
                    title = "En Revisión",
                    count = revisionCount.toString(),
                    icon = Icons.Default.PendingActions,
                    color = Color(0xFFD68100),
                    modifier = Modifier.weight(1f)
                )
                ExpedienteMetricCard(
                    title = "Consentimiento",
                    count = "$consentimientoCount/${totalExpedientes}",
                    icon = Icons.Default.VerifiedUser,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buscador reactivo
            SearchBarWidget(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar por folio, nombre, diagnóstico CIE, médico...",
                modifier = Modifier.testTag("expediente_search_bar")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filtros de Estatus
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val statusList = listOf(
                    "TODOS" to "Todos los estados",
                    "ACTIVO" to "Activos",
                    "EN_REVISION" to "En Revisión",
                    "CONCLUIDO" to "Concluidos",
                    "ARCHIVADO" to "Archivados"
                )
                items(statusList) { (key, label) ->
                    val isSelected = selectedStatusFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Expedientes
            if (filteredExpedientes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FolderOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No se encontraron expedientes con '$searchQuery'" else "No hay expedientes médicos en esta categoría.",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Puedes registrar un nuevo expediente médico usando el botón inferior.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedStatusFilter = "TODOS"
                                    expedienteToEdit = null
                                    showEditorDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Crear Expediente Médico")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredExpedientes, key = { it.id }) { expediente ->
                        ExpedienteItemCard(
                            expediente = expediente,
                            onCardClick = { selectedExpedienteForDetail = expediente },
                            onEditClick = {
                                expedienteToEdit = expediente
                                showEditorDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal de Detalle Completo del Expediente
    selectedExpedienteForDetail?.let { expediente ->
        ExpedienteDetailModal(
            expediente = expediente,
            onDismiss = { selectedExpedienteForDetail = null },
            onEdit = {
                selectedExpedienteForDetail = null
                expedienteToEdit = expediente
                showEditorDialog = true
            }
        )
    }

    // Modal de Edición / Creación de Expediente en Room usando ExpedienteFormDialog
    if (showEditorDialog) {
        ExpedienteFormDialog(
            initialExpediente = expedienteToEdit,
            residents = residents,
            onDismiss = { showEditorDialog = false },
            onSaveSuccess = { updatedExpediente ->
                onSaveExpediente(updatedExpediente)
                showEditorDialog = false
            }
        )
    }
}

/**
 * Tarjeta individual para visualizar un expediente médico dentro del listado.
 */
@Composable
fun ExpedienteItemCard(
    expediente: ExpedienteEntity,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("expediente_card_${expediente.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Folio y Estatus
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
                            imageVector = Icons.Default.MedicalInformation,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = expediente.folioExpediente,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = expediente.residentName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // Badge de Estatus
                val (badgeBg, badgeTextColor) = when (expediente.estatusExpediente) {
                    "ACTIVO" -> StatusActiveGreen.copy(alpha = 0.15f) to StatusActiveGreen
                    "EN_REVISION" -> Color(0xFFFFA000).copy(alpha = 0.15f) to Color(0xFFD68100)
                    "CONCLUIDO" -> Color(0xFF2196F3).copy(alpha = 0.15f) to Color(0xFF1976D2)
                    else -> Color(0xFF9E9E9E).copy(alpha = 0.15f) to Color(0xFF616161)
                }

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = expediente.estatusExpediente,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Diagnóstico Principal (CIE/DSM)
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = expediente.diagnosticoPrincipal,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sustancia de Impacto y Patrón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = expediente.sustanciaDeImpacto,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = expediente.tiempoDeConsumo,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Profesionales asignados y Consentimiento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expediente.medicoTratante,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = expediente.psicologoResponsable,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (expediente.consentimientoFirmado) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Consentimiento Firmado",
                            tint = StatusActiveGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "NOM-028",
                            style = MaterialTheme.typography.labelSmall.copy(color = StatusActiveGreen, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar expediente",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de métrica compacta para estadísticas de expedientes.
 */
@Composable
private fun ExpedienteMetricCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = color)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/**
 * Modal detallado para visualizar la información clínica completa de un expediente.
 */
@Composable
fun ExpedienteDetailModal(
    expediente: ExpedienteEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header del Modal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = expediente.folioExpediente,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = expediente.residentName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        DetailSection(title = "Datos Generales") {
                            DetailRow(label = "Fecha de Apertura", value = expediente.fechaApertura)
                            DetailRow(label = "Tipo de Ingreso", value = expediente.tipoIngreso)
                            DetailRow(label = "Estatus del Expediente", value = expediente.estatusExpediente)
                            DetailRow(
                                label = "Consentimiento Informado (NOM-028)",
                                value = if (expediente.consentimientoFirmado) "FIRMADO Y VIGENTE" else "PENDIENTE DE FIRMA"
                            )
                        }
                    }

                    item {
                        DetailSection(title = "Diagnóstico y Antecedentes") {
                            DetailRow(label = "Diagnóstico Principal", value = expediente.diagnosticoPrincipal)
                            DetailRow(label = "Antecedentes Patológicos", value = expediente.antecedentesPatologicos)
                            DetailRow(label = "Sustancia de Impacto", value = expediente.sustanciaDeImpacto)
                            DetailRow(label = "Tiempo de Consumo", value = expediente.tiempoDeConsumo)
                        }
                    }

                    item {
                        DetailSection(title = "Plan Terapéutico y Equipo Responsable") {
                            DetailRow(label = "Plan de Tratamiento", value = expediente.planTratamiento)
                            DetailRow(label = "Médico Tratante", value = expediente.medicoTratante)
                            DetailRow(label = "Psicólogo Responsable", value = expediente.psicologoResponsable)
                        }
                    }

                    item {
                        DetailSection(title = "Notas de Ingreso y Observaciones") {
                            Text(
                                text = expediente.notasIngreso,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Editar Expediente")
                    }
                }
            }
        }
    }
}

/**
 * Modal formulario para crear o editar un expediente médico con persistencia en Room.
 */
@Composable
fun ExpedienteEditorModal(
    expediente: ExpedienteEntity?,
    residents: List<ResidentEntity>,
    onDismiss: () -> Unit,
    onSave: (ExpedienteEntity) -> Unit
) {
    var folio by remember {
        mutableStateOf(expediente?.folioExpediente ?: "EXP-SND-2024-${(100..999).random()}")
    }
    var selectedResidentName by remember {
        mutableStateOf(expediente?.residentName ?: residents.firstOrNull()?.fullName ?: "")
    }
    var selectedResidentId by remember {
        mutableStateOf(expediente?.residentId ?: residents.firstOrNull()?.id ?: 1)
    }
    var fechaApertura by remember {
        mutableStateOf(expediente?.fechaApertura ?: "2024-09-29")
    }
    var tipoIngreso by remember {
        mutableStateOf(expediente?.tipoIngreso ?: "VOLUNTARIO")
    }
    var diagnostico by remember {
        mutableStateOf(expediente?.diagnosticoPrincipal ?: "")
    }
    var antecedentes by remember {
        mutableStateOf(expediente?.antecedentesPatologicos ?: "Sin antecedentes crónicos")
    }
    var sustancia by remember {
        mutableStateOf(expediente?.sustanciaDeImpacto ?: "")
    }
    var tiempoConsumo by remember {
        mutableStateOf(expediente?.tiempoDeConsumo ?: "1 año")
    }
    var planTratamiento by remember {
        mutableStateOf(expediente?.planTratamiento ?: "Tratamiento Residencial de 6 meses - Modelo Comunidad Terapéutica")
    }
    var medico by remember {
        mutableStateOf(expediente?.medicoTratante ?: "Dr. Armando Valdés Soto")
    }
    var psicologo by remember {
        mutableStateOf(expediente?.psicologoResponsable ?: "Psic. Mariana Flores Peñaloza")
    }
    var estatus by remember {
        mutableStateOf(expediente?.estatusExpediente ?: "ACTIVO")
    }
    var consentimientoFirmado by remember {
        mutableStateOf(expediente?.consentimientoFirmado ?: true)
    }
    var notas by remember {
        mutableStateOf(expediente?.notasIngreso ?: "Expediente formal aperturado conforme a la Norma Oficial Mexicana NOM-028-SSA2.")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = if (expediente == null) "Nuevo Expediente Médico" else "Editar Expediente",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Almacenamiento local seguro en Room Database",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        OutlinedTextField(
                            value = folio,
                            onValueChange = { folio = it },
                            label = { Text("Folio Único del Expediente") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = selectedResidentName,
                            onValueChange = { selectedResidentName = it },
                            label = { Text("Nombre Completo del Residente") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = diagnostico,
                            onValueChange = { diagnostico = it },
                            label = { Text("Diagnóstico Principal (CIE-11 / DSM-5)") },
                            placeholder = { Text("Ej. F10.2 Síndrome de dependencia al alcohol") },
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
                                label = { Text("Sustancia de Impacto") },
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
                            value = antecedentes,
                            onValueChange = { antecedentes = it },
                            label = { Text("Antecedentes Patológicos") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = planTratamiento,
                            onValueChange = { planTratamiento = it },
                            label = { Text("Plan de Tratamiento Integral") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = medico,
                                onValueChange = { medico = it },
                                label = { Text("Médico Tratante") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = psicologo,
                                onValueChange = { psicologo = it },
                                label = { Text("Psicólogo Asignado") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = consentimientoFirmado,
                                onCheckedChange = { consentimientoFirmado = it }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Consentimiento Informado Firmado (NOM-028)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = { Text("Notas de Ingreso y Observaciones") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (folio.isNotBlank() && selectedResidentName.isNotBlank()) {
                                val itemToPersist = ExpedienteEntity(
                                    id = expediente?.id ?: 0,
                                    folioExpediente = folio,
                                    residentId = selectedResidentId,
                                    residentName = selectedResidentName,
                                    fechaApertura = fechaApertura,
                                    tipoIngreso = tipoIngreso,
                                    diagnosticoPrincipal = if (diagnostico.isBlank()) "En proceso de valoración clínica" else diagnostico,
                                    antecedentesPatologicos = antecedentes,
                                    sustanciaDeImpacto = if (sustancia.isBlank()) "No especificada" else sustancia,
                                    tiempoDeConsumo = tiempoConsumo,
                                    planTratamiento = planTratamiento,
                                    medicoTratante = medico,
                                    psicologoResponsable = psicologo,
                                    estatusExpediente = estatus,
                                    consentimientoFirmado = consentimientoFirmado,
                                    notasIngreso = notas
                                )
                                onSave(itemToPersist)
                            }
                        }
                    ) {
                        Text("Guardar en Room")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
