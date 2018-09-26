package sam.teatime.db.repositories

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import sam.teatime.db.dao.PotDao
import sam.teatime.db.entities.Pot
import sam.teatime.db.entities.Tea

class PotRepository (private val potDao: PotDao) {
    val allPots: LiveData<List<Pot>> = potDao.getAll()

    @WorkerThread
    fun insert(pot: Pot) {
        potDao.insert(pot)
    }
}