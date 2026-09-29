package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ResidentEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.navigation.MainSection
import com.example.ui.theme.*

@Composable
fun MasOpcionesScreen(
    currentSection: MainSection,
    residents: List<ResidentEntity>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            when (currentSection) {
                MainSection.REPORTES -> {
                    item {
                        SectionHeader(
                            title = "Reportes Clínicos y Estadísticos",
                            subtitle = "Indicadores clave de rendimiento (KPIs) del centro"
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Tasa de Retención",
                                value = "88.5%",
                                subtitle = "Completación de programa",
                                icon = Icons.Default.Analytics,
                                iconColor = StatusActiveGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Estancia Promedio",
                                value = "165 días",
                                subtitle = "Objetivo: 180 días",
                                icon = Icons.Default.Timelapse,
                                iconColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Sustancias de Mayor Prevalencia en Admisión",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                val substances = listOf(
                                    Pair("Metanfetamina / Cristal", 0.42f),
                                    Pair("Alcoholismo Crónico", 0.28f),
                                    Pair("Cannabis y Benzodiacepinas", 0.18f),
                                    Pair("Poliadicciones / Opiáceos", 0.12f)
                                )

                                substances.forEach { (nombre, pct) ->
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(nombre, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                            Text("${(pct * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { pct },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                MainSection.GOOGLE_WORKSPACE -> {
                    item {
                        SectionHeader(
                            title = "Integración con Google Workspace",
                            subtitle = "Conexión en la nube con Google Drive, Calendar y Gmail"
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                WorkspaceIntegrationRow(
                                    name = "Google Drive (Expedientes Digitales)",
                                    status = "Conectado",
                                    account = "expedientes.senda@gmail.com",
                                    icon = Icons.Default.CloudQueue,
                                    color = StatusActiveGreen
                                )
                                HorizontalDivider()
                                WorkspaceIntegrationRow(
                                    name = "Google Calendar (Agenda Institucional)",
                                    status = "Sincronizado",
                                    account = "citas.senda@gmail.com",
                                    icon = Icons.Default.CalendarMonth,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider()
                                WorkspaceIntegrationRow(
                                    name = "Gmail (Notificaciones y Avisos)",
                                    status = "Activo",
                                    account = "avisos@sendaclinica.org",
                                    icon = Icons.Default.Mail,
                                    color = AlertCriticalRed
                                )
                            }
                        }
                    }
                }

                MainSection.CONFIGURACION -> {
                    item {
                        SectionHeader(
                            title = "Configuración Institucional",
                            subtitle = "Parámetros operativos y normativos del centro"
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Nombre del Centro: Comunidad Terapéutica Senda Residencial A.C.", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Licencia Sanitaria COFEPRIS: 24-09-SSA-012984", style = MaterialTheme.typography.bodySmall)
                                Text("Acreditación CONADIC: Centro Residencial Modelo Mixto No. 448", style = MaterialTheme.typography.bodySmall)
                                Text("Capacidad Máxima Instalada: 35 camas", style = MaterialTheme.typography.bodySmall)
                                Text("Horario de Visita Familiar: Domingos 10:00 - 14:00 hrs", style = MaterialTheme.typography.bodySmall)
                                Text("Cuota Residencial Base: $8,500.00 MXN / mes", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Configuración guardada exitosamente.", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Guardar Parámetros")
                                }
                            }
                        }
                    }
                }

                MainSection.ADMINISTRACION -> {
                    item {
                        SectionHeader(
                            title = "Administración y Seguridad del Sistema",
                            subtitle = "Roles de usuario y respaldo de base de datos"
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Roles de Acceso Configurados:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("• Director General (Acceso Total y Financiero)", style = MaterialTheme.typography.bodySmall)
                                Text("• Médicos y Psiquiatras (Historial Clínico, Fármacos e Incidentes)", style = MaterialTheme.typography.bodySmall)
                                Text("• Psicólogos y Consejeros (Notas de Evolución y Fases)", style = MaterialTheme.typography.bodySmall)
                                Text("• Administración y Caja (Cobros, Recibos y Facturación)", style = MaterialTheme.typography.bodySmall)
                                Text("• Guardia de Turno (Bitácoras, Llaves y Novedades)", style = MaterialTheme.typography.bodySmall)

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Respaldo de Base de Datos Local:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Text("Almacenamiento seguro SQLite con Room Database y exportación cifrada.", style = MaterialTheme.typography.bodySmall)
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "Respaldo local generado con éxito.", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Backup, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generar Respaldo Ahora")
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun WorkspaceIntegrationRow(
    name: String,
    status: String,
    account: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(account, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Surface(
            color = color.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = status,
                color = color,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
