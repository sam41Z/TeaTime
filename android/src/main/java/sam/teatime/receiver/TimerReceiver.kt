package sam.teatime.receiver

import android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.SystemClock
import android.support.v4.app.NotificationCompat
import org.ligi.kaxt.getAlarmManager
import org.ligi.kaxt.getNotificationManager
import sam.teatime.activities.TimerActivity
import sam.teatime.R
import java.util.*
import android.os.PowerManager


class TimerReceiver : BroadcastReceiver() {

    private val notificationId by lazy { Random().nextInt() }
    private val channelId = "alarm"


    override fun onReceive(context: Context, intent: Intent?) {
        createChannel(context)

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or
                PowerManager.ACQUIRE_CAUSES_WAKEUP or
                PowerManager.ON_AFTER_RELEASE, "mysms1")
        wakeLock.acquire(1000)

        val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, TimerActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT)

        val cancelIntent = Intent(context, CancelNotificationReceiver::class.java)
        cancelIntent.putExtra("id", notificationId)
        cancelIntent.putExtra("incrementInfusion", true)
        val pendingCancelIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setContentText(context.getString(R.string.notification_text_tea_ready))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .addAction(NotificationCompat.Action.Builder(R.drawable.ic_notification, "done", pendingCancelIntent).build())
                .build()

        context.getNotificationManager().notify(notificationId, notification)
        createAndInstallCancellation(context)
        wakeLock.release()
    }

    private fun createAndInstallCancellation(context: Context) {

        val triggerAtMillis = SystemClock.elapsedRealtime() + 10 * 60 * 1000

        val cancelIntent = Intent(context, CancelNotificationReceiver::class.java)
        cancelIntent.putExtra("id", notificationId)
        val pendingCancelIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        context.getAlarmManager().set(ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingCancelIntent)
    }

    private fun createChannel(context: Context) {
        val channel = NotificationChannel(channelId, "Tea Time", NotificationManager.IMPORTANCE_HIGH)
        channel.description = "Notifies when tea is ready"
        channel.enableLights(true)
        channel.lightColor = Color.CYAN
        channel.setShowBadge(true)
        channel.vibrationPattern = longArrayOf(0, 1000, 1000, 1000, 1000, 1000)
        context.getNotificationManager().createNotificationChannel(channel)
    }

}