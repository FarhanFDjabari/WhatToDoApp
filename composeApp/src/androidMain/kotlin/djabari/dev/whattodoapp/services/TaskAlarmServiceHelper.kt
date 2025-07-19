package djabari.dev.whattodoapp.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import djabari.dev.whattodoapp.data.model.Task
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class TaskAlarmServiceHelper(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @OptIn(ExperimentalTime::class)
    fun scheduleAlarm(task: Task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d("TaskAlarm", "scheduleAlarm: alarm can't schedule exact alarm")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            }
        }

        val triggerTime = Instant
            .fromEpochMilliseconds(task.dueDate)
            .minus(value = 10L, unit = DateTimeUnit.MINUTE)
            .toEpochMilliseconds()

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", task.id.toInt())
            putExtra("alarm_title", "What To Do Reminder")
            putExtra("alarm_content", task.taskName)
            putExtra("alarm_duedate", task.dueDate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("TaskAlarm", "scheduleAlarm: Scheduled alarm for task ${task.taskName} at $triggerTime")

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback to inexact alarm if exact alarm permission is not granted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(task: Task) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", task.id.toInt())
            putExtra("alarm_title", "What To Do Reminder")
            putExtra("alarm_content", "${task.taskName} due is end in 10 minutes!")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}