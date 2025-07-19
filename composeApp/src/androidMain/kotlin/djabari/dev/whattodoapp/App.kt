package djabari.dev.whattodoapp

import android.Manifest
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.repository.TaskRepository
import djabari.dev.whattodoapp.route.TodoAddNewTaskRoute
import djabari.dev.whattodoapp.route.TodoDetailRoute
import djabari.dev.whattodoapp.route.TodoHomeRoute
import djabari.dev.whattodoapp.screen.TodoDetailScreen
import djabari.dev.whattodoapp.screen.TodoHomeScreen
import djabari.dev.whattodoapp.screen.components.toast.ToastHandler
import djabari.dev.whattodoapp.screen.components.toast.rememberToastState
import djabari.dev.whattodoapp.services.TaskAlarmServiceHelper
import djabari.dev.whattodoapp.viewmodel.TaskDetailViewModel
import djabari.dev.whattodoapp.viewmodel.TaskViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel = koinViewModel<TaskViewModel>()
    val alarmHelper = koinInject<TaskAlarmServiceHelper>()
    val toastStateManager = rememberToastState()
    val notificationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        ) else null
    val taskState by viewModel.taskStateFlow.collectAsStateWithLifecycle()

    ToastHandler(toastStateManager)

    LaunchedEffect(Unit) {
        notificationPermissionState?.let {
            if (notificationPermissionState.status is PermissionStatus.Denied) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(taskState) {
        when (taskState) {
            is TaskViewModel.TaskState.Error -> {
                toastStateManager.showErrorToast(
                    (taskState as TaskViewModel.TaskState.Error).message
                )
                viewModel.resetTaskState()
            }

            is TaskViewModel.TaskState.TaskAdded -> {
                viewModel.resetTaskState()
            }

            is TaskViewModel.TaskState.TaskUpdated -> {
                viewModel.resetTaskState()
            }

            is TaskViewModel.TaskState.TaskDeleted -> {
                viewModel.resetTaskState()
            }

            else -> {}
        }
    }

    fun addTaskSchedule(task: Task) {
        viewModel.addTask(
            taskName = task.taskName,
            dueDate = task.dueDate
        )
        if (!task.isOverdue) {
            alarmHelper.scheduleAlarm(task)
        }
    }

    fun updateTaskSchedule(task: Task) {
        viewModel.updateTask(
            taskId = task.id,
            taskName = task.taskName,
            dueDate = task.dueDate
        )
        if (!task.isOverdue && !task.isCompleted) {
            alarmHelper.scheduleAlarm(task)
        } else if (task.isCompleted || task.isOverdue) {
            alarmHelper.cancelAlarm(task)
        }
    }

    fun checkUncheckTask(task: Task) {
        viewModel.updateTask(
            taskId = task.id,
            isCompleted = task.isCompleted
        )
        if (!task.isNearOverdue && !task.isCompleted) {
            alarmHelper.scheduleAlarm(task)
        } else if (task.isCompleted || task.isOverdue) {
            alarmHelper.cancelAlarm(task)
        }
    }

    fun deleteTaskSchedule(task: Task) {
        viewModel.deleteTask(task)
        alarmHelper.cancelAlarm(task)
    }

    MaterialTheme {
        Surface {
            NavHost(
                navController = navController,
                startDestination = TodoHomeRoute
            ) {
                composable<TodoHomeRoute> {
                    val tasksListState by viewModel.tasksDataFlow.collectAsStateWithLifecycle()
                    val taskState by viewModel.taskStateFlow.collectAsStateWithLifecycle()
                    val searchQueryState by viewModel.searchQueryFlow.collectAsStateWithLifecycle()
                    TodoHomeScreen(
                        searchQueryState = searchQueryState,
                        tasksListState = tasksListState,
                        taskState = taskState,
                        onSearch = viewModel::setSearchQuery,
                        onOpenTaskDetail = {
                            navController.navigate(TodoDetailRoute(it))
                        },
                        onAddNewTask = {
                            navController.navigate(TodoAddNewTaskRoute)
                        },
                        onTaskUpdate = {
                            checkUncheckTask(it)
                        },
                        onTaskDelete = {
                            deleteTaskSchedule(it)
                        },
                    )
                }
                composable<TodoAddNewTaskRoute> {
                    TodoDetailScreen(
                        onDismiss = {
                            navController.popBackStack()
                        },
                        onUpdateClick = { task ->
                            navController.popBackStack()
                            addTaskSchedule(task)
                        }
                    )
                }
                composable<TodoDetailRoute> {
                    val args = it.toRoute<TodoDetailRoute>()
                    val repository = koinInject<TaskRepository>()
                    val taskDetailViewModel = viewModel {
                        TaskDetailViewModel(
                            taskId = args.taskId,
                            taskRepository = repository
                        )
                    }
                    val currentTask by taskDetailViewModel.currentTask.collectAsStateWithLifecycle()
                    val taskDetailState by taskDetailViewModel.taskDetailStateFlow.collectAsStateWithLifecycle()
                    LaunchedEffect(taskDetailState) {
                        when (taskDetailState) {
                            is TaskDetailViewModel.TaskDetailState.Error -> {
                                toastStateManager.showErrorToast(
                                    (taskDetailState as TaskDetailViewModel.TaskDetailState.Error).message
                                )
                                taskDetailViewModel.resetTaskDetailState()
                            }

                            else -> {}
                        }
                    }
                    TodoDetailScreen(
                        taskState = currentTask,
                        onDismiss = {
                            navController.popBackStack()
                        },
                        onDeleteClick = { task ->
                            navController.popBackStack()
                            deleteTaskSchedule(task)
                        },
                        onUpdateClick = { task ->
                            navController.popBackStack()
                            updateTaskSchedule(task)
                        }
                    )
                }
            }
        }
    }
}