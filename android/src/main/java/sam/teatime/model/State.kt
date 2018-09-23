package sam.teatime.model

import com.chibatching.kotpref.KotprefModel

object State : KotprefModel() {

    var lastSelectedTeaName by stringPref()
}