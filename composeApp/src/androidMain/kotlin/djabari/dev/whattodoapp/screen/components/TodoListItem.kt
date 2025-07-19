package djabari.dev.whattodoapp.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import djabari.dev.whattodoapp.data.model.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Composable
fun TodoListItem(
    modifier: Modifier = Modifier,
    todoItem: Task,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit = {}
) {
    val dueDate = remember(todoItem.dueDate) {
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(todoItem.dueDate),
            java.time.ZoneId.systemDefault()
        )
    }
    val dueDateText = remember(dueDate) {
        dueDate.format(
            DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm")
        )
    }

    val isOverdue = dueDate.isBefore(LocalDateTime.now())

    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = {
            onClick()
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (todoItem.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todoItem.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = todoItem.taskName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (todoItem.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else if (isOverdue) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (todoItem.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dueDateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (todoItem.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else if (isOverdue) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

// Preview with sample data
@Preview(showBackground = true)
@Composable
fun TodoListItemPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TodoListItem(
                todoItem = Task(
                    id = 1,
                    taskName = "Complete project documentation",
                    dueDate = LocalDateTime.now().plusHours(1)
                        .atZone(ZoneOffset.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    isCompleted = false
                ),
                onCheckedChange = {}
            )

            TodoListItem(
                todoItem = Task(
                    id = 2,
                    taskName = "Review code changes and submit pull request",
                    dueDate = LocalDateTime.now().minusDays(1)
                        .atZone(ZoneOffset.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    isCompleted = true
                ),
                onCheckedChange = {}
            )

            TodoListItem(
                todoItem = Task(
                    id = 3,
                    taskName = "Schedule team meeting",
                    dueDate = LocalDateTime.now().minusHours(3)
                        .atZone(ZoneOffset.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                    isCompleted = false
                ),
                onCheckedChange = {}
            )
        }
    }
}