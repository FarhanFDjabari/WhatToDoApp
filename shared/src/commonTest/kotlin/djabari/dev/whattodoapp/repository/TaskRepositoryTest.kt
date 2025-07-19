package djabari.dev.whattodoapp.repository

import djabari.dev.whattodoapp.data.fakes.FakeTaskDatabaseDataSource
import djabari.dev.whattodoapp.data.fakes.FakeTaskRepository
import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.datasource.toTask
import djabari.dev.whattodoapp.data.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskRepositoryTest {

    private val fakeDataSource: TaskDataSource = FakeTaskDatabaseDataSource()
    private val taskRepository: TaskRepository = FakeTaskRepository(fakeDataSource)

    @Test
    fun `getAllTasks should return tasks from data source`() = runTest {
        val tasks = listOf(
            Task(1, "Task 1", 0L, false),
            Task(2, "Task 2", 0L, true)
        )
        tasks.forEach {
            fakeDataSource.insertTask(it.taskName, it.dueDate, it.isCompleted)
        }

        val result = taskRepository.getTasksOrderByDueDate().first()

        assertEquals(tasks, result)
    }

    @Test
    fun `getTaskById should return task from data source`() = runTest {
        val task = Task(1, "Task 1", 0L, false)
        fakeDataSource.insertTask(task.taskName, task.dueDate)

        val result = taskRepository.getTaskById(1)

        assertEquals(task, result)
    }

    @Test
    fun `insertTask should call insertTask on data source`() = runTest {
        val task = Task(1, "Task 1", 0L, false)
        taskRepository.insertTask(task.taskName, task.dueDate)

        val result = fakeDataSource.getTasksOrderByDueCompleteStatus().first().map { it.toTask() }

        assertEquals(listOf(task), result)
    }

    @Test
    fun `deleteTask should call deleteTask on data source`() = runTest {
        val task = Task(1, "Task 1", 0L, false)
        fakeDataSource.insertTask(task.taskName, task.dueDate)

        taskRepository.deleteTask(1)

        val result = fakeDataSource.getTasksOrderByDueCompleteStatus().first()

        assertEquals(emptyList(), result)
    }

    @Test
    fun `updateTask should call updateTask on data source`() = runTest {
        val task = Task(1, "Task 1", 0L, false)
        fakeDataSource.insertTask(task.taskName, task.dueDate)

        val updatedTask = task.copy(isCompleted = true)
        taskRepository.updateTask(
            id = updatedTask.id,
            taskName = updatedTask.taskName,
            dueDate = updatedTask.dueDate,
            isCompleted = updatedTask.isCompleted
        )

        val result = fakeDataSource.getTasksOrderByDueCompleteStatus().first().first().toTask()

        assertEquals(updatedTask, result)
    }
}