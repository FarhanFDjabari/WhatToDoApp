package djabari.dev.whattodoapp.viewmodel

import djabari.dev.whattodoapp.data.fakes.FakeTaskDatabaseDataSource
import djabari.dev.whattodoapp.data.fakes.FakeTaskRepository
import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExperimentalCoroutinesApi
class TaskDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var taskRepository: TaskRepository
    private lateinit var fakeDataSource: TaskDataSource

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeTaskDatabaseDataSource()
        taskRepository = FakeTaskRepository(fakeDataSource)
        viewModel = TaskDetailViewModel(taskId = 1, taskRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTaskById should load task`() = runTest {
        val task = Task(1, "Task 1", 0L, false)
        taskRepository.insertTask(task.taskName, task.dueDate)

        viewModel.getTask()
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.currentTask.value
        assertEquals(task, result)
    }

    @Test
    fun `getTaskById should handle non-existent task`() = runTest {
        viewModel.getTask()
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.currentTask.value
        assertNull(result)
    }
}
