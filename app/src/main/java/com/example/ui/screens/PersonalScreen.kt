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
import com.example.data.model.StaffMemberEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun PersonalScreen(
    staffList: List<StaffMemberEntity>,
    onAddStaff: (StaffMemberEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "TODOS" to "Todos",
        "SERVIDOR" to "Servidores",
        "PROFESIONAL" to "Profesionales",
        "EMPLEADO" to "Empleados",
        "CAPACITACIONES" to "Capacitaciones",
        "HORARIOS" to "Horarios y Turnos"
    )

    val trainings = listOf(
        Triple("NOM-028-SSA2-2009 Para la prevención, tratamiento y control de las adicciones", "Obligatorio Salubridad", "100% Personal Acreditado"),
        Triple("Soporte Vital Básico, Primeros Auxilios y RCP", "Cruz Roja Mexicana", "Vigente 2024-2025"),
        Triple("Contención Emocional y Verbal en Crisis de Abstinencia", "Consejo Estatal Contra las Adicciones", "Semestral"),
        Triple("Derechos Humanos y Trato Digno en Residencias Terapéuticas", "CNDH", "Acreditación Total")
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Nuevo Colaborador") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_staff_fab")
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
                placeholder = "Buscar por nombre, cargo o turno..."
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
                        onClick = { selectedSubTab = key },
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
                if (selectedSubTab == "CAPACITACIONES") {
                    item {
                        SectionHeader(
                            title = "Registro de Capacitaciones Oficiales",
                            subtitle = "Cumplimiento normativo y certificaciones del equipo"
                        )
                    }

                    items(trainings) { (nombre, emisor, estatus) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = StatusActiveGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = estatus,
                                            color = StatusActiveGreen,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(nombre, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Entidad Evaluadora: $emisor", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary))
                            }
                        }
                    }
                }

                if (selectedSubTab == "HORARIOS") {
                    item {
                        SectionHeader(
                            title = "Cuadrante de Turnos y Guardias",
                            subtitle = "Cobertura 24 horas los 365 días"
                        )
                    }

                    val turnos = listOf(
                        Pair("Turno Matutino (07:00 - 15:30)", "Médico General, Psicología, Jefa de Cocina, Limpieza"),
                        Pair("Turno Vespertino (14:30 - 22:00)", "Psicólogo de guardia, Consejeros de adicciones, Talleres"),
                        Pair("Turno Nocturno (21:45 - 07:15)", "Enfermero de noche, Padrino de guardia, Custodio"),
                        Pair("Guardia Residencial Continua", "Director Clínico y Consejero Titular")
                    )

                    items(turnos) { (turno, personal) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(turno, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(personal, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }

                if (selectedSubTab != "CAPACITACIONES" && selectedSubTab != "HORARIOS") {
                    item {
                        SectionHeader(
                            title = "Equipo de Trabajo",
                            subtitle = "Colaboradores activos en el centro"
                        )
                    }

                    val filteredStaff = staffList.filter { st ->
                        val matchesTab = when (selectedSubTab) {
                            "TODOS" -> true
                            else -> st.category.equals(selectedSubTab, ignoreCase = true)
                        }
                        val matchesSearch = searchQuery.isBlank() ||
                                st.fullName.contains(searchQuery, ignoreCase = true) ||
                                st.role.contains(searchQuery, ignoreCase = true) ||
                                st.shift.contains(searchQuery, ignoreCase = true)

                        matchesTab && matchesSearch
                    }

                    items(filteredStaff, key = { it.id }) { staff ->
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
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (staff.category) {
                                            "PROFESIONAL" -> Icons.Default.MedicalServices
                                            "SERVIDOR" -> Icons.Default.VolunteerActivism
                                            else -> Icons.Default.Engineering
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = staff.fullName,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = staff.role,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                    Text(
                                        text = "Turno: ${staff.shift}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                    if (staff.cedulaProf.isNotBlank()) {
                                        Text(
                                            text = "Registro: ${staff.cedulaProf}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${staff.phone}"))
                                    context.startActivity(intent)
                                }) {
                                    Icon(
                                        Icons.Default.Phone,
                                        contentDescription = "Llamar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
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
            var fullName by remember { mutableStateOf("") }
            var role by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("PROFESIONAL") }
            var phone by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var shift by remember { mutableStateOf("Matutino 08:00 - 15:00") }
            var cedula by remember { mutableStateOf("") }

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("Nuevo Colaborador del Centro", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    item {
                        Text("Tipo de Colaborador:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("SERVIDOR", "PROFESIONAL", "EMPLEADO").forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    item {
                        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Puesto / Función") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = shift, onValueChange = { shift = it }, label = { Text("Turno Asignado") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cédula / Cert.") }, modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                if (fullName.isNotBlank()) {
                                    val staff = StaffMemberEntity(
                                        fullName = fullName.trim(),
                                        role = role.trim(),
                                        category = category,
                                        phone = phone.trim(),
                                        email = email.trim(),
                                        shift = shift.trim(),
                                        cedulaProf = cedula.trim()
                                    )
                                    onAddStaff(staff)
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
