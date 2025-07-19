package djabari.dev.whattodoapp.di

import djabari.dev.whattodoapp.services.TaskAlarmServiceHelper
import org.koin.dsl.module

val appModule = module {
    single<TaskAlarmServiceHelper> {
        TaskAlarmServiceHelper(context = get())
    }
}