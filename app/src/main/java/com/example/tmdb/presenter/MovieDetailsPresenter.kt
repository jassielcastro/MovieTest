package com.example.tmdb.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.example.tmdb.R
import net.api.ApiClient
import net.api.ApiRoute
import extensions.formateDate
import extensions.roundAndString
import com.example.tmdb.models.MovieDetail
import com.example.tmdb.view.MovieDetailsViewImpl
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class MovieDetailsPresenter(private val view: MovieDetailsViewImpl, private val ctx: Context) : BasePresenter {

    private val targetImage: TargetImage by lazy {
        TargetImage()
    }

    override fun createView() {
        view.setUpViews()
        hideAllViews()
    }

    override fun hideAllViews() {
        view.hideViews()
    }

    fun loadMovieDetails(movieId: Int) {
        view.showLoading()
        ApiClient(ctx)
            .getMovieById(movieId) { detail, genres ->
                view.hideLoading()
                if (detail != null) {
                    showMovieDetails(detail)
                    view.showGenres(genres)
                } else {
                    view.showErrorMessage(ctx.getString(R.string.txt_invalid_movie_id))
                }
            }
    }

    private fun showMovieDetails(detail: MovieDetail) {
        if (detail.backdrop_path != null && detail.backdrop_path!!.isNotEmpty()) {
            view.updateBackgroundImage(detail.backdrop_path!!)
        } else if (detail.poster_path != null && detail.poster_path!!.isNotEmpty()) {
            view.updateBackgroundImage(detail.poster_path!!)
        } else {
            view.showErrorImage()
        }
        view.updateReleaseDate(detail.release_date.formateDate())
        view.updateTitle(detail.title)
        view.updateOverview(detail.overview)
        view.updateOriginalTitle(detail.original_title)
        view.updateTagline(detail.tagline ?: "")
        view.updateOriginaLanguage(detail.original_language)
        view.updatePromotionalPage(detail.homepage ?: "")

        view.updateRating(detail.vote_average.roundAndString(), detail.vote_count, detail.vote_average)
    }

    fun loadVideos(movieId: Int) {
        if (movieId != -1) {
            ApiClient(ctx)
                .getVideoMovies(movieId) { videos ->
                    if (videos.isNotEmpty()) {
                        view.onReadyVideoList(videos)
                    } else {
                        view.showErrorMessage("No se encontraron videos disponibles...")
                    }
                }
        }
    }

    fun renderImage(imageUrl: String, width: Int, height: Int) {
        val route = ApiRoute.DownloadImage(imageUrl, "w1280")
        Picasso.get()
            .load(route.fullUrl)
            .resize(width, height)
            .into(targetImage)
    }

    inner class TargetImage : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            view.showLoadingImage()
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            view.hideLoadingImage()
            view.showErrorImage()
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (bitmap != null) {
                view.hideLoadingImage()
                view.showMovieImage(bitmap)
            } else {
                view.hideLoadingImage()
                view.showErrorImage()
            }
        }
    }
}