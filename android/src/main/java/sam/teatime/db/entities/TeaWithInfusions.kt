package sam.teatime.db.entities

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation

data class TeaWithInfusions(
        @Embedded
        val tea: Tea
) {
    @Relation(parentColumn = "id", entityColumn = "teaId", entity = Infusion::class)
    var infusions: List<Infusion> = emptyList()
}