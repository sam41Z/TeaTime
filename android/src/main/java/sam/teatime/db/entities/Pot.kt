package sam.teatime.db.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "pots")
class Pot(val teaId: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var infusionIndex: Int = 0
    var active: Boolean = true
}