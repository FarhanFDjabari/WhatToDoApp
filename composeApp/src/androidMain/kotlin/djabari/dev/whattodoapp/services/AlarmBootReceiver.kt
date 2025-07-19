package djabari.dev.whattodoapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import djabari.dev.whattodoapp.data.datasource.TaskDatabaseDataSource
import djabari.dev.whattodoapp.data.db.DriverFactory
import djabari.dev.whattodoapp.data.db.createDatabase
import djabari.dev.whattodoapp.repository.TaskRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class AlarmBootReceiver : BroadcastReceiver(), KoinComponent {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            intent.action == Intent.ACTION_PACKAGE_REPLACED) {

            try {
                val taskAlarmHelper = TaskAlarmServiceHelper(context)
                val databaseDriver = DriverFactory(context)
                val taskDatabase = createDatabase(databaseDriver)
                val taskDataSource = TaskDatabaseDataSource(taskDatabase)
                val taskRepository = TaskRepositoryImpl(taskDataSource)

                Log.d("TaskAlarm", "onReceive: Boot completed, scheduling alarms...")

                CoroutineScope(Dispatchers.IO).launch {
                    val tasks = taskRepository.getAllTasksByCompletedStatus(isCompleted = false)
                    tasks.forEach { task ->
                        if (!task.isOverdue) {
                            taskAlarmHelper.scheduleAlarm(task)
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}