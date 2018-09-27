package sam.teatime.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import org.ligi.kaxt.getNotificationManager
import sam.teatime.R
import sam.teatime.activities.TimerActivity
import java.util.*

class TimerNotificationService : Service() {
    private val handler = Handler()

    private val notificationId by lazy { Random().nextInt() }
    private val channelId = "notification"
    private var notification: NotificationCompat.Builder? = null
    private var time: Int = 180

    val uppdater = object : Runnable {
        override fun run() {
            notification?.setProgress(time, Timer.elapsedSeconds().toInt(), false)
            val remaining = time - Timer.elapsedSeconds().toInt()
            val mins = remaining / 60
            val secs = remaining % 60
            notification?.setContentText("${mins}m${secs}s remaining")
            startForeground(notificationId, notification?.build())
            handler.postDelayed(this, 1000)
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(channelId, "Tea Time", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Notifies when tea is ready"
        channel.enableVibration(false)
        applicationContext.getNotificationManager().createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        createChannel()
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0,
                Intent(applicationContext, TimerActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(application.getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        if (intent == null) return
        time = intent.getIntExtra("time", 180)
        handler.post(uppdater)
    }

    override fun onDestroy() {
        handler.removeCallbacks(uppdater)
    }
}
