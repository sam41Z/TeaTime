package sam.teatime.db.entities

import android.arch.persistence.room.*

@Entity(tableName = "teas")
data class Tea(@PrimaryKey val id: Int, val name: String)
