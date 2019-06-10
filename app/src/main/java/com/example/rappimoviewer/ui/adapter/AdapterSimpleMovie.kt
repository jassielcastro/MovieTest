package com.example.rappimoviewer.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rappimoviewer.R
import com.example.rappimoviewer.api.ApiClient
import com.example.rappimoviewer.extensions.inflateView
import com.example.rappimoviewer.extensions.roundAndString
import com.example.rappimoviewer.models.Movie
import kotlinx.android.synthetic.main.item_simple_movie.view.*

class AdapterSimpleMovie(
    private val movies: ArrayList<Movie?>,
    private val click: ViewHolder.OnClickListener,
    private val clickSearch: (() -> Unit)?
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val EMPTY_MOVIES = 0
        private const val SEARCH_MOVIES = 1
        private const val FULL_MOVIES = 2
    }

    override fun getItemViewType(position: Int): Int {
        val movie = movies[position]
        return if (movie != null && movie.id != -1) {
            FULL_MOVIES
        } else if (movie != null && movie.id == -1) {
            SEARCH_MOVIES
        } else {
            EMPTY_MOVIES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            EMPTY_MOVIES -> ViewHolder(parent.inflateView(R.layout.item_error_movie))
            SEARCH_MOVIES -> ViewHolder(parent.inflateView(R.layout.items_search_more))
            else -> ViewHolder(parent.inflateView(R.layout.item_simple_movie))
        }
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        if (holder.itemViewType == FULL_MOVIES) {
            showMovieInformation(holder, movie!!)
        }  else if (holder.itemViewType == SEARCH_MOVIES) {
            holder.itemView.setOnClickListener {
                clickSearch?.invoke()
            }
        }
    }

    private fun showMovieInformation(holder: ViewHolder, movie: Movie) {
        holder.itemView.txtTitleMovie.text = movie.title
        holder.itemView.txtRating.text = movie.vote_average.roundAndString()
        if (movie.poster_path != null && movie.poster_path!!.isNotEmpty()) {
            val ctx = holder.itemView.context
            ApiClient(ctx)
                .loadImageUrl(holder.itemView.imgSimpleMovie, movie.poster_path!!, "w500")
        } else {
            holder.itemView.imgSimpleMovie.showErrorImage()
        }
        holder.itemView.setOnClickListener {
            click.onItemClick(it, holder.adapterPosition)
        }
    }

}