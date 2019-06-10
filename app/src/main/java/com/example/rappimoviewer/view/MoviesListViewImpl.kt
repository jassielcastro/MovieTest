package com.example.rappimoviewer.view

import android.app.ActivityOptions
import android.content.Intent
import com.example.rappimoviewer.models.Movie

interface MoviesListViewImpl {

    fun setUpViews()

    fun onReadyUpComingMovies(moviesList: ArrayList<Movie>)

    fun onReadyTopRateMovies(moviesList: ArrayList<Movie>)

    fun onReadyPopulateMovies(moviesList: ArrayList<Movie>)

    fun showLoadingUpComing()

    fun showLoadingTopRate()

    fun showLoadingPopulate()

    fun hideLoadingUpComing()

    fun hideLoadingTopRate()

    fun hideLoadingPopulate()

    fun hideViews()

    fun startActivityDetail(intent: Intent)

    fun startActivityDetailTransition(intent: Intent, options: ActivityOptions)
}