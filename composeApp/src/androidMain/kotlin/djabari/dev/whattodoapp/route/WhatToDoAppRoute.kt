package djabari.dev.whattodoapp.route

import kotlinx.serialization.Serializable

@Serializable
data object TodoHomeRoute

@Serializable
data class TodoDetailRoute(val taskId: Long)

@Serializable
data object TodoAddNewTaskRoute