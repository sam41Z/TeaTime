package sam.teatime.db.repositories

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import sam.teatime.db.dao.TeaDao
import sam.teatime.db.entities.Infusion
import sam.teatime.db.entities.Tea
import sam.teatime.db.entities.TeaWithInfusions


class TeaRepository (private val teaDao: TeaDao) {
    val allTeas: LiveData<List<TeaWithInfusions>> = teaDao.getAll()

    @WorkerThread
    fun insert(tea: Tea) {
        teaDao.insert(tea)
    }

    @WorkerThread
    fun update(tea: Tea) {
        teaDao.update(tea)
    }

    @WorkerThread
    fun insert(infusion: Infusion) {
        teaDao.insert(infusion)
    }

    @WorkerThread
    fun deleteInfusionsForTeaId(teaId: Int) {
        teaDao.deleteInfusionsForTeaId(teaId)
    }
}