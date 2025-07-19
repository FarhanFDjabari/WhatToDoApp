package djabari.dev.whattodoapp.screen.components.toast

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

data class ToastState(
    val message: String = "",
    val isVisible: Boolean = false,
    val duration: Int = Toast.LENGTH_SHORT
)

@Composable
fun rememberToastState(): ToastStateManager {
    val context = LocalContext.current
    return remember { ToastStateManager(context) }
}

class ToastStateManager(private val context: android.content.Context) {
    private var _toastState by mutableStateOf(ToastState())
    val toastState: ToastState get() = _toastState

    fun showToast(
        message: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        _toastState = ToastState(
            message = message,
            isVisible = true,
            duration = duration
        )
    }

    fun showErrorToast(errorMessage: String) {
        showToast(errorMessage, Toast.LENGTH_LONG)
    }

    fun hideToast() {
        _toastState = _toastState.copy(isVisible = false)
    }

    // Function to actually display the Android toast
    fun displayToast() {
        if (_toastState.isVisible && _toastState.message.isNotEmpty()) {
            Toast.makeText(context, _toastState.message, _toastState.duration).show()
            hideToast()
        }
    }
}