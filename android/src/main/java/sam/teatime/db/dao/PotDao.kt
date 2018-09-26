package sam.teatime.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import sam.teatime.db.entities.Pot

@Dao
interface PotDao {
    @Insert
    fun insert(pot: Pot)

    @Update
    fun update(pot: Pot)

    @Query("SELECT * FROM pots WHERE active = 1")
    fun getAll(): LiveData<List<Pot>>

    @Query("DELETE FROM pots")
    fun deleteAll()
}