package sam.teatime.activities

import android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.help.view.*
import org.ligi.compat.HtmlCompat
import org.ligi.kaxt.getAlarmManager
import org.ligi.kaxt.setExactAndAllowWhileIdleCompat
import org.ligi.kaxt.startActivityFromClass
import sam.teatime.R
import sam.teatime.State
import sam.teatime.db.entities.TeaWithInfusions
import sam.teatime.receivers.TimerReceiver
import sam.teatime.timer.Timer
import sam.teatime.services.TimerNotificationService
import sam.teatime.viewmodels.TeaViewModel


class TimerActivity : AppCompatActivity() {

    var handler = Handler()

    val pendingTimerReceiver: PendingIntent by lazy {
        val intent = Intent(applicationContext, TimerReceiver::class.java)
        PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    var defaultTextColor: Int = 0

    private var pauseState = !Timer.getTimer().isPaused()
    private var time = 180
    private var maxInfusions = 0
    private var tea: TeaWithInfusions? = null

    private val updater = object : Runnable {
        override fun run() {
            val remaining = time - Timer.getTimer().elapsedSeconds()

            val prefix = if (remaining < 0) {
                timerMin.setTextColor(Color.RED)
                timerSec.setTextColor(Color.RED)
                "-"
            } else {
                timerMin.setTextColor(defaultTextColor)
                timerSec.setTextColor(defaultTextColor)
                ""
            }

            timerMin.text = prefix + Math.abs(remaining / 60).toString() + "m"
            val zeroSecPrefix = if (Math.abs((remaining % 60)) < 10) "0" else ""
            timerSec.text = zeroSecPrefix + Math.abs(remaining % 60).toString() + "s"

            teaProgress.max = time
            teaProgress.progress = Timer.getTimer().elapsedSeconds().toInt()

            if (pauseState != Timer.getTimer().isPaused()) {

                pauseState = Timer.getTimer().isPaused()

                if (pauseState) {
                    getAlarmManager().cancel(pendingTimerReceiver)
                } else if (remaining > 0) {
                    val now = SystemClock.elapsedRealtime()
                    val triggerAtMillis = now + remaining * 1000
                    getAlarmManager().setExactAndAllowWhileIdleCompat(ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingTimerReceiver)
                    Log.d("ALARM", "Alarm scheduled at $triggerAtMillis now: $now remaining: $remaining")
                }
                changeDrawableWithAnimation()
            }
            invalidateOptionsMenu()

            handler.postDelayed(this, 50)
        }

        private fun changeDrawableWithAnimation() {
            val drawable = getDrawable(if (pauseState) R.drawable.vectalign_animated_vector_drawable_end_to_start else R.drawable.vectalign_animated_vector_drawable_start_to_end) as AnimatedVectorDrawable
            playPause.setImageDrawable(drawable)
            drawable.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        defaultTextColor = timerMin.currentTextColor

        playPause.setOnClickListener {
            Timer.getTimer().togglePause()
        }

        previousInfusion.setOnClickListener {
            Timer.getTimer().resetAndPause()
            State.lastSelectedInfusionIndex = Math.max(0, State.lastSelectedInfusionIndex - 1)
            updateView()
        }
        nextInfusion.setOnClickListener {
            Timer.getTimer().resetAndPause()
            State.lastSelectedInfusionIndex = Math.min(maxInfusions - 1, State.lastSelectedInfusionIndex + 1)
            updateView()
        }

        fabTimer.setOnClickListener {
            startActivityFromClass(TeaActivity::class.java)
        }
    }

    override fun onPause() {
        handler.removeCallbacks(updater)
        if (!Timer.getTimer().isPaused()) {
            val intent = Intent(this, TimerNotificationService::class.java)
            intent.putExtra("time", time)
            startService(intent)
        }
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.timer, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.resetTime).isVisible = Timer.getTimer().elapsedSeconds() > 0
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.resetTime -> {
            Timer.getTimer().resetAndPause()
            true
        }
        R.id.menuInfo -> {
            val view = layoutInflater.inflate(R.layout.help, null, false)
            view.helpText.text = HtmlCompat.fromHtml(getString(R.string.help))
            view.helpText.movementMethod = LinkMovementMethod()
            AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        stopService(Intent(this, TimerNotificationService::class.java))

        val teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
        teaViewModel.allTeas.observe(this, Observer { teas ->
            teas?.find { tea -> tea.tea.id == State.lastSelectedTeaId }?.let {
                tea = it
                updateView()
                handler.post(updater)
            }
        })
    }

    private fun updateView() {
        val tea = tea
        if (tea != null && tea.infusions.isNotEmpty()) {
            maxInfusions = tea.infusions.size
            val infusionIndex = Math.min(State.lastSelectedInfusionIndex, maxInfusions - 1)
            time = tea.infusions[infusionIndex].time
            teaName.text = tea.tea.name
            teaInfusion.text = "Infusion ${infusionIndex + 1}"
            teaMins.text = "${time / 60}m"
            tea_secs.text = "${time % 60}s"
        }
    }
}

