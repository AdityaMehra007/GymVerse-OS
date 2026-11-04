package com.example

import com.example.data.GymRepository
import com.example.model.EquipmentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `repository initial data loads equipment and crowd status`() {
        val repo = GymRepository()
        val equipment = repo.equipmentList.value
        val crowd = repo.crowdStatus.value

        assertTrue(equipment.isNotEmpty())
        assertEquals(44, crowd.occupancyPercentage)
        assertTrue(crowd.activeMembersCount > 0)
    }

    @Test
    fun `equipment fault report creates maintenance ticket and updates status`() {
        val repo = GymRepository()
        val assetId = "EQ-LEG-03"

        repo.reportEquipmentFault(assetId, "Abnormal friction in safety stop pin", "High")

        val updatedAsset = repo.equipmentList.value.find { it.id == assetId }
        assertNotNull(updatedAsset)
        assertEquals(EquipmentStatus.FAULT_REPORTED, updatedAsset?.status)

        val ticket = repo.maintenanceTickets.value.find { it.assetId == assetId }
        assertNotNull(ticket)
        assertEquals("High", ticket?.severity)
    }

    @Test
    fun `group class reservation toggle updates booked count`() {
        val repo = GymRepository()
        val classId = "CLS-02"

        val initialClass = repo.groupClasses.value.find { it.id == classId }
        assertNotNull(initialClass)
        val initialCount = initialClass!!.bookedCount

        repo.toggleClassBooking(classId)

        val updatedClass = repo.groupClasses.value.find { it.id == classId }
        assertTrue(updatedClass!!.isBookedByMe)
        assertEquals(initialCount + 1, updatedClass.bookedCount)
    }

    @Test
    fun `ai assistant answers questions with verified gym equipment`() {
        val repo = GymRepository()
        repo.sendChatMessage("Is the hack squat available?")

        val messages = repo.chatMessages.value
        val lastMessage = messages.last()

        assertTrue(lastMessage.isAi)
        assertTrue(lastMessage.text.contains("Hack Squat", ignoreCase = true))
    }
}
