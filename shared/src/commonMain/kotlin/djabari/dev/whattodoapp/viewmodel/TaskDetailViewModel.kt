package djabari.dev.whattodoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskId: Long,
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask = _currentTask.asStateFlow()
    private val _taskDetailStateFlow = MutableStateFlow<TaskDetailState>(TaskDetailState.Idle)
    val taskDetailStateFlow = _taskDetailStateFlow.asStateFlow()

    init {
        getTask()
    }

    fun getTask() {
        viewModelScope.launch {
            _taskDetailStateFlow.value = TaskDetailState.DetailTaskLoading
            try {
                val taskDetail = taskRepository.getTaskById(taskId)
                if (taskDetail != null) {
                    _taskDetailStateFlow.value = TaskDetailState.DetailTask(taskDetail)
                    _currentTask.value = taskDetail
                } else {
                    _taskDetailStateFlow.value = TaskDetailState.Error("Task not found")
                }
            } catch (e: Exception) {
                _taskDetailStateFlow.value = TaskDetailState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun resetTaskDetailState() {
        _taskDetailStateFlow.value = TaskDetailState.Idle
    }

    sealed interface TaskDetailState {
        data object Idle : TaskDetailState
        data object DetailTaskLoading : TaskDetailState
        data class Error(val message: String) : TaskDetailState
        data class DetailTask(val task: Task) : TaskDetailState
    }
}