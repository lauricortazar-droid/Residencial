package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AgendaEventEntity
import com.example.data.model.ResidentEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun FamiliasScreen(
    residents: List<ResidentEntity>,
    agendaEvents: List<AgendaEventEntity>,
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onScheduleVisit: (AgendaEventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showScheduleDialog by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "FAMILIARES" to "Familiares",
        "VISITAS" to "Visitas",
        "REUNIONES" to "Reuniones",
        "COMUNICACIONES" to "Comunicaciones",
        "PORTAL" to "Portal Familiar"
    )

    Scaffold(
        floatingActionButton = {
            if (selectedSubTab == "VISITAS" || selectedSubTab == "REUNIONES") {
                ExtendedFloatingActionButton(
                    onClick = { showScheduleDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Agendar Visita/Junta") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("schedule_family_event_fab")
                )
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
                placeholder = "Buscar familiar, residente o reunión..."
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
                when (selectedSubTab) {
                    "FAMILIARES" -> {
                        item {
                            SectionHeader(
                                title = "Directorio de Tutores y Familiares",
                                subtitle = "Contactos autorizados por expediente"
                            )
                        }

                        val filteredResidents = residents.filter {
                            searchQuery.isBlank() ||
                                    it.tutorName.contains(searchQuery, ignoreCase = true) ||
                                    it.fullName.contains(searchQuery, ignoreCase = true)
                        }

                        items(filteredResidents, key = { it.id }) { res ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
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
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = res.tutorName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Parentesco: ${res.tutorRelationship} de ${res.fullName}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                        )
                                        Text(
                                            text = "Tel: ${res.tutorPhone} • Estado: ${res.status}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${res.tutorPhone}"))
                                            context.startActivity(intent)
                                        }) {
                                            Icon(
                                                Icons.Default.Phone,
                                                contentDescription = "Llamar",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = {
                                            val cleanPhone = res.tutorPhone.replace(Regex("[^0+0-9]"), "")
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
                                            context.startActivity(intent)
                                        }) {
                                            Icon(
                                                Icons.Default.Chat,
                                                contentDescription = "WhatsApp",
                                                tint = StatusActiveGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "VISITAS" -> {
                        item {
                            SectionHeader(
                                title = "Visitas Familiares Presenciales",
                                subtitle = "Domingos de 10:00 a 14:00 hrs para residentes en Fase 2+"
                            )
                        }

                        val visitEvents = agendaEvents.filter { it.type == "VISITA_FAMILIAR" }
                        if (visitEvents.isEmpty()) {
                            item {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Text("No hay visitas programadas en el calendario.", modifier = Modifier.padding(14.dp))
                                }
                            }
                        } else {
                            items(visitEvents, key = { it.id }) { visit ->
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
                                                color = StatusActiveGreen.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "AUTORIZADA",
                                                    color = StatusActiveGreen,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Text(
                                                text = "${visit.date} • ${visit.time}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = visit.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Ubicación: ${visit.location} | Participantes: ${visit.personInvolved}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = visit.description,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "REUNIONES" -> {
                        item {
                            SectionHeader(
                                title = "Juntas de Familias y Escuela para Padres",
                                subtitle = "Sesiones terapéuticas grupales quincenales"
                            )
                        }

                        val reunionEvents = agendaEvents.filter { it.type == "REUNION" }
                        items(reunionEvents, key = { it.id }) { reunion ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = reunion.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Fecha: ${reunion.date} • Horario: ${reunion.time}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                                    )
                                    Text(
                                        text = "Lugar: ${reunion.location}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = reunion.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    "COMUNICACIONES" -> {
                        item {
                            SectionHeader(
                                title = "Bitácora de Comunicaciones Telefónicas",
                                subtitle = "Llamadas autorizadas y reporte a familiares"
                            )
                        }

                        val sampleCalls = listOf(
                            Triple("Rodrigo Morales", "Elena Alarcón (Madre)", "Llamada de 10 min. Reporta ánimo positivo y sin ansiedad. Consejero presente."),
                            Triple("Alejandro Herrera", "Maricela Gómez (Esposa)", "Llamada semanal programada. Se coordina visita dominical."),
                            Triple("Valeria Ríos", "Dr. Javier Ríos (Padre)", "Videollamada terapéutica guiada por Psic. Flores.")
                        )

                        items(sampleCalls) { (res, tutor, notes) ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = res, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Surface(
                                            color = StatusActiveGreen.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Completada",
                                                color = StatusActiveGreen,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(text = "Contacto: $tutor", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = notes, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                        }
                    }

                    "PORTAL" -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "VISTA PREVIA DEL PORTAL FAMILIAR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Así ve la familia el reporte de su residente en la app web / móvil:",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    val firstResident = residents.firstOrNull() ?: return@Column
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Text(
                                                text = "Residente: ${firstResident.fullName}",
                                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "Fase Actual: ${firstResident.currentPhase}",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Nota del Terapeuta de la Semana:",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = "\"Muestra excelente compromiso en la dinámica grupal y talleres ocupacionales. Aprobado para visita dominical familiar.\"",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Estado de Cuenta: Mensualidad al corriente ($0.00)",
                                                style = MaterialTheme.typography.labelSmall.copy(color = StatusActiveGreen, fontWeight = FontWeight.Bold)
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
    }

    if (showScheduleDialog) {
        Dialog(onDismissRequest = { showScheduleDialog = false }) {
            var title by remember { mutableStateOf("") }
            var date by remember { mutableStateOf("2024-10-06") }
            var time by remember { mutableStateOf("10:00 AM - 02:00 PM") }
            var location by remember { mutableStateOf("Jardín Central") }
            var description by remember { mutableStateOf("") }
            var person by remember { mutableStateOf("Familias autorizadas") }

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Agendar Evento / Visita", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título de la Visita o Junta") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Horario") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lugar / Sala") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Observaciones / Requisitos") }, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showScheduleDialog = false }) { Text("Cancelar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (title.isNotBlank()) {
                                onScheduleVisit(
                                    AgendaEventEntity(
                                        title = title,
                                        type = "VISITA_FAMILIAR",
                                        date = date,
                                        time = time,
                                        location = location,
                                        description = description,
                                        personInvolved = person
                                    )
                                )
                                showScheduleDialog = false
                            }
                        }) {
                            Text("Agendar")
                        }
                    }
                }
            }
        }
    }
}
