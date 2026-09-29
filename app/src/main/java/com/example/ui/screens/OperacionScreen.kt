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
import com.example.data.model.OperationLogEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun OperacionScreen(
    operationLogs: List<OperationLogEntity>,
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit,
    onAddLog: (OperationLogEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "BITACORAS" to "Bitácoras",
        "TURNOS" to "Turnos y Relevos",
        "LLAVES" to "Control de Llaves",
        "INVENTARIOS" to "Inventarios",
        "MANTENIMIENTO" to "Mantenimiento",
        "TAREAS" to "Tareas Comunitarias"
    )

    val inventoryItems = listOf(
        Triple("Pruebas Rápidas Multidroga 5 Parámetros", "45 piezas", "Stock Óptimo"),
        Triple("Guantes de Látex Desechables", "12 cajas", "Stock Óptimo"),
        Triple("Juegos de Sábanas y Cobijas", "40 juegos", "Suficiente"),
        Triple("Víveres y Granos No Perecederos", "120 kg arroz/frijol", "Abastecido"),
        Triple("Botiquín Antiséptico y Gasas", "8 paquetes", "Reabastecer en 10 días")
    )

    val keyInventory = listOf(
        Pair("Llave Maestra Portón Principal", "En poder de: Vigilancia Nocturna"),
        Pair("Llave Vitrina de Medicamentos Controlados", "En poder de: Enfermería / Dr. Valdés"),
        Pair("Llave Almacén General de Despensa", "En poder de: Jefa de Cocina"),
        Pair("Llave de Vehículo Institucional (Van)", "En caseta de guardia")
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                text = { Text("Nueva Bitácora/Reporte") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_operation_log_fab")
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

            SearchBarWidget(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Buscar en bitácoras, llaves o inventario..."
            )

            Spacer(modifier = Modifier.height(12.dp))

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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedSubTab == "LLAVES") {
                    item {
                        SectionHeader(
                            title = "Control y Custodia de Llaves",
                            subtitle = "Registro de resguardo de accesos críticos"
                        )
                    }

                    items(keyInventory) { (llave, custodia) ->
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
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(llave, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(custodia, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                        }
                    }
                }

                if (selectedSubTab == "INVENTARIOS") {
                    item {
                        SectionHeader(
                            title = "Inventario de Almacén e Insumos",
                            subtitle = "Control de existencias clínicas y de abastecimiento"
                        )
                    }

                    items(inventoryItems) { (articulo, stock, estatus) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(articulo, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text("Existencia: $stock", style = MaterialTheme.typography.bodySmall)
                                }
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = estatus,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedSubTab == "TAREAS") {
                    item {
                        SectionHeader(
                            title = "Tareas Comunitarias y Mantenimiento Residencial",
                            subtitle = "Terapia ocupacional y faenas asignadas"
                        )
                    }

                    val tareas = listOf(
                        Pair("Limpieza y Sanitización de Dormitorios A y B", "Asignado a: Brigada Matutina de Residentes"),
                        Pair("Cuidado de Huerto Orgánico y Áreas Verdes", "Asignado a: Taller Ocupacional de la Tarde"),
                        Pair("Apoyo en Comedor y Lavado de Loza", "Asignado a: Equipo Rotativo 1"),
                        Pair("Revisión de Extintores y Luces de Emergencia", "Asignado a: Mantenimiento Preventivo")
                    )

                    items(tareas) { (tarea, asignado) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(tarea, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(asignado, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary))
                            }
                        }
                    }
                }

                // Default Bitácoras / Logs List
                if (selectedSubTab == "BITACORAS" || selectedSubTab == "TURNOS" || selectedSubTab == "MANTENIMIENTO") {
                    item {
                        SectionHeader(
                            title = "Bitácora Cronológica de Guardia",
                            subtitle = "Rondines nocturnos, relevos y novedades"
                        )
                    }

                    val filteredLogs = operationLogs.filter { log ->
                        val matchesTab = when (selectedSubTab) {
                            "BITACORAS" -> true
                            "TURNOS" -> log.logType == "TURNO" || log.logType == "BITACORA_GUARDIA"
                            "MANTENIMIENTO" -> log.logType == "MANTENIMIENTO"
                            else -> true
                        }
                        val matchesSearch = searchQuery.isBlank() ||
                                log.title.contains(searchQuery, ignoreCase = true) ||
                                log.staffName.contains(searchQuery, ignoreCase = true) ||
                                log.details.contains(searchQuery, ignoreCase = true)

                        matchesTab && matchesSearch
                    }

                    items(filteredLogs, key = { it.id }) { log ->
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
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = log.logType,
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = log.timestamp,
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = log.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Responsable: ${log.staffName} • Turno: ${log.shift}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.details,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var logType by remember { mutableStateOf("BITACORA_GUARDIA") }
            var title by remember { mutableStateOf("") }
            var staffName by remember { mutableStateOf("Roberto Silva") }
            var shift by remember { mutableStateOf("NOCTURNO") }
            var details by remember { mutableStateOf("") }

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("Registrar Evento en Bitácora", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    item {
                        Text("Tipo de Registro:", style = MaterialTheme.typography.bodySmall)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("BITACORA_GUARDIA", "TURNO", "CONTROL_LLAVES", "MANTENIMIENTO")) { tp ->
                                FilterChip(
                                    selected = logType == tp,
                                    onClick = { logType = tp },
                                    label = { Text(tp, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título / Asunto") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = staffName, onValueChange = { staffName = it }, label = { Text("Responsable") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = shift, onValueChange = { shift = it }, label = { Text("Turno") }, modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        OutlinedTextField(value = details, onValueChange = { details = it }, label = { Text("Detalle de Novedades") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                if (title.isNotBlank()) {
                                    val log = OperationLogEntity(
                                        logType = logType,
                                        title = title.trim(),
                                        staffName = staffName.trim(),
                                        timestamp = "2024-09-28 09:00 AM",
                                        shift = shift.trim(),
                                        details = details.trim(),
                                        status = "OK"
                                    )
                                    onAddLog(log)
                                    showAddDialog = false
                                }
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}
