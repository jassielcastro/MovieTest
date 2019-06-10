package extensions

import android.content.res.Resources

val Int.dp: Int
    get() = this

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Double.roundAndString() : String {
    return (Math.floor(this * 100) / 100).toString()
}