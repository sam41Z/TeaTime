package sam.teatime.db.entities

import android.arch.persistence.room.Entity

@Entity(tableName = "pots")
class Pot {
    var infusionIndex: Int = 0
}