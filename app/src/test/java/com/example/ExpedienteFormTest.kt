package com.example

import com.example.data.model.ExpedienteEntity
import com.example.data.repository.ClinicalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test for capturing new medical records (expedientes) through the Repository
 * verifying data integrity for Room storage.
 */
class ExpedienteFormTest {

    @Test
    fun `test capturing and saving new expediente in repository`() = runBlocking {
        val fakeRepo = RepositoryTest.FakeSendaRepository()
        val clinicalRepo: ClinicalRepository = fakeRepo

        val initialList = clinicalRepo.allExpedientes.first()
        val initialCount = initialList.size

        val capturedExpediente = ExpedienteEntity(
            id = 10,
            folioExpediente = "EXP-SND-2024-999",
            residentId = 5,
            residentName = "Carlos Mendoza Silva",
            fechaApertura = "2024-09-29",
            tipoIngreso = "VOLUNTARIO",
            diagnosticoPrincipal = "F15.2 Dependencia a metanfetaminas (Cristal)",
            antecedentesPatologicos = "Gastritis medicamentosa. Alergia a penicilina.",
            sustanciaDeImpacto = "Metanfetamina / Cristal",
            tiempoDeConsumo = "2 años",
            planTratamiento = "Programa Residencial de 6 meses - Modelo Comunidad Terapéutica NOM-028",
            medicoTratante = "Dr. Armando Valdés Soto",
            psicologoResponsable = "Psic. Mariana Flores Peñaloza",
            estatusExpediente = "ACTIVO",
            consentimientoFirmado = true,
            notasIngreso = "Ingreso voluntario acompañado por su madre. Valoración médica completada."
        )

        val insertedId = clinicalRepo.insertExpediente(capturedExpediente)
        assertEquals(10L, insertedId)

        val updatedList = clinicalRepo.allExpedientes.first()
        assertEquals(initialCount + 1, updatedList.size)

        val found = updatedList.find { it.folioExpediente == "EXP-SND-2024-999" }
        assertTrue(found != null)
        assertEquals("Carlos Mendoza Silva", found?.residentName)
        assertEquals("F15.2 Dependencia a metanfetaminas (Cristal)", found?.diagnosticoPrincipal)
        assertTrue(found?.consentimientoFirmado == true)
    }
}
