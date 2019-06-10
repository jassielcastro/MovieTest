package com.example.tmdb.view

import android.graphics.Bitmap
import com.example.tmdb.models.Video

interface MovieDetailsViewImpl : LoadViewImpl {

    fun setUpViews()

    fun hideViews()

    fun showLoadingImage()

    fun hideLoadingImage()

    fun updateBackgroundImage(url: String)

    fun showErrorImage()

    fun showMovieImage(bmp: Bitmap)

    fun updateReleaseDate(date: String)

    fun updateRating(rate: String, countVotes: Int, percentStars: Double)

    fun updateTitle(title: String)

    fun showGenres(genders: ArrayList<String>)

    fun updateOverview(overView: String)

    fun updateOriginalTitle(original: String)

    fun updateTagline(tagline: String)

    fun updateOriginaLanguage(language: String)

    fun updatePromotionalPage(page: String)

    fun onReadyVideoList(videos: ArrayList<Video>)

}