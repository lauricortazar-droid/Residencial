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
import com.example.data.model.AgendaEventEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun AgendaScreen(
    agendaEvents: List<AgendaEventEntity>,
    onAddAgendaEvent: (AgendaEventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("CALENDARIO") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "CALENDARIO" to "Calendario",
        "CITAS" to "Citas Médicas",
        "VISITAS" to "Visitas",
        "EVENTOS" to "Eventos",
        "ACTIVIDADES" to "Actividades Fijas",
        "GOOGLE_CALENDAR" to "Google Calendar"
    )

    val dailySchedule = listOf(
        Pair("07:00 AM", "Despertar, aseo personal y tendido de camas"),
        Pair("08:00 AM", "Meditación matutina y desayuno balanceado"),
        Pair("09:30 AM", "Junta de Espiritualidad y 12 Pasos de Recuperación"),
        Pair("11:30 AM", "Terapia Cognitivo-Conductual Grupal (Psicología)"),
        Pair("01:30 PM", "Comida comunitaria y convivencia"),
        Pair("03:00 PM", "Acondicionamiento físico, yoga y deporte"),
        Pair("05:00 PM", "Taller de Prevención de Recaídas y Proyecto de Vida"),
        Pair("07:00 PM", "Cena ligera"),
        Pair("08:30 PM", "Junta de sentimientos y evaluación del día"),
        Pair("10:00 PM", "Toque de silencio y descanso nocturno")
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva Cita / Evento") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_agenda_event_fab")
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
                placeholder = "Buscar cita, doctor o evento..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subTabs) { (key, label) ->
                    val isSelected = selectedTab == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = key },
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
                if (selectedTab == "GOOGLE_CALENDAR") {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudSync,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Sincronización con Google Calendar",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Calendario: senda.residencial@gmail.com",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Estado: Sincronizado hace 5 minutos. Todas las citas médicas y visitas familiares se reflejan en tiempo real.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { /* Force sync */ },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Forzar Sincronización")
                                }
                            }
                        }
                    }
                }

                if (selectedTab == "ACTIVIDADES" || selectedTab == "CALENDARIO") {
                    item {
                        SectionHeader(
                            title = "Cronograma Diario Fijo del Centro",
                            subtitle = "Estructura de recuperación y disciplina"
                        )
                    }

                    items(dailySchedule) { (hora, actividad) ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = hora,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = actividad,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }

                if (selectedTab != "ACTIVIDADES") {
                    item {
                        SectionHeader(
                            title = "Eventos y Citas Agendadas",
                            subtitle = "Próximos compromisos programados"
                        )
                    }

                    val filteredEvents = agendaEvents.filter { event ->
                        val matchesTab = when (selectedTab) {
                            "CALENDARIO", "GOOGLE_CALENDAR" -> true
                            "CITAS" -> event.type == "CITA_MEDICA"
                            "VISITAS" -> event.type == "VISITA_FAMILIAR"
                            "EVENTOS" -> event.type == "EVENTO" || event.type == "REUNION"
                            else -> true
                        }
                        val matchesSearch = searchQuery.isBlank() ||
                                event.title.contains(searchQuery, ignoreCase = true) ||
                                event.location.contains(searchQuery, ignoreCase = true) ||
                                event.personInvolved.contains(searchQuery, ignoreCase = true)

                        matchesTab && matchesSearch
                    }

                    if (filteredEvents.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text("No hay eventos que coincidan.", modifier = Modifier.padding(14.dp))
                            }
                        }
                    } else {
                        items(filteredEvents, key = { it.id }) { ev ->
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
                                                text = ev.type,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = "${ev.date} • ${ev.time}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = ev.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Lugar: ${ev.location} | Involucrados: ${ev.personInvolved}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (ev.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = ev.description,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
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

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var title by remember { mutableStateOf("") }
            var type by remember { mutableStateOf("CITA_MEDICA") }
            var date by remember { mutableStateOf("2024-10-05") }
            var time by remember { mutableStateOf("11:00 AM") }
            var location by remember { mutableStateOf("Consultorio Médico") }
            var person by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("Nueva Cita / Evento de Agenda", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    item {
                        Text("Tipo de Evento:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("CITA_MEDICA", "VISITA_FAMILIAR", "REUNION", "EVENTO")) { tp ->
                                FilterChip(
                                    selected = type == tp,
                                    onClick = { type = tp },
                                    label = { Text(tp, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título del Evento") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Hora") }, modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lugar / Sala") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = person, onValueChange = { person = it }, label = { Text("Personas Involucradas / Médico") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción / Notas") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                if (title.isNotBlank()) {
                                    val ev = AgendaEventEntity(
                                        title = title.trim(),
                                        type = type,
                                        date = date.trim(),
                                        time = time.trim(),
                                        location = location.trim(),
                                        description = desc.trim(),
                                        personInvolved = person.trim()
                                    )
                                    onAddAgendaEvent(ev)
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
