package sam.teatime.timer

import android.arch.persistence.room.Room
import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.experimental.CoroutineScope
import sam.teatime.State
import sam.teatime.db.TeaRoomDatabase

class Timer {

    var startTime = SystemClock.elapsedRealtime()
    var pauseTime: Long? = SystemClock.elapsedRealtime()

    fun resetAndPause() {
        pauseTime = SystemClock.elapsedRealtime()
        startTime = SystemClock.elapsedRealtime()
    }

    fun togglePause() {
        if (pauseTime == null) {
            pauseTime = SystemClock.elapsedRealtime()
        } else {
            startTime = SystemClock.elapsedRealtime() - (pauseTime!! - startTime)
            pauseTime = null

        }
    }

    fun elapsedSeconds(): Long {
        val baseTime = pauseTime ?: SystemClock.elapsedRealtime()
        return (baseTime - startTime) / 1000
    }

    fun isPaused() = pauseTime != null

    companion object {
        @Volatile
        private var INSTANCE: Timer? = null

        fun getTimer(): Timer {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                // Create database here
                val instance = Timer()
                INSTANCE = instance
                return instance
            }
        }
    }
}