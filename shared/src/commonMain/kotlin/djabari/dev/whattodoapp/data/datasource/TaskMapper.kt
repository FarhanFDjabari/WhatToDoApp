package djabari.dev.whattodoapp.data.datasource

import djabari.dev.whattodoapp.data.model.Task
import djabari.dev.whattodoapp.data.sqldelight.TaskTable

fun TaskTable.toTask(): Task {
    return Task(
        id = this.id,
        taskName = this.taskname,
        dueDate = this.duedate,
        isCompleted = this.is_completed == 1L
    )
}
