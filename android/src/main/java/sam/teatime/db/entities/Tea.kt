package sam.teatime.db.entities

import android.arch.persistence.room.*

@Entity(tableName = "teas")
data class Tea(val name: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
