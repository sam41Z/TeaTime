package sam.teatime.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.Main
import sam.teatime.db.TeaRoomDatabase
import sam.teatime.db.entities.Pot
import sam.teatime.db.repositories.PotRepository
import kotlin.coroutines.experimental.CoroutineContext

class PotViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: PotRepository
    val allPots: LiveData<List<Pot>>

    init {
        val potDao = TeaRoomDatabase.getDatabase(application, scope).potDao()
        repository = PotRepository(potDao)
        allPots = repository.allPots
    }

    fun insert(pot: Pot) = scope.launch(Dispatchers.IO) {
        repository.insert(pot)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}