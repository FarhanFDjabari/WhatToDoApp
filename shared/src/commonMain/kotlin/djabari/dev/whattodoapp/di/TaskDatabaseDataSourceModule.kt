package djabari.dev.whattodoapp.di

import djabari.dev.whattodoapp.data.datasource.TaskDataSource
import djabari.dev.whattodoapp.data.datasource.TaskDatabaseDataSource
import djabari.dev.whattodoapp.data.sqldelight.Database
import org.koin.dsl.module

val TaskDatabaseDataSourceModule = module {
    factory<TaskDataSource> {
        TaskDatabaseDataSource(
            database = get<Database>()
        )
    }
}