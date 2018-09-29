package sam.teatime.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import sam.teatime.db.TeaRoomDatabase
import sam.teatime.db.entities.Infusion
import sam.teatime.db.entities.Tea
import sam.teatime.db.entities.TeaWithInfusions
import sam.teatime.db.repositories.TeaRepository
import kotlin.coroutines.experimental.CoroutineContext

class TeaViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: TeaRepository
    val allTeas: LiveData<List<TeaWithInfusions>>

    init {
        val teaDao = TeaRoomDatabase.getDatabase(application, scope).teaDao()
        repository = TeaRepository(teaDao)
        allTeas = repository.allTeas
    }

    fun insert(tea: Tea) = scope.launch(Dispatchers.IO) {
        repository.insert(tea)
    }

    fun update(tea: Tea) = scope.launch(Dispatchers.IO) {
        repository.update(tea)
    }

    fun update(teaId: Int, infusions: List<Infusion>) = scope.launch(Dispatchers.IO) {
        deleteInfusionsForTeaId(teaId).join()
        infusions.forEach { insert(it) }
    }

    fun insert(infusion: Infusion) = scope.launch(Dispatchers.IO) {
        repository.insert(infusion)
    }

    fun deleteInfusionsForTeaId(teaId: Int) = scope.launch(Dispatchers.IO) {
        repository.deleteInfusionsForTeaId(teaId)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}