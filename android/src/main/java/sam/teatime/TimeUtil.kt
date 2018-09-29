package sam.teatime


object TimeUtil {

    fun secondsToString(time: Int): String {
        val min = Math.abs(time / 60).toString()
        val zeroSecPrefix = if (Math.abs((time % 60)) < 10) "0" else ""
        val sec = zeroSecPrefix + Math.abs(time % 60).toString()
        return "$min:$sec"
    }

    fun parseString(string: String): Int {
        val units = string.split(":")
        return units[0].toInt() * 60 + units[1].toInt()
    }
}