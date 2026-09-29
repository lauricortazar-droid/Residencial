package com.example.data.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.ExpedienteEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formatos de exportación admitidos para reportes clínicos y normativos NOM-028.
 */
enum class ExportFormat(val extension: String, val mimeType: String, val displayName: String) {
    CSV("csv", "text/csv", "CSV (Excel / Hojas de Cálculo)"),
    JSON("json", "application/json", "JSON (Reporte Clínico Digital)")
}

/**
 * Utilidad especializada para exportar los expedientes clínicos locales
 * almacenados en Room hacia archivos estructurados CSV y JSON para auditoría,
 * reportes NOM-028 y copias de seguridad clínicas.
 */
object ExpedienteReportExporter {

    private const val INSTITUCION = "SENDA RESIDENCIAL - Comunidad Terapéutica & ERP"
    private const val NORMATIVA = "NOM-028-SSA2-2009 (Prevención, Tratamiento y Control de las Adicciones)"

    /**
     * Convierte la lista de expedientes clínicos a formato CSV estándar (RFC 4180).
     */
    fun exportToCsv(expedientes: List<ExpedienteEntity>): String {
        val sb = StringBuilder()

        // Encabezados normalizados
        val headers = listOf(
            "ID",
            "Folio_Expediente",
            "ID_Residente",
            "Nombre_Residente",
            "Fecha_Apertura",
            "Tipo_Ingreso",
            "Diagnostico_Principal_CIE",
            "Sustancia_De_Impacto",
            "Tiempo_De_Consumo",
            "Plan_Tratamiento",
            "Medico_Tratante",
            "Psicologo_Responsable",
            "Estatus_Expediente",
            "Consentimiento_Firmado",
            "Notas_Ingreso"
        )
        sb.append(headers.joinToString(",")).append("\r\n")

        // Registros
        for (exp in expedientes) {
            val row = listOf(
                exp.id.toString(),
                escapeCsv(exp.folioExpediente),
                exp.residentId.toString(),
                escapeCsv(exp.residentName),
                escapeCsv(exp.fechaApertura),
                escapeCsv(exp.tipoIngreso),
                escapeCsv(exp.diagnosticoPrincipal),
                escapeCsv(exp.sustanciaDeImpacto),
                escapeCsv(exp.tiempoDeConsumo),
                escapeCsv(exp.planTratamiento),
                escapeCsv(exp.medicoTratante),
                escapeCsv(exp.psicologoResponsable),
                escapeCsv(exp.estatusExpediente),
                if (exp.consentimientoFirmado) "SI" else "NO",
                escapeCsv(exp.notasIngreso)
            )
            sb.append(row.joinToString(",")).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Convierte la lista de expedientes clínicos a formato JSON con metadatos institucionales.
     */
    fun exportToJson(expedientes: List<ExpedienteEntity>, prettyPrint: Boolean = true): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        val sb = StringBuilder()
        val indent = if (prettyPrint) "  " else ""
        val nl = if (prettyPrint) "\n" else ""

        sb.append("{").append(nl)
        sb.append(indent).append("\"institucion\": \"").append(escapeJson(INSTITUCION)).append("\",").append(nl)
        sb.append(indent).append("\"normativa\": \"").append(escapeJson(NORMATIVA)).append("\",").append(nl)
        sb.append(indent).append("\"fechaGeneracion\": \"").append(timestamp).append("\",").append(nl)
        sb.append(indent).append("\"totalRegistros\": ").append(expedientes.size).append(",").append(nl)
        sb.append(indent).append("\"expedientes\": [").append(nl)

        expedientes.forEachIndexed { index, exp ->
            sb.append(indent).append(indent).append("{").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"id\": ").append(exp.id).append(",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"folioExpediente\": \"").append(escapeJson(exp.folioExpediente)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"residentId\": ").append(exp.residentId).append(",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"residentName\": \"").append(escapeJson(exp.residentName)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"fechaApertura\": \"").append(escapeJson(exp.fechaApertura)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"tipoIngreso\": \"").append(escapeJson(exp.tipoIngreso)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"diagnosticoPrincipal\": \"").append(escapeJson(exp.diagnosticoPrincipal)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"sustanciaDeImpacto\": \"").append(escapeJson(exp.sustanciaDeImpacto)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"tiempoDeConsumo\": \"").append(escapeJson(exp.tiempoDeConsumo)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"planTratamiento\": \"").append(escapeJson(exp.planTratamiento)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"medicoTratante\": \"").append(escapeJson(exp.medicoTratante)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"psicologoResponsable\": \"").append(escapeJson(exp.psicologoResponsable)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"estatusExpediente\": \"").append(escapeJson(exp.estatusExpediente)).append("\",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"consentimientoFirmado\": ").append(exp.consentimientoFirmado).append(",").append(nl)
            sb.append(indent).append(indent).append(indent).append("\"notasIngreso\": \"").append(escapeJson(exp.notasIngreso)).append("\"").append(nl)
            sb.append(indent).append(indent).append("}")
            if (index < expedientes.size - 1) {
                sb.append(",")
            }
            sb.append(nl)
        }

        sb.append(indent).append("]").append(nl)
        sb.append("}")

        return sb.toString()
    }

    /**
     * Guarda el reporte en el almacenamiento temporal de la app (sin requerir permisos de almacenamiento).
     */
    fun createExportFile(context: Context, expedientes: List<ExpedienteEntity>, format: ExportFormat): File {
        val reportDir = File(context.cacheDir, "reports")
        if (!reportDir.exists()) {
            reportDir.mkdirs()
        }

        val dateSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "expedientes_clinicos_${dateSuffix}.${format.extension}"
        val file = File(reportDir, fileName)

        val content = when (format) {
            ExportFormat.CSV -> exportToCsv(expedientes)
            ExportFormat.JSON -> exportToJson(expedientes)
        }

        FileOutputStream(file).use { out ->
            out.write(content.toByteArray(Charsets.UTF_8))
        }

        return file
    }

    /**
     * Construye un Intent nativo para compartir o enviar el archivo generado (ShareSheet).
     */
    fun createShareIntent(context: Context, file: File, format: ExportFormat): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = format.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Reporte Clínico de Expedientes - SENDA Residencial")
            putExtra(Intent.EXTRA_TEXT, "Adjunto reporte de expedientes clínicos en formato ${format.displayName} emitido por el sistema SENDA Residencial conforme a la NOM-028-SSA2.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return Intent.createChooser(intent, "Exportar / Compartir Reporte de Expedientes")
    }

    /**
     * Copia el texto exportado directamente al portapapeles.
     */
    fun copyToClipboard(context: Context, content: String, label: String = "Reporte Expedientes") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
    }

    private fun escapeCsv(value: String): String {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\""
        }
        return value
    }

    private fun escapeJson(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\u000C", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
