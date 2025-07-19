package djabari.dev.whattodoapp.viewmodel

import djabari.dev.whattodoapp.data.fakes.FakeTaskDatabaseDataSource
import djabari.dev.whattodoapp.data.fakes.FakeTaskRepository
import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TaskViewModel
    private lateinit var taskRepository: TaskRepository
    private lateinit var fakeDataSource: TaskDataSource

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeTaskDatabaseDataSource()
        taskRepository = FakeTaskRepository(fakeDataSource)
        viewModel = TaskViewModel(taskRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals("", viewModel.searchQueryFlow.value)
        assertEquals(emptyList(), viewModel.tasksDataFlow.value)
        assertEquals(TaskViewModel.TaskState.Idle, viewModel.taskStateFlow.value)
    }

    @Test
    fun `onSearchQueryChanged updates search query`() {
        viewModel.setSearchQuery("test")
        assertEquals("test", viewModel.searchQueryFlow.value)
    }

    @Test
    fun `addTask adds a new task`() = runTest {
        val task = Task(0, "New Task", 0L, false)
        viewModel.addTask(task.taskName, task.dueDate)

        testDispatcher.scheduler.advanceTimeBy(100L)

        assertEquals(
            TaskViewModel.TaskState.TaskAdded(task.copy(1)),
            viewModel.taskStateFlow.value
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val tasks = taskRepository.getTasksOrderByDueCompleteStatus().first()
        assertEquals(1, tasks.size)
        assertEquals(task.copy(id = 1), tasks[0])
    }

    @Test
    fun `updateTask updates an existing task`() = runTest {
        val task = Task(1, "Task", 0L, false)
        taskRepository.insertTask(task.taskName, task.dueDate)
        val updatedTask = task.copy(isCompleted = true)
        viewModel.updateTask(
            updatedTask.id,
            updatedTask.taskName,
            updatedTask.dueDate,
            updatedTask.isCompleted
        )
        testDispatcher.scheduler.advanceTimeBy(100L)
        assertEquals(
            TaskViewModel.TaskState.TaskUpdated(updatedTask),
            viewModel.taskStateFlow.value
        )
        testDispatcher.scheduler.advanceUntilIdle()
        val tasks = taskRepository.getTasksOrderByDueCompleteStatus().first()
        assertEquals(1, tasks.size)
        assertEquals(updatedTask, tasks[0])
    }

    @Test
    fun `deleteTask deletes a task`() = runTest {
        val task = Task(1, "Task", 0L, false)
        taskRepository.insertTask(task.taskName, task.dueDate)
        viewModel.deleteTask(task)
        testDispatcher.scheduler.advanceTimeBy(100L)
        assertEquals(TaskViewModel.TaskState.TaskDeleted(task), viewModel.taskStateFlow.value)
        testDispatcher.scheduler.advanceUntilIdle()
        val tasks = taskRepository.getTasksOrderByDueCompleteStatus().first()
        assertEquals(0, tasks.size)
    }
}
