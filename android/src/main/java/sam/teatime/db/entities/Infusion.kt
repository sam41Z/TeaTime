package sam.teatime.db.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey

@Entity(
        tableName = "infusions",
        primaryKeys = ["teaId", "index"],
        foreignKeys = [
            ForeignKey(
                    entity = Tea::class,
                    parentColumns = ["id"],
                    childColumns = ["teaId"],
                    onDelete = ForeignKey.CASCADE)]
)
data class Infusion(
        val teaId: Int,
        val index: Int,
        var time: Int = 180
)