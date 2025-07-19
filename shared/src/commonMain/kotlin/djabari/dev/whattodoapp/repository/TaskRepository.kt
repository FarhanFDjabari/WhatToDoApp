package djabari.dev.whattodoapp.repository

import djabari.dev.whattodoapp.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(
        taskName: String,
        dueDate: Long,
        isCompleted: Boolean = false
    ): Task

    fun getAllTasksByCompletedStatus(
        search: String = "",
        isCompleted: Boolean = false
    ): List<Task>

    fun getTaskById(id: Long): Task?

    fun getTasksOrderByDueDate(
        search: String = ""
    ): Flow<List<Task>>

    fun getTasksOrderByDueCompleteStatus(
        search: String = ""
    ): Flow<List<Task>>

    suspend fun updateTask(
        id: Long,
        taskName: String? = null,
        dueDate: Long? = null,
        isCompleted: Boolean? = null
    ): Task

    suspend fun deleteTask(id: Long)
}