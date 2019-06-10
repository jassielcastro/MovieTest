package extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.formateDate(newFormat: String = "MMM d, yyyy", currentFormat: String = "yyyy-mm-dd") : String {
    return try {
        val sdf = SimpleDateFormat(newFormat, Locale.getDefault())
        val date = SimpleDateFormat(currentFormat, Locale.getDefault()).parse(this)
        sdf.format(date.time)
    } catch (e: Exception) {
        this
    }
}