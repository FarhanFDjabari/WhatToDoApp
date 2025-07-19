package djabari.dev.whattodoapp.di

import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.repository.TaskRepository
import djabari.dev.whattodoapp.repository.TaskRepositoryImpl
import org.koin.dsl.module

val taskRepositoryModule = module {
    factory<TaskRepository> {
        TaskRepositoryImpl(
            taskDataSource = get<TaskDataSource>()
        )
    }
}