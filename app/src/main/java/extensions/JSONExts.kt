package extensions

import com.google.gson.Gson
import org.json.JSONArray
import java.io.Serializable

interface JSONConvertable : Serializable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

inline fun <reified T : JSONConvertable> JSONArray.toArrayList(): ArrayList<T> {
    val array: ArrayList<T> = arrayListOf()
    for (i in 0 until this.length()) {
        array.add(this.getJSONObject(i).toString().toObject())
    }
    return array
}

