package sam.teatime.db.repository

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import sam.teatime.db.dao.TeaDao
import sam.teatime.db.entities.Tea


class TeaRepository (private val teaDao: TeaDao) {
    val allTeas: LiveData<List<Tea>> = teaDao.getAll()

    @WorkerThread
    suspend fun insert(tea: Tea) {
        teaDao.insert(tea)
    }
}