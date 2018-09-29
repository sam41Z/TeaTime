package sam.teatime.db.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "teas")
data class Tea(@PrimaryKey val id: Int, val name: String, val description: String = "")
