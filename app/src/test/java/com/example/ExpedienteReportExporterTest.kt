package com.example

import com.example.data.export.ExpedienteReportExporter
import com.example.data.export.ExportFormat
import com.example.data.model.ExpedienteEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for ExpedienteReportExporter verifying CSV and JSON formatting.
 */
class ExpedienteReportExporterTest {

    private val sampleExpedientes = listOf(
        ExpedienteEntity(
            id = 1,
            folioExpediente = "EXP-SND-2024-001",
            residentId = 10,
            residentName = "Carlos Garza, Jr.",
            fechaApertura = "2024-01-15",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "F10.2 Dependencia al Alcohol \"Severa\"",
            sustanciaDeImpacto = "Alcohol, Tabaco",
            tiempoDeConsumo = "5 años",
            planTratamiento = "Comunidad Terapéutica (NOM-028)",
            medicoTratante = "Dr. Armando Valdés",
            psicologoResponsable = "Lic. Elena Cárdenas",
            estatusExpediente = "ACTIVO",
            consentimientoFirmado = true,
            notasIngreso = "Ingreso voluntario acompañado de su tutor legal."
        ),
        ExpedienteEntity(
            id = 2,
            folioExpediente = "EXP-SND-2024-002",
            residentId = 11,
            residentName = "Mateo Ríos",
            fechaApertura = "2024-02-01",
            tipoIngreso = "OBLIGATORIO",
            diagnosticoPrincipal = "F15.2 Dependencia a Metanfetaminas",
            sustanciaDeImpacto = "Cristal",
            tiempoDeConsumo = "2 años",
            planTratamiento = "Fase 1: Desintoxicación",
            medicoTratante = "Dr. Armando Valdés",
            psicologoResponsable = "Lic. Roberto M.",
            estatusExpediente = "EN_REVISION",
            consentimientoFirmado = false,
            notasIngreso = "Requiere monitoreo psiquiátrico."
        )
    )

    @Test
    fun `test exportToCsv header and escaping`() {
        val csv = ExpedienteReportExporter.exportToCsv(sampleExpedientes)
        assertNotNull(csv)

        // Verify CSV Headers
        assertTrue(csv.contains("ID,Folio_Expediente,ID_Residente,Nombre_Residente,Fecha_Apertura"))
        assertTrue(csv.contains("Diagnostico_Principal_CIE,Sustancia_De_Impacto"))

        // Verify record 1 content and comma escaping
        assertTrue(csv.contains("EXP-SND-2024-001"))
        assertTrue(csv.contains("\"Carlos Garza, Jr.\""))
        assertTrue(csv.contains("\"F10.2 Dependencia al Alcohol \"\"Severa\"\"\""))
        assertTrue(csv.contains("SI"))

        // Verify record 2 content
        assertTrue(csv.contains("EXP-SND-2024-002"))
        assertTrue(csv.contains("Mateo Ríos"))
        assertTrue(csv.contains("NO"))
    }

    @Test
    fun `test exportToJson metadata and structure`() {
        val json = ExpedienteReportExporter.exportToJson(sampleExpedientes)
        assertNotNull(json)

        // Verify institution and NOM-028 metadata
        assertTrue(json.contains("\"institucion\": \"SENDA RESIDENCIAL - Comunidad Terapéutica & ERP\""))
        assertTrue(json.contains("\"normativa\": \"NOM-028-SSA2-2009"))
        assertTrue(json.contains("\"totalRegistros\": 2"))

        // Verify expediente objects
        assertTrue(json.contains("\"folioExpediente\": \"EXP-SND-2024-001\""))
        assertTrue(json.contains("\"residentName\": \"Carlos Garza, Jr.\""))
        assertTrue(json.contains("\"diagnosticoPrincipal\": \"F10.2 Dependencia al Alcohol \\\"Severa\\\"\""))
        assertTrue(json.contains("\"consentimientoFirmado\": true"))
        assertTrue(json.contains("\"consentimientoFirmado\": false"))
    }

    @Test
    fun `test export empty list`() {
        val csv = ExpedienteReportExporter.exportToCsv(emptyList())
        assertTrue(csv.startsWith("ID,Folio_Expediente"))

        val json = ExpedienteReportExporter.exportToJson(emptyList())
        assertTrue(json.contains("\"totalRegistros\": 0"))
        assertTrue(json.contains("\"expedientes\": ["))
    }

    @Test
    fun `test export format properties`() {
        assertEquals("csv", ExportFormat.CSV.extension)
        assertEquals("text/csv", ExportFormat.CSV.mimeType)
        assertEquals("json", ExportFormat.JSON.extension)
        assertEquals("application/json", ExportFormat.JSON.mimeType)
    }
}
