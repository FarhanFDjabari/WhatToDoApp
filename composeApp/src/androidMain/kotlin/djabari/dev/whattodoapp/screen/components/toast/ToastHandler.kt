package djabari.dev.whattodoapp.screen.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ToastHandler(toastStateManager: ToastStateManager) {
    val toastState = toastStateManager.toastState

    LaunchedEffect(toastState.isVisible) {
        if (toastState.isVisible) {
            toastStateManager.displayToast()
        }
    }
}