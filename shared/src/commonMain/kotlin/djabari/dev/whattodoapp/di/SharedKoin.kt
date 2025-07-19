package djabari.dev.whattodoapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initSharedKoin(config : KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(databaseModule)
        modules(TaskDatabaseDataSourceModule)
        modules(taskRepositoryModule)
        modules(taskViewModelModule)
    }
}