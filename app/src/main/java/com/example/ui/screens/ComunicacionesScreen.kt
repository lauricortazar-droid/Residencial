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
import com.example.data.model.ResidentEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun ComunicacionesScreen(
    residents: List<ResidentEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedSubTab by remember { mutableStateOf("WHATSAPP") }
    var selectedResident by remember { mutableStateOf(residents.firstOrNull()) }

    val subTabs = listOf(
        "WHATSAPP" to "WhatsApp",
        "CORREO" to "Correo Electrónico",
        "AVISOS" to "Avisos Generales",
        "PLANTILLAS" to "Plantillas",
        "HISTORIAL" to "Historial de Mensajes"
    )

    val templates = listOf(
        Triple(
            "Reporte Semanal de Bienestar",
            "Hola estimado tutor. Le saludamos del Centro Residencial Senda para informarle que su familiar se encuentra con excelente ánimo, participando activamente en sus terapias y talleres. ¡Seguimos adelante un día a la vez!",
            "Semanal"
        ),
        Triple(
            "Confirmación de Visita Dominical",
            "Estimada familia: Se encuentra confirmada su visita para este domingo en horario de 10:00 a 14:00 hrs. Recuerde traer ropa cómoda y evitar alimentos enlatados o bebidas no autorizadas.",
            "Visitas"
        ),
        Triple(
            "Recordatorio de Cuota Mensual",
            "Apreciable familia: Le enviamos un cordial recordatorio sobre la cuota mensual de mantenimiento residencial correspondiente a este periodo. Agradecemos su puntual colaboración para la recuperación de su familiar.",
            "Finanzas"
        ),
        Triple(
            "Convocatoria Junta de Padres y Familias",
            "Les invitamos cordialmente a nuestra próxima Escuela para Familias este sábado a las 11:00 AM en el auditorio. Su presencia es fundamental en el proceso de sanación del sistema familiar.",
            "Terapéutico"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
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
            if (selectedSubTab == "WHATSAPP" || selectedSubTab == "PLANTILLAS") {
                item {
                    SectionHeader(
                        title = "Centro de Mensajería WhatsApp",
                        subtitle = "Envío instantáneo de reportes a familiares"
                    )
                }

                item {
                    Text(
                        text = "Selecciona el tutor de destino:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(residents) { res ->
                            FilterChip(
                                selected = selectedResident?.id == res.id,
                                onClick = { selectedResident = res },
                                label = { Text("${res.tutorName} (${res.fullName.split(" ").first()})", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                items(templates) { (titulo, mensaje, categoria) ->
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
                                Text(titulo, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Surface(
                                    color = StatusActiveGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = categoria,
                                        color = StatusActiveGreen,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(mensaje, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        val phone = selectedResident?.tutorPhone?.replace(Regex("[^0-9]"), "") ?: ""
                                        val url = "https://wa.me/$phone?text=${Uri.encode(mensaje)}"
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusActiveGreen)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enviar por WhatsApp", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (selectedSubTab == "CORREO") {
                item {
                    SectionHeader(
                        title = "Correo Institucional Senda",
                        subtitle = "Emisión de circulares formales y estados de cuenta"
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Para: Comunidad de Familias y Tutores", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("De: direccion@sendaclinica.org", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Asunto: Resumen de Actividades Mensuales y Escuela para Padres",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Estimadas familias: Les compartimos el cronograma de actividades terapéuticas, avances grupales de la comunidad y recordatorio de fechas de visita presencial...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_SUBJECT, "Boletín Informativo Centro Senda")
                                }
                                context.startActivity(intent)
                            }) {
                                Icon(Icons.Default.Email, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir en Correo")
                            }
                        }
                    }
                }
            }

            if (selectedSubTab == "AVISOS" || selectedSubTab == "HISTORIAL") {
                item {
                    SectionHeader(
                        title = "Historial de Avisos a la Comunidad",
                        subtitle = "Comunicados oficiales emitidos"
                    )
                }

                val avisos = listOf(
                    Pair("Aviso de Supervisión Sanitaria Programada", "Se informa que este próximo jueves 10 de octubre el centro recibirá la visita anual de verificación sanitaria."),
                    Pair("Protocolo de Visitas Familiares Temporada de Otoño", "Por clima fresco, las visitas se realizarán en el salón múltiple techado de 10:00 a 14:00 hrs."),
                    Pair("Taller Especial de Nutrición y Deshabituación", "Agradecemos la entusiasta participación de las familias en la sesión del fin de semana.")
                )

                items(avisos) { (titulo, desc) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(titulo, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }
                }
            }
        }
    }
}
