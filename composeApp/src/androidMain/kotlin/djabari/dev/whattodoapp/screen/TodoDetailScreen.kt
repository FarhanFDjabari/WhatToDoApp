package djabari.dev.whattodoapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.screen.components.DatePickerDialog
import djabari.dev.whattodoapp.screen.components.TimePickerDialog
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.time.ExperimentalTime

private val LocalDateTimeSaver = listSaver<LocalDateTime, Any>(
    save = {
        listOf(
            it.year,
            it.monthValue,
            it.dayOfMonth,
            it.hour,
            it.minute,
            it.second,
            it.nano
        )
    },
    restore = {
        LocalDateTime.of(
            it[0] as Int,
            it[1] as Int,
            it[2] as Int,
            it[3] as Int,
            it[4] as Int,
            it[5] as Int,
            it[6] as Int
        )
    }
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
internal fun TodoDetailScreen(
    modifier: Modifier = Modifier,
    taskState: Task? = null,
    onDismiss: () -> Unit = {},
    onDeleteClick: (Task) -> Unit = {},
    onUpdateClick: (Task) -> Unit = {}
) {
    var taskName by rememberSaveable { mutableStateOf(taskState?.taskName ?: "") }
    var selectedDateTime by rememberSaveable(stateSaver = LocalDateTimeSaver) {
        mutableStateOf(
            taskState?.dueDate?.let {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            } ?: LocalDateTime.now()
        )
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .isAfter(LocalDateTime.now().toLocalDate())
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = selectedDateTime.hour,
        initialMinute = selectedDateTime.minute,
        is24Hour = true
    )
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    val isTimeValid = remember(timePickerState.hour, timePickerState.minute) {
        val now = LocalDateTime.now()
        LocalDateTime.of(
            selectedDateTime.toLocalDate(),
            LocalTime.of(timePickerState.hour, timePickerState.minute)
        ).isAfter(now)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (taskState == null) "New Task" else "Edit Task",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Due Date & Time",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Date Picker Button
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateFormatter.format(
                                Date.from(
                                    selectedDateTime.atZone(ZoneId.systemDefault()).toInstant()
                                )
                            )
                        )
                    }

                    // Time Picker Button
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Select Time"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timeFormatter.format(
                                Date.from(
                                    selectedDateTime.atZone(ZoneId.systemDefault()).toInstant()
                                )
                            )
                        )
                    }

                    if (!isTimeValid) {
                        Text(
                            text = "Selected time is in the past",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (taskState != null) {
                    OutlinedButton(
                        onClick = {
                            onDeleteClick(taskState)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete")
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        val updatedTodo = taskState?.copy(
                            taskName = taskName.trim(),
                            dueDate = selectedDateTime.toKotlinLocalDateTime()
                                .toInstant(TimeZone.currentSystemDefault())
                                .toEpochMilliseconds()
                        ) ?: Task(
                            id = 0L,
                            taskName = taskName.trim(),
                            dueDate = selectedDateTime.toKotlinLocalDateTime()
                                .toInstant(TimeZone.currentSystemDefault())
                                .toEpochMilliseconds(),
                            isCompleted = false
                        )
                        onUpdateClick(updatedTodo)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = taskName.trim().isNotEmpty() && isTimeValid
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save Task"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            datePickerState = datePickerState,
            onDateSelected = { dateMillis ->
                dateMillis?.let {
                    val localDate = Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    selectedDateTime = LocalDateTime.of(
                        localDate,
                        selectedDateTime.toLocalTime()
                    )
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                selectedDateTime = LocalDateTime.of(
                    selectedDateTime.toLocalDate(),
                    LocalTime.of(hour, minute)
                )
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
            timePickerState = timePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun TodoDetailScreenPreview() {
    MaterialTheme {
        Surface {
            TodoDetailScreen(
                taskState = Task(
                    id = 1L,
                    taskName = "Sample Task",
                    dueDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli(),
                    isCompleted = false
                ),
            )
        }
    }
}