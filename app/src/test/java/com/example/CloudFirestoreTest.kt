package com.example

import com.example.data.cloud.CloudFirestoreState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Cloud Firestore sync models and state representation.
 */
class CloudFirestoreTest {

    @Test
    fun `test cloud firestore state default values`() {
        val state = CloudFirestoreState()
        assertFalse(state.isAvailable)
        assertFalse(state.isSyncing)
        assertEquals("Nunca", state.lastSyncTime)
        assertEquals(0, state.cloudCountExpedientes)
        assertTrue(state.autoSyncEnabled)
    }

    @Test
    fun `test cloud firestore updated sync state`() {
        val state = CloudFirestoreState(
            isAvailable = true,
            isSyncing = false,
            lastSyncTime = "11:15:00 29/09",
            cloudCountExpedientes = 12,
            cloudCountResidents = 20,
            cloudCountRecords = 8,
            statusMessage = "Sincronizado con éxito en Cloud Firestore"
        )
        assertTrue(state.isAvailable)
        assertFalse(state.isSyncing)
        assertEquals("11:15:00 29/09", state.lastSyncTime)
        assertEquals(12, state.cloudCountExpedientes)
        assertEquals(20, state.cloudCountResidents)
        assertEquals(8, state.cloudCountRecords)
    }
}
