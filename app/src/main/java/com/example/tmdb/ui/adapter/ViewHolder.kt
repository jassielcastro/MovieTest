package com.example.tmdb.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    interface OnClickListener {
        fun onItemClick(view: View, position: Int)
    }

}