package com.example.rappimoviewer.view

import android.app.ActivityOptions
import android.content.Intent
import com.example.rappimoviewer.models.Movie

interface SearchMovieViewImpl: LoadViewImpl {

    fun setUpViews()

    fun onReadySortedMovies(movies: ArrayList<Movie>)

    fun showError()

    fun startActivityDetail(intent: Intent)

    fun startActivityDetailTransition(intent: Intent, options: ActivityOptions)

}