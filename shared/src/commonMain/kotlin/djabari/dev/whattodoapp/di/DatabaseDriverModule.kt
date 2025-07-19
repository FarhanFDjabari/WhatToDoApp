package djabari.dev.whattodoapp.di

import djabari.dev.whattodoapp.data.db.DriverFactory
import djabari.dev.whattodoapp.data.db.createDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val databaseDriverModule: Module

val databaseModule = module {
    includes(databaseDriverModule)
    single {
        createDatabase(
            driverFactory = get<DriverFactory>()
        )
    }
}