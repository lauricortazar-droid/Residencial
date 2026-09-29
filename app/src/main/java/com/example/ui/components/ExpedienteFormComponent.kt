package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.repository.ClinicalRepository
import com.example.data.repository.ISendaRepository
import com.example.ui.theme.StatusActiveGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Componente de formulario en Jetpack Compose para capturar y validar nuevos expedientes médicos.
 * Se integra directamente con el Repository para persistir los datos en Room Database.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpedienteFormComponent(
    initialExpediente: ExpedienteEntity? = null,
    residents: List<ResidentEntity> = emptyList(),
    onSaveSuccess: (ExpedienteEntity) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Campos del Formulario
    var folio by remember {
        mutableStateOf(initialExpediente?.folioExpediente ?: "EXP-SND-2024-${(100..999).random()}")
    }
    var selectedResidentId by remember {
        mutableStateOf(initialExpediente?.residentId ?: residents.firstOrNull()?.id ?: 1)
    }
    var residentName by remember {
        mutableStateOf(initialExpediente?.residentName ?: residents.firstOrNull()?.fullName ?: "")
    }
    var fechaApertura by remember {
        mutableStateOf(initialExpediente?.fechaApertura ?: "2024-09-29")
    }
    var tipoIngreso by remember {
        mutableStateOf(initialExpediente?.tipoIngreso ?: "VOLUNTARIO")
    }
    var diagnosticoPrincipal by remember {
        mutableStateOf(initialExpediente?.diagnosticoPrincipal ?: "")
    }
    var antecedentesPatologicos by remember {
        mutableStateOf(initialExpediente?.antecedentesPatologicos ?: "Sin antecedentes patológicos crónicos reportados")
    }
    var sustanciaDeImpacto by remember {
        mutableStateOf(initialExpediente?.sustanciaDeImpacto ?: "")
    }
    var tiempoDeConsumo by remember {
        mutableStateOf(initialExpediente?.tiempoDeConsumo ?: "2 años")
    }
    var planTratamiento by remember {
        mutableStateOf(initialExpediente?.planTratamiento ?: "Programa Residencial de 6 meses - Modelo Comunidad Terapéutica NOM-028")
    }
    var medicoTratante by remember {
        mutableStateOf(initialExpediente?.medicoTratante ?: "Dr. Armando Valdés Soto")
    }
    var psicologoResponsable by remember {
        mutableStateOf(initialExpediente?.psicologoResponsable ?: "Psic. Mariana Flores Peñaloza")
    }
    var estatusExpediente by remember {
        mutableStateOf(initialExpediente?.estatusExpediente ?: "ACTIVO")
    }
    var consentimientoFirmado by remember {
        mutableStateOf(initialExpediente?.consentimientoFirmado ?: true)
    }
    var notasIngreso by remember {
        mutableStateOf(initialExpediente?.notasIngreso ?: "Apertura de expediente formal conforme a la Norma Oficial Mexicana NOM-028-SSA2.")
    }

    // Estados de Validación
    var folioError by remember { mutableStateOf(false) }
    var residentError by remember { mutableStateOf(false) }
    var diagnosticoError by remember { mutableStateOf(false) }
    var sustanciaError by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Catálogos rápidos
    val diagnosticosSugeridos = listOf(
        "F10.2 Síndrome de dependencia al alcohol",
        "F15.2 Dependencia a metanfetaminas (Cristal)",
        "F12.2 Consumo perjudicial de cannabis",
        "F13.2 Dependencia a sedantes / benzodiacepinas",
        "F19.2 Dependencia a múltiples sustancias (Policonsumo)"
    )

    val sustanciasSugeridas = listOf(
        "Alcohol",
        "Metanfetamina / Cristal",
        "Cannabis",
        "Cocaína",
        "Benzodiacepinas / Sedantes",
        "Opioides"
    )

    val planesSugeridos = listOf(
        "Programa Residencial de 6 meses - Comunidad Terapéutica",
        "Desintoxicación y Estabilización Médica Intensiva (90 días)",
        "Desescalamiento Farmacológico y Psicoterapia Cognitiva",
        "Atención Ambulatoria y Prevención de Recaídas"
    )

    fun validateAndSave() {
        folioError = folio.isBlank()
        residentError = residentName.isBlank()
        diagnosticoError = diagnosticoPrincipal.isBlank()
        sustanciaError = sustanciaDeImpacto.isBlank()

        if (!folioError && !residentError && !diagnosticoError && !sustanciaError) {
            isSaving = true
            val expedienteToPersist = ExpedienteEntity(
                id = initialExpediente?.id ?: 0,
                folioExpediente = folio.trim(),
                residentId = selectedResidentId,
                residentName = residentName.trim(),
                fechaApertura = fechaApertura,
                tipoIngreso = tipoIngreso,
                diagnosticoPrincipal = diagnosticoPrincipal.trim(),
                antecedentesPatologicos = antecedentesPatologicos.trim(),
                sustanciaDeImpacto = sustanciaDeImpacto.trim(),
                tiempoDeConsumo = tiempoDeConsumo.trim(),
                planTratamiento = planTratamiento.trim(),
                medicoTratante = medicoTratante.trim(),
                psicologoResponsable = psicologoResponsable.trim(),
                estatusExpediente = estatusExpediente,
                consentimientoFirmado = consentimientoFirmado,
                notasIngreso = notasIngreso.trim()
            )
            onSaveSuccess(expedienteToPersist)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            // SECCIÓN 1: DATOS ADMINISTRATIVOS
            item {
                FormSectionCard(
                    title = "1. Identificación y Residente",
                    icon = Icons.Default.Badge
                ) {
                    // Folio con botón de generación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = folio,
                            onValueChange = {
                                folio = it
                                folioError = false
                            },
                            label = { Text("Folio Único Oficial *") },
                            isError = folioError,
                            supportingText = if (folioError) {
                                { Text("El folio es obligatorio", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("expediente_input_folio")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalIconButton(
                            onClick = {
                                folio = "EXP-SND-2024-${(100..999).random()}"
                                folioError = false
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(Icons.Default.Autorenew, contentDescription = "Autogenerar folio")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selector rápido de residentes registrados en Room
                    if (residents.isNotEmpty()) {
                        Text(
                            text = "Vincular a Residente Registrado:",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(residents) { res ->
                                val isSelected = residentName == res.fullName
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        residentName = res.fullName
                                        selectedResidentId = res.id
                                        sustanciaDeImpacto = res.primaryReason
                                        residentError = false
                                    },
                                    label = { Text(res.fullName.split(" ").take(2).joinToString(" ")) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = residentName,
                        onValueChange = {
                            residentName = it
                            residentError = false
                        },
                        label = { Text("Nombre Completo del Residente *") },
                        isError = residentError,
                        supportingText = if (residentError) {
                            { Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expediente_input_resident_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = fechaApertura,
                            onValueChange = { fechaApertura = it },
                            label = { Text("Fecha Apertura") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            modifier = Modifier.weight(1f)
                        )

                        // Selector de Tipo de Ingreso
                        var expandedTipo by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedTipo,
                            onExpandedChange = { expandedTipo = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = tipoIngreso,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo Ingreso") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTipo,
                                onDismissRequest = { expandedTipo = false }
                            ) {
                                listOf("VOLUNTARIO", "INVOLUNTARIO_FAMILIAR", "OBLIGATORIO_JUDICIAL").forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            tipoIngreso = opt
                                            expandedTipo = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECCIÓN 2: DIAGNÓSTICO CLÍNICO Y TOXICOLOGÍA
            item {
                FormSectionCard(
                    title = "2. Diagnóstico Clínico y Toxicología",
                    icon = Icons.Default.HealthAndSafety
                ) {
                    OutlinedTextField(
                        value = diagnosticoPrincipal,
                        onValueChange = {
                            diagnosticoPrincipal = it
                            diagnosticoError = false
                        },
                        label = { Text("Diagnóstico Principal (CIE-11 / DSM-5) *") },
                        placeholder = { Text("Ej. F10.2 Dependencia al alcohol") },
                        isError = diagnosticoError,
                        supportingText = if (diagnosticoError) {
                            { Text("El diagnóstico médico es obligatorio", color = MaterialTheme.colorScheme.error) }
                        } else null,
                        leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expediente_input_diagnostico")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chips de diagnósticos sugeridos
                    Text("Sugerencias CIE:", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(diagnosticosSugeridos) { diag ->
                            SuggestionChip(
                                onClick = {
                                    diagnosticoPrincipal = diag
                                    diagnosticoError = false
                                },
                                label = { Text(diag.split(" ").take(3).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = sustanciaDeImpacto,
                            onValueChange = {
                                sustanciaDeImpacto = it
                                sustanciaError = false
                            },
                            label = { Text("Sustancia Impacto *") },
                            placeholder = { Text("Cristal, Alcohol...") },
                            isError = sustanciaError,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("expediente_input_sustancia")
                        )

                        OutlinedTextField(
                            value = tiempoDeConsumo,
                            onValueChange = { tiempoDeConsumo = it },
                            label = { Text("Tiempo Consumo") },
                            placeholder = { Text("2 años...") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chips de sustancias sugeridas
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(sustanciasSugeridas) { sust ->
                            AssistChip(
                                onClick = {
                                    sustanciaDeImpacto = sust
                                    sustanciaError = false
                                },
                                label = { Text(sust, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = antecedentesPatologicos,
                        onValueChange = { antecedentesPatologicos = it },
                        label = { Text("Antecedentes Médicos / Alergias") },
                        placeholder = { Text("Alergias, hipertensión, diabetes, cirugías...") },
                        leadingIcon = { Icon(Icons.Default.Healing, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // SECCIÓN 3: PLAN TERAPÉUTICO Y EQUIPO PROFESIONAL
            item {
                FormSectionCard(
                    title = "3. Plan Terapéutico y Asignación",
                    icon = Icons.Default.Psychology
                ) {
                    OutlinedTextField(
                        value = planTratamiento,
                        onValueChange = { planTratamiento = it },
                        label = { Text("Plan Integral de Tratamiento") },
                        minLines = 2,
                        leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expediente_input_plan")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Planes sugeridos
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(planesSugeridos) { plan ->
                            SuggestionChip(
                                onClick = { planTratamiento = plan },
                                label = { Text(plan.split(" ").take(3).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = medicoTratante,
                            onValueChange = { medicoTratante = it },
                            label = { Text("Médico Tratante") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = psicologoResponsable,
                            onValueChange = { psicologoResponsable = it },
                            label = { Text("Psicólogo Asignado") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Estatus del Expediente
                    var expandedStatus by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = estatusExpediente,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estatus del Expediente") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false }
                        ) {
                            listOf("ACTIVO", "EN_REVISION", "CONCLUIDO", "ARCHIVADO").forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        estatusExpediente = st
                                        expandedStatus = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // SECCIÓN 4: NORMATIVA NOM-028 Y CONSENTIMIENTO INFORMADO
            item {
                FormSectionCard(
                    title = "4. Consentimiento y Observaciones NOM-028",
                    icon = Icons.Default.VerifiedUser
                ) {
                    Surface(
                        color = if (consentimientoFirmado) StatusActiveGreen.copy(alpha = 0.1f) else Color(0xFFFFA000).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { consentimientoFirmado = !consentimientoFirmado }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = consentimientoFirmado,
                                onCheckedChange = { consentimientoFirmado = it },
                                colors = CheckboxDefaults.colors(checkedColor = StatusActiveGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Consentimiento Informado Firmado",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Cumplimiento con la NOM-028-SSA2 para prevención y tratamiento de adicciones.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notasIngreso,
                        onValueChange = { notasIngreso = it },
                        label = { Text("Notas de Ingreso y Observaciones Iniciales") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botonera de Acción
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = { validateAndSave() },
                shape = RoundedCornerShape(12.dp),
                enabled = !isSaving,
                modifier = Modifier
                    .weight(1.5f)
                    .testTag("save_expediente_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (initialExpediente == null) "Guardar en Room" else "Actualizar en Room")
                }
            }
        }
    }
}

/**
 * Diálogo modal para capturar expedientes usando ExpedienteFormComponent.
 */
@Composable
fun ExpedienteFormDialog(
    initialExpediente: ExpedienteEntity? = null,
    residents: List<ResidentEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSaveSuccess: (ExpedienteEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (initialExpediente == null) "Nuevo Expediente Médico" else "Editar Expediente Clínico",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Persistencia directa en Room Database",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                ExpedienteFormComponent(
                    initialExpediente = initialExpediente,
                    residents = residents,
                    onSaveSuccess = onSaveSuccess,
                    onCancel = onDismiss,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun FormSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
            content()
        }
    }
}
