package sam.teatime

import java.util.*

fun IntRange.random() = Random().nextInt((endInclusive + 1) - start) + start