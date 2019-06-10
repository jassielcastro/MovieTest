package com.example.rappimoviewer.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.example.rappimoviewer.R
import com.example.rappimoviewer.models.Video
import android.app.ActivityOptions
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler

fun <T : Activity> Activity.launch(activity: Class<T>, terminate: Boolean = false, options: ActivityOptions? = null) {
    val i = Intent(this, activity)
    this.launch(i, terminate, options)
}

fun Activity.launch(i: Intent, terminate: Boolean = false, options: ActivityOptions? = null) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && options != null) {
        startActivity(i, options.toBundle())
    } else {
        startActivity(i)
    }

    if (terminate) {
        this.finish()
    }
    this.overridePendingTransition(R.anim.left_in, R.anim.left_out)
}

fun Activity.showStringDialogList(title: String, list: ArrayList<Video>, completion: (selection: String) -> Unit) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
        .setItems(list.map { it.name }.toTypedArray()) { dialog, selection ->
            dialog.dismiss()
            completion.invoke(list[selection].key)
        }
        .setCancelable(true)
        .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
    val dialog = builder.create()
    dialog.show()
}

fun Activity.openYTVideo(key: String) {
    val url = "https://www.youtube.com/watch?v=$key"
    val uris = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uris)
    this.startActivity(intent)
}

fun delay(time: Long, completion: () -> Unit) {
    Handler().postDelayed({
        completion.invoke()
    }, time)
}

fun Activity.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager) {
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        networkInfo?.isConnected ?: false
    } else false
}