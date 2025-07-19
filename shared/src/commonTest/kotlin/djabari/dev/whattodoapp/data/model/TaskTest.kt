package djabari.dev.whattodoapp.data.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TaskTest {

    @OptIn(ExperimentalTime::class)
    @Test
    fun `isOverdue should return true for past due dates`() {
        val task = Task(
            id = 1,
            taskName = "Test Task",
            dueDate = Clock.System.now().toEpochMilliseconds() - 1000,
            isCompleted = false
        )
        assertTrue(task.isOverdue)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `isOverdue should return false for future due dates`() {
        val task = Task(
            id = 1,
            taskName = "Test Task",
            dueDate = Clock.System.now().toEpochMilliseconds() + 1000,
            isCompleted = false
        )
        assertFalse(task.isOverdue)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `isNearOverdue should return true when due date is within 10 minutes`() {
        val task = Task(
            id = 1,
            taskName = "Test Task",
            dueDate = Clock.System.now().toEpochMilliseconds() + 5 * 60 * 1000, // 5 minutes from now
            isCompleted = false
        )
        assertTrue(task.isNearOverdue)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `isNearOverdue should return false when due date is more than 10 minutes away`() {
        val task = Task(
            id = 1,
            taskName = "Test Task",
            dueDate = Clock.System.now().toEpochMilliseconds() + 11 * 60 * 1000, // 11 minutes from now
            isCompleted = false
        )
        assertFalse(task.isNearOverdue)
    }
}