package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainSection(
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    INICIO("Inicio", Icons.Default.Dashboard, "Tablero de control y resumen ejecutivo"),
    USUARIOS("Usuarios", Icons.Default.People, "Preingreso, Internados, Egresados y Expedientes"),
    FAMILIAS("Familias", Icons.Default.FamilyRestroom, "Familiares, Visitas, Reuniones y Portal"),
    CLINICA("Clínica", Icons.Default.LocalHospital, "Medicina, Psicología, Psiquiatría, Medicamentos"),
    FINANZAS("Finanzas", Icons.Default.AccountBalanceWallet, "Caja, Ingresos, Egresos, Pagos y Adeudos"),
    AGENDA("Agenda", Icons.Default.CalendarMonth, "Calendario, Citas, Visitas y Eventos"),
    PERSONAL("Personal", Icons.Default.Badge, "Servidores, Profesionales, Empleados y Horarios"),
    OPERACION("Operación", Icons.Default.Assignment, "Turnos, Bitácoras, Llaves, Inventarios"),
    DOCUMENTOS("Documentos", Icons.Default.Description, "Plantillas, PDFs, Contratos y Firma"),
    EVIDENCIAS("Evidencias", Icons.Default.PhotoLibrary, "Instalaciones, Supervisiones y Álbumes"),
    COMUNICACIONES("Comunicaciones", Icons.Default.Chat, "WhatsApp, Correo, Avisos comunitarios"),
    REPORTES("Reportes", Icons.Default.Assessment, "Estadísticas, Gráficos y Métricas"),
    GOOGLE_WORKSPACE("Google Workspace", Icons.Default.CloudSync, "Sincronización Drive, Calendar, Gmail"),
    CONFIGURACION("Configuración", Icons.Default.Settings, "Parámetros del centro y capacidad"),
    ADMINISTRACION("Administración", Icons.Default.AdminPanelSettings, "Seguridad, Roles y Respaldo")
}
