package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.export.ExpedienteReportExporter
import com.example.data.export.ExportFormat
import com.example.data.model.ExpedienteEntity
import com.example.ui.theme.StatusActiveGreen

/**
 * Diálogo modal para la exportación de expedientes clínicos en formatos CSV y JSON.
 */
@Composable
fun ExportExpedientesDialog(
    expedientes: List<ExpedienteEntity>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf(ExportFormat.CSV) }
    var showPreview by remember { mutableStateOf(false) }

    val exportedContent = remember(selectedFormat, expedientes) {
        when (selectedFormat) {
            ExportFormat.CSV -> ExpedienteReportExporter.exportToCsv(expedientes)
            ExportFormat.JSON -> ExpedienteReportExporter.exportToJson(expedientes, prettyPrint = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("export_expedientes_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Exportar Reporte Clínico",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "${expedientes.size} expedientes listos • NOM-028-SSA2",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Formato
                Text(
                    text = "SELECCIONA EL FORMATO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FormatSelectCard(
                        title = "CSV (Excel)",
                        subtitle = "Hojas de cálculo",
                        icon = Icons.Default.TableChart,
                        isSelected = selectedFormat == ExportFormat.CSV,
                        onClick = { selectedFormat = ExportFormat.CSV },
                        modifier = Modifier.weight(1f).testTag("select_format_csv")
                    )

                    FormatSelectCard(
                        title = "JSON",
                        subtitle = "Reporte digital",
                        icon = Icons.Default.Code,
                        isSelected = selectedFormat == ExportFormat.JSON,
                        onClick = { selectedFormat = ExportFormat.JSON },
                        modifier = Modifier.weight(1f).testTag("select_format_json")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Vista previa colapsable
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPreview = !showPreview }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (showPreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (showPreview) "Ocultar vista previa" else "Ver vista previa del archivo",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Icon(
                            imageVector = if (showPreview) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (showPreview) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                    ) {
                        Text(
                            text = exportedContent.take(800) + if (exportedContent.length > 800) "\n... [más registros]" else "",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Acciones de Exportación
                Button(
                    onClick = {
                        try {
                            val file = ExpedienteReportExporter.createExportFile(context, expedientes, selectedFormat)
                            val shareIntent = ExpedienteReportExporter.createShareIntent(context, file, selectedFormat)
                            context.startActivity(shareIntent)
                            Toast.makeText(context, "Archivo ${selectedFormat.extension.uppercase()} generado con éxito", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al generar archivo: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_export_share_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir / Guardar Archivo")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        ExpedienteReportExporter.copyToClipboard(context, exportedContent, "Reporte ${selectedFormat.name}")
                        Toast.makeText(context, "Contenido ${selectedFormat.name} copiado al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("copy_export_clipboard_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar al Portapapeles")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
private fun FormatSelectCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = modifier
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
