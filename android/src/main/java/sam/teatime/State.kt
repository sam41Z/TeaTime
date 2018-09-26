package sam.teatime

import com.chibatching.kotpref.KotprefModel

object State : KotprefModel() {
    var lastSelectedTeaId by intPref()
    var lastSelectedInfusionIndex by intPref()
}