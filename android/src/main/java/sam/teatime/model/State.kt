package sam.teatime.model

import com.chibatching.kotpref.KotprefModel

object State : KotprefModel() {
    var lastSelectedTeaId by intPref()
    var lastSelectedInfusionIndex by intPref()
}