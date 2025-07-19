package djabari.dev.whattodoapp.di

import djabari.dev.whattodoapp.data.db.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val databaseDriverModule: Module = module {
    single<DriverFactory> {
        DriverFactory(context = get())
    }
}