package sam.teatime.db.entities

import android.arch.persistence.room.*

@Entity(tableName = "teas")
data class Tea(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String
)

@Entity(tableName = "infusions", primaryKeys = ["teaId", "index"])
data class Infusion(
        val teaId: Int,
        val index: Int,
        val time: Long
)

class TeaAndInfusions(@Embedded val tea: Tea) {
    @Relation(parentColumn = "id", entityColumn = "teaId", entity = Infusion::class)
    var infusions: List<Infusion> = emptyList()
}
