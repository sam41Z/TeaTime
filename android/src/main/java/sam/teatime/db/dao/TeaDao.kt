package sam.teatime.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import sam.teatime.db.entities.Tea

@Dao
interface TeaDao {
    @Insert
    fun insert(tea: Tea)

    @Update
    fun update(tea: Tea)

    @Query("SELECT * FROM teas ORDER BY name ASC")
    fun getAll(): LiveData<List<Tea>>

    @Query("DELETE FROM teas")
    fun deleteAll()
}