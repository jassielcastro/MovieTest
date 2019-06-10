package com.example.tmdb.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tmdb.R
import extensions.inflateView
import kotlinx.android.synthetic.main.item_genres.view.*

class AdapterGenres(private val genres: ArrayList<String>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder = ViewHolder(parent.inflateView(R.layout.item_genres))

    override fun getItemCount(): Int = genres.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.txtGenre.text = genres[position]
    }
}