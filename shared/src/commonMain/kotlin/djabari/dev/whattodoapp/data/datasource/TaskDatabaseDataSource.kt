package djabari.dev.whattodoapp.data.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import djabari.dev.whattodoapp.data.sqldelight.Database
import djabari.dev.whattodoapp.data.sqldelight.TaskTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskDatabaseDataSource(private val database: Database) : TaskDataSource {
    override suspend fun insertTask(
        taskName: String,
        dueDate: Long,
        isCompleted: Boolean
    ): TaskTable {
        return withContext(Dispatchers.IO) {
            database.taskQueries.saveTask(
                taskname = taskName, duedate = dueDate, is_completed = if (isCompleted) 1L else 0L
            )
            val insertedTask = database.taskQueries.getLastInsertedTask().executeAsOne()
            return@withContext insertedTask
        }
    }

    override fun getAllTaskByCompletedStatus(search: String, isCompleted: Boolean): List<TaskTable> {
        return database.taskQueries.getAllTasksByCompletedStatus(
            search,
            if (isCompleted) 1L else 0L
        ).executeAsList()
    }

    override fun getTasksOrderByDueDate(search: String): Flow<List<TaskTable>> {
        return database.taskQueries.getTasksByDueDateDesc(search).asFlow().mapToList(Dispatchers.IO)
    }

    override fun getTasksOrderByDueCompleteStatus(search: String): Flow<List<TaskTable>> {
        return database.taskQueries.getTasksByDueCompletionStatus(search).asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun getTaskById(id: Long): TaskTable? {
        return database.taskQueries.getTaskById(id).executeAsOneOrNull()
    }

    override suspend fun updateTask(
        id: Long,
        taskName: String?,
        dueDate: Long?,
        isCompleted: Boolean?
    ): TaskTable {
        return withContext(Dispatchers.IO) {
            database.taskQueries.updateTask(
                id = id,
                taskname = taskName,
                duedate = dueDate,
                is_completed = isCompleted?.let { if (isCompleted) 1L else 0L },
            )
            val updatedTask = database.taskQueries.getTaskById(id).executeAsOne()
            return@withContext updatedTask
        }
    }

    override suspend fun deleteTask(taskId: Long) {
        withContext(Dispatchers.IO) {
            database.taskQueries.deleteTask(taskId)
        }
    }

    override suspend fun clearTasks() {
        withContext(Dispatchers.IO) {
            database.taskQueries.clearTasks()
        }
    }
}