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
import com.example.rappimoviewer.models.Movie
import com.example.rappimoviewer.ui.activities.MovieDetailActivity
import com.example.rappimoviewer.ui.activities.SearchActivity
import com.example.rappimoviewer.view.MoviesListViewImpl
import customviews.ImageMovieView

class MoviesListPresenter(private var view: MoviesListViewImpl, private var ctx: Context) : BasePresenter {

    override fun createView() {
        view.setUpViews()
        hideAllViews()
        loadMovies()
    }

    private fun loadMovies() {
        loadUpcomingMovies()
        loadTopRateMovies()
        loadPopulateMovies()
    }

    private fun loadUpcomingMovies() {
        view.showLoadingUpComing()
        ApiClient(ctx)
            .getUpComingMovies(1) { _, upcoming, error ->
                view.hideLoadingUpComing()
                if (error == null) {
                    view.onReadyUpComingMovies(upcoming)
                } else {
                    view.onReadyUpComingMovies(arrayListOf())
                }
            }
    }

    private fun loadTopRateMovies() {
        view.showLoadingTopRate()
        ApiClient(ctx)
            .getTopRateMovies(1) { _, topRate, error ->
                view.hideLoadingTopRate()
                if (error == null) {
                    view.onReadyTopRateMovies(topRate)
                } else {
                    view.onReadyTopRateMovies(arrayListOf())
                }
            }
    }

    private fun loadPopulateMovies() {
        view.showLoadingPopulate()
        ApiClient(ctx)
            .getPopularMovies(1) { _, populate, error ->
                view.hideLoadingPopulate()
                if (error == null) {
                    view.onReadyPopulateMovies(populate)
                } else {
                    view.onReadyPopulateMovies(arrayListOf())
                }
            }
    }

    override fun hideAllViews() {
        view.hideViews()
    }

    fun handleClickMovies(movie: Movie?, act: Activity) {
        if (movie != null) {
            val intent = Intent(act, MovieDetailActivity::class.java)
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie.id)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(act)
                view.startActivityDetailTransition(intent, options)
            } else {
                view.startActivityDetail(intent)
            }
        }
    }

    fun requestSearchView(act: Activity, cardSearch: CardView) {
        val intent = Intent(act, SearchActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options =
                ActivityOptions.makeSceneTransitionAnimation(act, cardSearch, SearchActivity.CARD_TRANSITION_NAME)
            view.startActivityDetailTransition(intent, options)
        } else {
            view.startActivityDetail(intent)
        }
    }

    fun requestSearchViewCategory(act: Activity, category: Int) {
        val intent = Intent(act, SearchActivity::class.java)
        intent.putExtra(SearchActivity.EXTRA_CATEGORY_SEARCH, category)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(act)
            view.startActivityDetailTransition(intent, options)
        } else {
            view.startActivityDetail(intent)
        }
    }

}