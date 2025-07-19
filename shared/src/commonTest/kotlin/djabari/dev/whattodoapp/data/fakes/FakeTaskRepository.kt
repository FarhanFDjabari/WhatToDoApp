package djabari.dev.whattodoapp.data.fakes

import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.datasource.toTask
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FakeTaskRepository(
    val taskDataSource: TaskDataSource
) : TaskRepository {

    override suspend fun insertTask(
        taskName: String,
        dueDate: Long,
        isCompleted: Boolean
    ): Task {
        val task = taskDataSource.insertTask(
            taskName = taskName,
            dueDate = dueDate,
            isCompleted = isCompleted
        )
        return task.toTask()
    }

    override fun getAllTasksByCompletedStatus(
        search: String,
        isCompleted: Boolean
    ): List<Task> {
        return taskDataSource.getAllTaskByCompletedStatus(
            search = search,
            isCompleted = isCompleted
        ).map { it.toTask() }
    }

    override fun getTaskById(id: Long): Task? {
        return taskDataSource.getTaskById(id)?.toTask()
    }

    override fun getTasksOrderByDueDate(
        search: String
    ): Flow<List<Task>> = taskDataSource.getTasksOrderByDueDate(search)
        .map { tasks -> tasks.map { it.toTask() } }

    override fun getTasksOrderByDueCompleteStatus(
        search: String
    ): Flow<List<Task>> = taskDataSource.getTasksOrderByDueCompleteStatus(search)
        .map { tasks -> tasks.map { it.toTask() } }

    override suspend fun updateTask(
        id: Long,
        taskName: String?,
        dueDate: Long?,
        isCompleted: Boolean?
    ): Task {
        val updatedTask = taskDataSource.updateTask(
            id = id,
            taskName = taskName,
            dueDate = dueDate,
            isCompleted = isCompleted
        )
        return updatedTask.toTask()
    }

    override suspend fun deleteTask(id: Long) {
        taskDataSource.deleteTask(id)
    }
}