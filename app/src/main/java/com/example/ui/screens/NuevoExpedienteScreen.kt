package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ExpedienteEntity
import com.example.data.model.ResidentEntity
import com.example.data.repository.ClinicalRepository
import com.example.ui.components.ExpedienteFormComponent
import kotlinx.coroutines.launch

/**
 * Pantalla completa dedicada para capturar un nuevo expediente médico,
 * interactuando directamente con el ClinicalRepository para persistir en Room Database.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoExpedienteScreen(
    clinicalRepository: ClinicalRepository,
    residents: List<ResidentEntity> = emptyList(),
    initialExpediente: ExpedienteEntity? = null,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(saveSuccessMessage) {
        saveSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (initialExpediente == null) "Nuevo Expediente Médico" else "Editar Expediente Clínico",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Base de Datos Local Room • NOM-028-SSA2",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ExpedienteFormComponent(
                initialExpediente = initialExpediente,
                residents = residents,
                onSaveSuccess = { expedienteToSave ->
                    coroutineScope.launch {
                        if (expedienteToSave.id == 0) {
                            clinicalRepository.insertExpediente(expedienteToSave)
                            saveSuccessMessage = "Expediente ${expedienteToSave.folioExpediente} guardado en Room exitosamente"
                        } else {
                            clinicalRepository.updateExpediente(expedienteToSave)
                            saveSuccessMessage = "Expediente ${expedienteToSave.folioExpediente} actualizado en Room exitosamente"
                        }
                    }
                },
                onCancel = onNavigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("nuevo_expediente_form")
            )
        }
    }
}
