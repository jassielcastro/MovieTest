package com.example.rappimoviewer.presenter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.cardview.widget.CardView
import com.example.rappimoviewer.R
import com.example.rappimoviewer.api.ApiClient
import com.example.rappimoviewer.extensions.isNetworkAvailable
import com.example.rappimoviewer.extensions.toArrayList
import com.example.rappimoviewer.models.Movie
import com.example.rappimoviewer.ui.activities.MovieDetailActivity
import com.example.rappimoviewer.ui.activities.SearchActivity
import com.example.rappimoviewer.view.SearchMovieViewImpl

class SearchMoviesPresenter(private val view: SearchMovieViewImpl) : BasePresenter {

    override fun createView() {
        view.setUpViews()
        hideAllViews()
    }

    override fun hideAllViews() {
        view.hideView()
    }

    fun searchMovieBy(userInput: String, act: Activity) {
        if (userInput.isNotEmpty()) {
            view.showLoading()
            ApiClient(act)
                .sortMoviesBy(userInput, 1, act.isNetworkAvailable()) { _, movies, error ->
                    view.hideLoading()
                    if (error == null && movies.isNotEmpty()) {
                        view.onReadySortedMovies(movies)
                    } else {
                        view.showError()
                    }
                }
        }
    }

    fun searchMovieByCategory(userInput: String, category: Int, act: Activity) {
        if (userInput.isNotEmpty()) {
            view.showLoading()
            when (category) {
                SearchActivity.CATEGORY_UPCOMING -> {
                    ApiClient(act)
                        .getUpComingMovies(1) { _, upcoming, _ ->
                            sendMoviesByCategory(upcoming, userInput)
                        }
                }
                SearchActivity.CATEGORY_TOP_RATE -> {
                    ApiClient(act)
                        .getTopRateMovies(1) { _, top_rate, _ ->
                            sendMoviesByCategory(top_rate, userInput)
                        }
                }
                SearchActivity.CATEGORY_POPULATE -> {
                    ApiClient(act)
                        .getPopularMovies(1) { _, populates, _ ->
                            sendMoviesByCategory(populates, userInput)
                        }
                }
            }
        }
    }

    private fun sendMoviesByCategory(movies: ArrayList<Movie>, userInput: String) {
        view.hideLoading()
        val sortedMovies = movies.filter { it.title.contains(userInput, true) }.toArrayList()
        if (sortedMovies.isNotEmpty()) {
            view.onReadySortedMovies(sortedMovies)
        } else {
            view.showError()
        }
    }

    fun handleClickMovies(v: View, movie: Movie?, act: Activity) {
        if (movie != null) {
            val intent = Intent(v.context, MovieDetailActivity::class.java)
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie.id)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(act)
                view.startActivityDetailTransition(intent, options)
            } else {
                view.startActivityDetail(intent)
            }
        }
    }

}