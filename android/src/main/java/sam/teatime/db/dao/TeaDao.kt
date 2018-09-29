package sam.teatime.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import sam.teatime.db.entities.Infusion
import sam.teatime.db.entities.Tea
import sam.teatime.db.entities.TeaWithInfusions

@Dao
interface TeaDao {
    @Insert
    fun insert(tea: Tea)

    @Update
    fun update(tea: Tea)

    @Insert
    fun insert(Infusion: Infusion)

    @Update
    fun update(Infusion: Infusion)

    @Query("DELETE FROM teas")
    fun deleteAll()

    @Query("DELETE FROM infusions WHERE teaId = :teaId")
    fun deleteInfusionsForTeaId(teaId: Int)

    @Query("SELECT * FROM teas WHERE id = :teaId")
    fun getByTeaId(teaId: Int): LiveData<List<TeaWithInfusions>>

    @Query("SELECT * FROM teas")
    fun getAll(): LiveData<List<TeaWithInfusions>>
}