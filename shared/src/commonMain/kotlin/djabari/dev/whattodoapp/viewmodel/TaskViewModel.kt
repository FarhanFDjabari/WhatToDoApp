package djabari.dev.whattodoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.repository.TaskRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _taskSortTypeFlow = MutableStateFlow(TaskSortType.DUE_DATE)
    private val _searchQueryFlow = MutableStateFlow("")
    val searchQueryFlow = _searchQueryFlow.asStateFlow()
    private val _tasksDataFlow = MutableStateFlow<List<Task>>(emptyList())
    val tasksDataFlow = _tasksDataFlow.asStateFlow()
    private val _taskStateFlow = MutableStateFlow<TaskState>(TaskState.Idle)
    val taskStateFlow = _taskStateFlow.asStateFlow()

    private var _taskFetchJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                _searchQueryFlow,
                _taskSortTypeFlow,
                ::Pair
            )
                .debounce(300L)
                .collectLatest {
                    getTasks()
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQueryFlow.value = query
    }

    fun getTasks() {
        _taskFetchJob?.cancel()
        _taskFetchJob = viewModelScope.launch {
            _taskStateFlow.value = TaskState.Loading
            try {
                when (_taskSortTypeFlow.value) {
                    TaskSortType.DUE_DATE -> {
                        taskRepository.getTasksOrderByDueDate(
                            search = _searchQueryFlow.value
                        )
                    }

                    TaskSortType.COMPLETION_STATUS -> {
                        taskRepository.getTasksOrderByDueCompleteStatus(
                            search = _searchQueryFlow.value
                        )
                    }
                }.collectLatest {
                    _tasksDataFlow.value = it
                    _taskStateFlow.value = TaskState.Success
                }
            } catch (e: Exception) {
                _taskStateFlow.value = TaskState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun addTask(taskName: String, dueDate: Long) {
        viewModelScope.launch {
            _taskStateFlow.value = TaskState.AddTaskLoading
            try {
                val newTask = taskRepository.insertTask(
                    taskName = taskName,
                    dueDate = dueDate
                )
                _taskStateFlow.value = TaskState.TaskAdded(newTask)
            } catch (e: Exception) {
                _taskStateFlow.value = TaskState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun updateTask(
        taskId: Long,
        taskName: String? = null,
        dueDate: Long? = null,
        isCompleted: Boolean? = null
    ) {
        viewModelScope.launch {
            _taskStateFlow.value = TaskState.UpdateTaskLoading
            try {
                val updatedTask = taskRepository.updateTask(
                    id = taskId,
                    taskName = taskName,
                    dueDate = dueDate,
                    isCompleted = isCompleted
                )
                _taskStateFlow.value = TaskState.TaskUpdated(updatedTask)
            } catch (e: Exception) {
                _taskStateFlow.value = TaskState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _taskStateFlow.value = TaskState.DeleteTaskLoading
            try {
                taskRepository.deleteTask(
                    id = task.id
                )
                _taskStateFlow.value = TaskState.TaskDeleted(task)
            } catch (e: Exception) {
                _taskStateFlow.value = TaskState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetTaskState() {
        _taskStateFlow.value = TaskState.Idle
    }

    override fun onCleared() {
        _taskFetchJob?.cancel()
        _taskFetchJob = null
        super.onCleared()
    }

    enum class TaskSortType {
        DUE_DATE,
        COMPLETION_STATUS
    }

    sealed interface TaskState {
        data object Idle : TaskState
        data object Success : TaskState
        data class Error(val message: String) : TaskState
        data class TaskAdded(val task: Task) : TaskState
        data class TaskUpdated(val task: Task) : TaskState
        data class TaskDeleted(val task: Task): TaskState
        data object Loading : TaskState
        data object UpdateTaskLoading : TaskState
        data object AddTaskLoading : TaskState
        data object DeleteTaskLoading : TaskState
    }
}