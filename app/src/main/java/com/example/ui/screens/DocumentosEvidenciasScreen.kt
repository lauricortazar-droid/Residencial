package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AdministrativeRecordEntity
import com.example.data.model.ResidentEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun DocumentosEvidenciasScreen(
    residents: List<ResidentEntity>,
    administrativeRecords: List<AdministrativeRecordEntity> = emptyList(),
    onSaveAdministrativeRecord: (AdministrativeRecordEntity) -> Unit = {},
    isEvidenciasMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember {
        mutableStateOf(if (isEvidenciasMode) "EVIDENCIAS_INSTALACIONES" else "PLANTILLAS")
    }
    var selectedResidentForDoc by remember { mutableStateOf(residents.firstOrNull()) }
    var showSignatureDialog by remember { mutableStateOf(false) }
    var signatureSaved by remember { mutableStateOf(false) }
    var previewDocTitle by remember { mutableStateOf<String?>(null) }

    var adminSearchQuery by remember { mutableStateOf("") }
    var showAddAdminRecordDialog by remember { mutableStateOf(false) }
    var selectedAdminRecordDetail by remember { mutableStateOf<AdministrativeRecordEntity?>(null) }
    var adminCategoryFilter by remember { mutableStateOf("TODOS") }

    val subTabs = if (isEvidenciasMode) {
        listOf(
            "EVIDENCIAS_INSTALACIONES" to "Instalaciones",
            "EVIDENCIAS_SUPERVISIONES" to "Supervisiones COFEPRIS",
            "EVIDENCIAS_CAPACITACION" to "Capacitación",
            "EVIDENCIAS_USUARIOS" to "Usuarios y Diplomas"
        )
    } else {
        listOf(
            "PLANTILLAS" to "Plantillas",
            "DISEÑADOR" to "Generador PDF",
            "CONTRATOS" to "Contratos y Registros (${administrativeRecords.size})",
            "FIRMA_DIGITAL" to "Firma Digital",
            "ARCHIVO" to "Archivo Digital"
        )
    }

    val templates = listOf(
        Triple("Contrato de Prestación de Servicios Residenciales", "Legal y Terapéutico", "Válido ante CONADIC y Salubridad"),
        Triple("Consentimiento Informado para Ingreso Voluntario", "Normativa NOM-028-SSA2", "Requiere firma de usuario y tutor"),
        Triple("Convenio Económico y Pagaré de Mensualidad", "Administración", "Detalla cuota y fechas de abono"),
        Triple("Reglamento Interno de Convivencia y Disciplina", "Convivencia Residencial", "Normas de respeto y artículos no permitidos"),
        Triple("Carta de Deslinde y Responsiva Médica", "Clínica", "En caso de emergencias preexistentes")
    )

    val evidencias = listOf(
        Triple("Inspección de Cocina y Comedor", "Cumplimiento con Norma de Manejo Higiénico", "Inspeccionado Sep 2024"),
        Triple("Revisión de Extintores y Rutas de Evacuación", "Aprobado por Protección Civil", "Vigencia Anual"),
        Triple("Dormitorios A, B y Femenil", "Higiene y Ventilación Adecuada", "Supervisión Semanal"),
        Triple("Botiquín y Almacén de Medicamentos", "Cerradura de seguridad y bitácora", "Auditoría Interna")
    )

    val filteredAdminRecords = administrativeRecords.filter { rec ->
        val matchesCategory = when (adminCategoryFilter) {
            "TODOS" -> true
            else -> rec.category.equals(adminCategoryFilter, ignoreCase = true)
        }
        val matchesSearch = adminSearchQuery.isBlank() ||
                rec.folio.contains(adminSearchQuery, ignoreCase = true) ||
                rec.title.contains(adminSearchQuery, ignoreCase = true) ||
                rec.residentName.contains(adminSearchQuery, ignoreCase = true) ||
                rec.responsibleStaff.contains(adminSearchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

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
            if (selectedSubTab == "PLANTILLAS") {
                item {
                    SectionHeader(
                        title = "Formatos y Plantillas Oficiales",
                        subtitle = "Documentos normativos listos para expedientes"
                    )
                }

                items(templates) { (nombre, area, desc) ->
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
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(nombre, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(area, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary))
                                Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                            Button(
                                onClick = { previewDocTitle = nombre },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Ver", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (selectedSubTab == "DISEÑADOR") {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Generador de Documento PDF para Residente",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selecciona el residente para autocompletar el contrato:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(residents) { res ->
                                    FilterChip(
                                        selected = selectedResidentForDoc?.id == res.id,
                                        onClick = { selectedResidentForDoc = res },
                                        label = { Text(res.fullName.split(" ").take(2).joinToString(" ")) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            selectedResidentForDoc?.let { res ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("VISTA PREVIA DEL CONTRATO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                        Text("RESIDENTE: ${res.fullName}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                        Text("FOLIO: ${res.folio} | TUTOR: ${res.tutorName} (${res.tutorPhone})", style = MaterialTheme.typography.bodySmall)
                                        Text("CUOTA ACORDADA: $${String.format("%,.0f", res.monthlyFee)} MXN / mes", style = MaterialTheme.typography.bodySmall)
                                        Text("FECHA DE INGRESO: ${res.admissionDate}", style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "DECLARACIONES: Ambas partes convienen en sujetarse al tratamiento residencial bajo el modelo de Comunidad Terapéutica por un periodo recomendado de 6 meses...",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { showSignatureDialog = true },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Capturar Firma")
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            // Register generated contract as an administrative record in Room!
                                            val newRecord = AdministrativeRecordEntity(
                                                folio = "ADM-CNT-${System.currentTimeMillis() % 10000}",
                                                category = "CONTRATO_INGRESO",
                                                residentId = res.id,
                                                residentName = res.fullName,
                                                title = "Contrato de Ingreso Residencial",
                                                description = "Contrato firmado electrónicamente para estancia de 180 días.",
                                                responsibleStaff = "Dirección General",
                                                date = res.admissionDate,
                                                status = if (signatureSaved) "FIRMADO" else "PENDIENTE",
                                                documentNumber = "PDF-SND-${res.folio}"
                                            )
                                            onSaveAdministrativeRecord(newRecord)
                                            selectedSubTab = "CONTRATOS"
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Guardar en Room")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedSubTab == "FIRMA_DIGITAL") {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Módulo de Firma Digital Biométrica",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Permite a los familiares y residentes rubricar contratos y consentimientos desde la pantalla táctil.",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            if (signatureSaved) {
                                Surface(
                                    color = StatusActiveGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusActiveGreen)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Firma digital registrada con sello de tiempo criptográfico.",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = StatusActiveGreen, fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Button(
                                onClick = { showSignatureDialog = true },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Gesture, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Abrir Pad de Firma")
                            }
                        }
                    }
                }
            }

            if (selectedSubTab.startsWith("EVIDENCIAS")) {
                item {
                    SectionHeader(
                        title = "Álbum de Evidencias e Inspecciones",
                        subtitle = "Respaldo fotográfico y supervisiones oficiales"
                    )
                }

                items(evidencias) { (titulo, desc, fecha) ->
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
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(titulo, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                Text(fecha, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // Room Database: Registros Administrativos & Contratos
            if (selectedSubTab == "CONTRATOS" || selectedSubTab == "ARCHIVO") {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionHeader(
                                title = "Registros Administrativos (Room)",
                                subtitle = "Contratos, consentimientos, resguardos y supervisiones"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search and Actions
                        SearchBarWidget(
                            query = adminSearchQuery,
                            onQueryChange = { adminSearchQuery = it },
                            placeholder = "Buscar por folio, título, residente o responsable..."
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                val categories = listOf(
                                    "TODOS" to "Todos",
                                    "CONTRATO_INGRESO" to "Contratos",
                                    "CONSENTIMIENTO_TUTOR" to "Consentimientos",
                                    "RESGUARDO_VALORES" to "Resguardos",
                                    "SUPERVISION_OFICIAL" to "Supervisiones"
                                )
                                items(categories) { (key, label) ->
                                    FilterChip(
                                        selected = adminCategoryFilter == key,
                                        onClick = { adminCategoryFilter = key },
                                        label = { Text(label, fontSize = 12.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { showAddAdminRecordDialog = true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Nuevo Registro", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                if (filteredAdminRecords.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No hay registros administrativos que coincidan.", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { showAddAdminRecordDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Crear Registro en Room")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredAdminRecords, key = { it.id }) { record ->
                        AdministrativeRecordCard(
                            record = record,
                            onDetailClick = { selectedAdminRecordDetail = record }
                        )
                    }
                }
            }
        }
    }

    // Signature Canvas Dialog
    if (showSignatureDialog) {
        Dialog(onDismissRequest = { showSignatureDialog = false }) {
            var pathPoints by remember { mutableStateOf(listOf<Offset>()) }

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pad de Firma Táctil",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { showSignatureDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    Text(
                        text = "Firma del tutor responsable o residente para el expediente oficial:",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    pathPoints = pathPoints + change.position
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (pathPoints.size > 1) {
                                for (i in 0 until pathPoints.size - 1) {
                                    drawLine(
                                        color = Color(0xFF003D4D),
                                        start = pathPoints[i],
                                        end = pathPoints[i + 1],
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { pathPoints = emptyList() }) {
                            Text("Limpiar")
                        }
                        Row {
                            TextButton(onClick = { showSignatureDialog = false }) {
                                Text("Cancelar")
                            }
                            Button(onClick = {
                                signatureSaved = true
                                showSignatureDialog = false
                            }) {
                                Text("Aceptar y Guardar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Administrative Record Dialog
    if (showAddAdminRecordDialog) {
        AddAdminRecordDialog(
            residents = residents,
            onDismiss = { showAddAdminRecordDialog = false },
            onSave = {
                onSaveAdministrativeRecord(it)
                showAddAdminRecordDialog = false
            }
        )
    }

    // Administrative Record Detail Dialog
    selectedAdminRecordDetail?.let { record ->
        AlertDialog(
            onDismissRequest = { selectedAdminRecordDetail = null },
            icon = { Icon(Icons.Default.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(record.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Folio: ${record.folio}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
                    Text("Categoría: ${record.category}", style = MaterialTheme.typography.bodySmall)
                    Text("Residente / Sujeto: ${record.residentName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Fecha: ${record.date} • Estatus: ${record.status}", style = MaterialTheme.typography.bodySmall)
                    Text("Personal Responsable: ${record.responsibleStaff}", style = MaterialTheme.typography.bodySmall)
                    if (record.documentNumber.isNotBlank()) {
                        Text("Doc. Ref: ${record.documentNumber}", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Descripción y Resguardo:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            Text(record.description, style = MaterialTheme.typography.bodySmall)
                            if (record.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Observaciones: ${record.notes}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedAdminRecordDetail = null }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Document Preview Dialog
    previewDocTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { previewDocTitle = null },
            title = { Text(title) },
            text = {
                Text(
                    "Este documento se encuentra en la plantilla maestra oficial de la institución con folios y logos autorizados. Listo para exportar o imprimir en formato PDF institucional.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = { previewDocTitle = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun AdministrativeRecordCard(
    record: AdministrativeRecordEntity,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (record.category) {
                                "CONTRATO_INGRESO" -> Icons.Default.Gavel
                                "RESGUARDO_VALORES" -> Icons.Default.Lock
                                "CONSENTIMIENTO_TUTOR" -> Icons.Default.AssignmentTurnedIn
                                else -> Icons.Default.Article
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = record.folio,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = record.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Surface(
                    color = when (record.status) {
                        "FIRMADO", "VIGENTE" -> StatusActiveGreen.copy(alpha = 0.15f)
                        else -> Color(0xFFFFA000).copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (record.status) {
                                "FIRMADO", "VIGENTE" -> StatusActiveGreen
                                else -> Color(0xFFD68100)
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Residente: ${record.residentName}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = record.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${record.date} • ${record.responsibleStaff}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                TextButton(
                    onClick = onDetailClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Ver Detalle", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddAdminRecordDialog(
    residents: List<ResidentEntity>,
    onDismiss: () -> Unit,
    onSave: (AdministrativeRecordEntity) -> Unit
) {
    var folio by remember { mutableStateOf("ADM-${(100..999).random()}") }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("CONTRATO_INGRESO") }
    var selectedResidentName by remember { mutableStateOf(residents.firstOrNull()?.fullName ?: "Centro Senda") }
    var selectedResidentId by remember { mutableStateOf(residents.firstOrNull()?.id) }
    var responsibleStaff by remember { mutableStateOf("Lic. Carlos Méndez (Director)") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Registro Administrativo (Room)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = folio,
                    onValueChange = { folio = it },
                    label = { Text("Folio Administrativo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del Registro") },
                    placeholder = { Text("Ej. Resguardo de Pertenencias") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedResidentName,
                    onValueChange = { selectedResidentName = it },
                    label = { Text("Residente o Beneficiario") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = responsibleStaff,
                    onValueChange = { responsibleStaff = it },
                    label = { Text("Personal Responsable") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción / Cláusula") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val newRecord = AdministrativeRecordEntity(
                            folio = folio,
                            category = category,
                            residentId = selectedResidentId,
                            residentName = selectedResidentName,
                            title = title,
                            description = description,
                            responsibleStaff = responsibleStaff,
                            date = "2024-09-29",
                            status = "FIRMADO",
                            notes = notes
                        )
                        onSave(newRecord)
                    }
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
