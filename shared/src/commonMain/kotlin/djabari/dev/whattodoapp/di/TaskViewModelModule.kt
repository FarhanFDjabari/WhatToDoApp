package djabari.dev.whattodoapp.di

import djabari.dev.whattodoapp.repository.TaskRepository
import djabari.dev.whattodoapp.viewmodel.TaskViewModel
import org.koin.dsl.module

val taskViewModelModule = module {
    factory {
        TaskViewModel(
            taskRepository = get<TaskRepository>()
        )
    }
}