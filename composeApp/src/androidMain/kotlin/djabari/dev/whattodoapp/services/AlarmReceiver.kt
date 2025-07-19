package djabari.dev.whattodoapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.math.ceil
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AlarmReceiver : BroadcastReceiver() {
    @OptIn(ExperimentalTime::class)
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("alarm_id", -1)
        val alarmTitle = intent.getStringExtra("alarm_title") ?: "Todo Reminder"
        val taskName = intent.getStringExtra("alarm_content") ?: "Unknown Task"
        val taskDueDate = intent.getLongExtra("alarm_duedate", 0L)
        val triggerTimeDiff = taskDueDate - Clock.System.now().toEpochMilliseconds()
        val dueEndIn = when {
            triggerTimeDiff < 10_000 -> "now"
            triggerTimeDiff < 60_000 -> "in ${ceil(triggerTimeDiff / 1_000.0).toInt()} seconds"
            triggerTimeDiff < 120_000 -> "in a minute"
            triggerTimeDiff < 3_600_000 -> "in ${ceil(triggerTimeDiff / 60_000.0).toInt()} minutes"
            else -> "in ${ceil(triggerTimeDiff / 3_600_000.0).toInt()} hours"
        }
        val taskContent = "$taskName due is end $dueEndIn!"
        Log.d("TaskAlarm", "onReceive: Receive Alarm $id")

        val notificationHelper = NotificationServiceHelper(context)

        val pendingIntent = notificationHelper.getActionPendingIntent(id)

        notificationHelper.showNotification(
            id = id,
            title = alarmTitle,
            content = taskContent,
            pendingIntent = pendingIntent
        )
    }
}