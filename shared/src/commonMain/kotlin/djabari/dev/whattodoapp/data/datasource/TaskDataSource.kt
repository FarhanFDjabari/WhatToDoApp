package djabari.dev.whattodoapp.data.datasource

import djabari.dev.whattodoapp.data.sqldelight.TaskTable
import kotlinx.coroutines.flow.Flow

interface TaskDataSource {
    fun getAllTaskByCompletedStatus(
        search: String = "",
        isCompleted: Boolean = false
    ): List<TaskTable>
    fun getTasksOrderByDueDate(search: String = ""): Flow<List<TaskTable>>
    fun getTasksOrderByDueCompleteStatus(search: String = ""): Flow<List<TaskTable>>
    fun getTaskById(id: Long): TaskTable?
    suspend fun insertTask(
        taskName: String,
        dueDate: Long,
        isCompleted: Boolean = false
    ): TaskTable
    suspend fun updateTask(
        id: Long,
        taskName: String? = null,
        dueDate: Long? = null,
        isCompleted: Boolean? = null
    ): TaskTable
    suspend fun deleteTask(taskId: Long)
    suspend fun clearTasks()
}
