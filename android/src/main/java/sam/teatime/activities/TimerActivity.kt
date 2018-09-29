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
import sam.teatime.receiver.TimerReceiver
import sam.teatime.timer.Timer
import sam.teatime.timer.TimerNotificationService
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
                timer_min.setTextColor(Color.RED)
                timer_sec.setTextColor(Color.RED)
                "-"
            } else {
                timer_min.setTextColor(defaultTextColor)
                timer_sec.setTextColor(defaultTextColor)
                ""
            }

            timer_min.text = prefix + Math.abs(remaining / 60).toString() + "m"
            val zeroSecPrefix = if (Math.abs((remaining % 60)) < 10) "0" else ""
            timer_sec.text = zeroSecPrefix + Math.abs(remaining % 60).toString() + "s"

            tea_progress.max = time
            tea_progress.progress = Timer.getTimer().elapsedSeconds().toInt()

            if (pauseState != Timer.getTimer().isPaused()) {

                pauseState = Timer.getTimer().isPaused()

                if (pauseState) {
                    getAlarmManager().cancel(pendingTimerReceiver)
                } else if (remaining > 0) {
                    val triggerAtMillis = SystemClock.elapsedRealtime() + remaining * 1000
                    getAlarmManager().setExactAndAllowWhileIdleCompat(ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingTimerReceiver)
                }
                changeDrawableWithAnimation()
            }
            invalidateOptionsMenu()

            handler.postDelayed(this, 50)
        }

        private fun changeDrawableWithAnimation() {
            val drawable = getDrawable(if (pauseState) R.drawable.vectalign_animated_vector_drawable_end_to_start else R.drawable.vectalign_animated_vector_drawable_start_to_end) as AnimatedVectorDrawable
            play_pause.setImageDrawable(drawable)
            drawable.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        defaultTextColor = timer_min.currentTextColor

        play_pause.setOnClickListener {
            Timer.getTimer().togglePause()
        }

        previous_infusion.setOnClickListener {
            Timer.getTimer().resetAndPause()
            State.lastSelectedInfusionIndex = Math.max(0, State.lastSelectedInfusionIndex - 1)
            updateView()
        }
        next_infusion.setOnClickListener {
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
            tea_name.text = tea.tea.name
            tea_infusion.text = "Infusion ${infusionIndex + 1}"
            tea_mins.text = "${time / 60}m"
            tea_secs.text = "${time % 60}s"
        }
    }
}

