package djabari.dev.whattodoapp.data.fakes

import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.sqldelight.TaskTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTaskDatabaseDataSource : TaskDataSource {
    private val _tasks = MutableStateFlow<List<TaskTable>>(emptyList())
    private val tasks = _tasks.asStateFlow()

    override fun getAllTaskByCompletedStatus(
        search: String,
        isCompleted: Boolean
    ): List<TaskTable> {
        return tasks.value.filter { task ->
            task.taskname.contains(search, ignoreCase = true) &&
                    task.is_completed == if (isCompleted) 1L else 0L
        }
    }

    override fun getTasksOrderByDueDate(search: String): Flow<List<TaskTable>> {
        return tasks.value.filter { task ->
            task.taskname.contains(search, ignoreCase = true)
        }.sortedByDescending { it.duedate }.let {
            MutableStateFlow(it)
        }
    }

    override fun getTasksOrderByDueCompleteStatus(search: String): Flow<List<TaskTable>> {
        return tasks.value.filter { task ->
            task.taskname.contains(search, ignoreCase = true)
        }.sortedByDescending { it.is_completed }.let {
            MutableStateFlow(it)
        }
    }

    override fun getTaskById(id: Long): TaskTable? {
        return tasks.value.firstOrNull { it.id == id }
    }

    override suspend fun insertTask(
        taskName: String,
        dueDate: Long,
        isCompleted: Boolean
    ): TaskTable {
        val newTask = TaskTable(
            id = _tasks.value.size.toLong() + 1,
            taskname = taskName,
            duedate = dueDate,
            is_completed = if (isCompleted) 1L else 0L
        )
        _tasks.value = _tasks.value + newTask
        return newTask
    }

    override suspend fun updateTask(
        id: Long,
        taskName: String?,
        dueDate: Long?,
        isCompleted: Boolean?
    ): TaskTable {
        val updatedTasks = _tasks.value.map { task ->
            if (task.id == id) {
                task.copy(
                    taskname = taskName ?: task.taskname,
                    duedate = dueDate ?: task.duedate,
                    is_completed = isCompleted?.let { if (it) 1L else 0L } ?: task.is_completed
                )
            } else {
                task
            }
        }
        _tasks.value = updatedTasks
        return updatedTasks.first { it.id == id }
    }

    override suspend fun deleteTask(taskId: Long) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
    }

    override suspend fun clearTasks() {
        _tasks.value = emptyList()
    }
}
