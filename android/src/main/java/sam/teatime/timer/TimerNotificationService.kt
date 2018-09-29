package sam.teatime.timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
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
    private val timer: Timer = Timer.getTimer()

    private val updater = object : Runnable {
        override fun run() {
            notification?.setProgress(time, timer.elapsedSeconds().toInt(), false)
            val remaining = time - timer.elapsedSeconds().toInt()
            val mins = if (remaining / 60 > 0) "${(remaining / 60)}:" else ""
            val secs = (if (!mins.isEmpty() && remaining % 60 < 10) "0" else "") + (remaining % 60)
            notification?.setContentText("$mins$secs")
            startForeground(notificationId, notification?.build())
            handler.postDelayed(this, 1000)
            Log.d("SERVICE", "updated time to $mins$secs")
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
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
                .setSmallIcon(R.drawable.ic_pot)
                .setContentTitle(getString(R.string.notification_text_tea_ready_in))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            time = intent.getIntExtra("time", 180)
            handler.post(updater)
        }
        return START_STICKY
    }

    override fun onStart(intent: Intent?, startId: Int) {

    }

    override fun onDestroy() {
        handler.removeCallbacks(updater)
    }
}
