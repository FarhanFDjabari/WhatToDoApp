package djabari.dev.whattodoapp.repository

import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.datasource.toTask
import djabari.dev.whattodoapp.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource
) : TaskRepository {
    override suspend fun insertTask(
        taskName: String,
        dueDate: Long,
        isCompleted: Boolean
    ): Task {
        val newTask = taskDataSource.insertTask(taskName, dueDate, isCompleted)
        return newTask.toTask()
    }

    override fun getAllTasksByCompletedStatus(
        search: String,
        isCompleted: Boolean
    ): List<Task> = taskDataSource.getAllTaskByCompletedStatus(search, isCompleted).map {
        it.toTask()
    }

    override fun getTaskById(id: Long): Task? = taskDataSource.getTaskById(id)?.toTask()

    override fun getTasksOrderByDueDate(
        search: String
    ): Flow<List<Task>> = taskDataSource.getTasksOrderByDueDate(search).map {
        it.map { taskTable ->
            taskTable.toTask()
        }
    }

    override fun getTasksOrderByDueCompleteStatus(
        search: String
    ): Flow<List<Task>> =
        taskDataSource.getTasksOrderByDueCompleteStatus(search).map {
            it.map { taskTable ->
                taskTable.toTask()
            }
        }

    override suspend fun updateTask(
        id: Long,
        taskName: String?,
        dueDate: Long?,
        isCompleted: Boolean?
    ): Task {
        val updatedTask = taskDataSource.updateTask(id, taskName, dueDate, isCompleted)
        return updatedTask.toTask()
    }

    override suspend fun deleteTask(id: Long) {
        taskDataSource.deleteTask(id)
    }

}