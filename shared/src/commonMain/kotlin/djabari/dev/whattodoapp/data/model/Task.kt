package djabari.dev.whattodoapp.data.model

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class Task(
    val id: Long,
    val taskName: String,
    val dueDate: Long,
    val isCompleted: Boolean = false
) {
    @OptIn(ExperimentalTime::class)
    val isOverdue: Boolean
        get() = Clock.System.now().toEpochMilliseconds() > dueDate
    @OptIn(ExperimentalTime::class)
    val isNearOverdue: Boolean
        get() = Clock.System.now().toEpochMilliseconds() > dueDate - 600_000
}
