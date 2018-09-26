package sam.teatime.activities

import android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_pots.*
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.help.view.*
import org.ligi.compat.HtmlCompat
import org.ligi.kaxt.getAlarmManager
import org.ligi.kaxt.setExactAndAllowWhileIdleCompat
import org.ligi.kaxt.startActivityFromClass
import sam.teatime.R
import sam.teatime.model.State
import sam.teatime.receiver.TimerReceiver
import sam.teatime.timer.Timer
import sam.teatime.viewholders.TeaViewHolder
import sam.teatime.viewmodels.TeaViewModel


class TimerActivity : AppCompatActivity() {

    var handler = Handler()

    val pendingTimerReceiver: PendingIntent by lazy {
        val intent = Intent(applicationContext, TimerReceiver::class.java)
        PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    var defaultTextColor: Int = 0

    private var pauseState = !Timer.isPaused()
    private var time = 180

    val updater = object : Runnable {
        override fun run() {
            val remaining = 300 - Timer.elapsedSeconds()

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
            timer_sec.text = Math.abs(remaining % 60).toString() + "s"

            tea_progress.max = 300
            tea_progress.progress = Timer.elapsedSeconds().toInt()

            if (pauseState != Timer.isPaused()) {

                pauseState = Timer.isPaused()

                if (pauseState) {
                    getAlarmManager().cancel(pendingTimerReceiver)
                } else {
                    if (remaining > 0) {
                        val triggerAtMillis = SystemClock.elapsedRealtime() + remaining * 1000
                        getAlarmManager().setExactAndAllowWhileIdleCompat(ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingTimerReceiver)
                    }
                }

                val nextDrawable = if (pauseState) R.drawable.vectalign_vector_drawable_start else R.drawable.vectalign_vector_drawable_end
                val drawable = VectorDrawableCompat.create(resources, nextDrawable, theme)
                play_pause.setImageDrawable(drawable)

            }
            invalidateOptionsMenu()

            handler.postDelayed(this, 50)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        defaultTextColor = timer_min.currentTextColor

        play_pause.setOnClickListener {
            Timer.togglePause()
        }

        fabTimer.setOnClickListener {
            startActivityFromClass(TeaActivity::class.java)
        }
    }

    override fun onPause() {
        handler.removeCallbacks(updater)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.resetTime).isVisible = Timer.elapsedSeconds() > 0
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.resetTime -> {
            Timer.resetAndPause()
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
        handler.post(updater)
        val teaViewModel = ViewModelProviders.of(this).get(TeaViewModel::class.java)
        teaViewModel.allTeas.observe(this, Observer { teas ->
            teas?.find { tea -> tea.tea.id == State.lastSelectedTeaId }?.let {
                TeaViewHolder(window.decorView).bind(it)
            }
        })
    }
}
