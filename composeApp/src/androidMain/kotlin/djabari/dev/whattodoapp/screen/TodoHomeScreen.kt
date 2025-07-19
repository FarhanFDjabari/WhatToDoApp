package djabari.dev.whattodoapp.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PostAdd
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.screen.components.SwipeToDeleteTodoItem
import djabari.dev.whattodoapp.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoHomeScreen(
    modifier: Modifier = Modifier,
    searchQueryState: String,
    tasksListState: List<Task>,
    taskState: TaskViewModel.TaskState,
    onSearch: (String) -> Unit = {},
    onAddNewTask: () -> Unit = {},
    onTaskUpdate: (Task) -> Unit = {},
    onTaskDelete: (Task) -> Unit = {},
    onOpenTaskDetail: (Long) -> Unit = {},
) {
    val taskListState = rememberLazyListState()
    var showSearchBar by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(taskState) {
        when (taskState) {
            is TaskViewModel.TaskState.TaskAdded -> {
                taskListState.scrollToItem(0)
            }

            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            AnimatedVisibility(
                visible = !showSearchBar,
                modifier = Modifier.fillMaxWidth(),
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it / 2 }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                TopAppBar(
                    title = {
                        Text(text = "What To Do")
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                showSearchBar = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Search"
                            )
                        }
                    }
                )
            }
            AnimatedVisibility(
                visible = showSearchBar,
                modifier = Modifier.fillMaxWidth(),
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQueryState,
                            onQueryChange = {
                                onSearch(it)
                            },
                            onSearch = {
                                onSearch(it)
                            },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = {
                                Text(text = "Search tasks")
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        onSearch("")
                                        showSearchBar = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Close Search"
                                    )
                                }
                            }
                        )
                    },
                    expanded = false,
                    onExpandedChange = {},
                ) {}
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAddNewTask()
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.PostAdd,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { innerPadding ->
        if (tasksListState.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "No tasks available",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                state = taskListState,
                modifier = Modifier
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    tasksListState,
                    key = { item -> item.id },
                    contentType = { item -> item }
                ) { task ->
                    SwipeToDeleteTodoItem(
                        modifier = Modifier.animateItem(),
                        todoItem = task,
                        onCheckedChange = { checked ->
                            onTaskUpdate(
                                task.copy(isCompleted = checked)
                            )
                        },
                        onDelete = {
                            onTaskDelete(task)
                        },
                        onClick = {
                            onOpenTaskDetail(task.id)
                        }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TodoHomeScreenPreview() {
    MaterialTheme {
        Surface {
            TodoHomeScreen(
                searchQueryState = "",
                tasksListState = listOf(
                    Task(id = 1, taskName = "Task 1", dueDate = 0L, isCompleted = false),
                    Task(id = 2, taskName = "Task 2", dueDate = 0L, isCompleted = true)
                ),
                taskState = TaskViewModel.TaskState.Idle
            )
        }
    }
}