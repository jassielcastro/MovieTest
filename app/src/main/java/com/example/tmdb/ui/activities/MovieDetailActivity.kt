package com.example.tmdb.ui.activities

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.tmdb.R
import com.example.tmdb.models.Video
import com.example.tmdb.presenter.MovieDetailsPresenter
import com.example.tmdb.ui.adapter.AdapterGenres
import com.example.tmdb.view.MovieDetailsViewImpl
import customviews.ActivityBase
import extensions.*
import kotlinx.android.synthetic.main.activity_movie_detail.*

class MovieDetailActivity : ActivityBase(), MovieDetailsViewImpl, View.OnClickListener {

    companion object {
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
        const val IMAGE_TRANSITION_NAME = "TRANSITION_IMAGE"
    }

    private val presenter: MovieDetailsPresenter by lazy {
        MovieDetailsPresenter(this, this)
    }

    var movieId: Int = -1
    private val heightImage = 324.dp.toPx
    private val animationError = "not_found.json"

    private val genresList: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        presenter.createView()

        movieId = intent.getIntExtra(EXTRA_MOVIE, -1)
        if (movieId != -1) {
            presenter.loadMovieDetails(movieId)
        } else {
            showErrorMessage(getString(R.string.txt_invalid_movie_id))
        }
    }

    override fun setUpViews() {
        btnBack.setOnClickListener(this)
        btnPlayTrailer.setOnClickListener(this)

        ratingMovie.setIsIndicator(true)
        ratingMovie.numStars = 10
        ratingMovie.stepSize = 0.1f

        recyclerGenres.setLayoutHorizontal()
        recyclerGenres.adapter = AdapterGenres(genresList)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            movieImage.transitionName = IMAGE_TRANSITION_NAME
        }
    }

    override fun hideViews() {
        loadingDetailsMovie.visibility = View.GONE
        movieImage.visibility = View.GONE
        loadingImageMovie.visibility = View.GONE
        txtMovieDate.visibility = View.GONE
        txtRating.visibility = View.GONE
        txtCountVotes.visibility = View.GONE
        ratingMovie.visibility = View.GONE

        recyclerGenres.visibility = View.GONE
        txtDescription.visibility = View.GONE
        txtOriginalTitle.visibility = View.GONE
        txtTagline.visibility = View.GONE
        txtOriginalLanguage.visibility = View.GONE
        txtHomePage.visibility = View.GONE
    }

    override fun showLoadingImage() {
        loadingImageMovie.visibility = View.VISIBLE
        loadingImageMovie.playAnimation()
    }

    override fun hideLoadingImage() {
        loadingImageMovie.visibility = View.GONE
        loadingImageMovie.cancelAnimation()
    }

    override fun updateBackgroundImage(url: String) {
        presenter.renderImage(url, 0, heightImage)
    }

    override fun showErrorImage() {
        loadingImageMovie.visibility = View.VISIBLE
        loadingImageMovie.setAnimation(animationError)
        loadingImageMovie.playAnimation()
    }

    override fun showMovieImage(bmp: Bitmap) {
        movieImage.visibility = View.VISIBLE
        movieImage.setImageBitmap(bmp)
    }

    override fun updateReleaseDate(date: String) {
        txtMovieDate.visibility = View.VISIBLE
        if (date.isNotEmpty()) {
            txtMovieDate.text = date
        } else {
            txtMovieDate.text = getString(R.string.txt_default_result)
        }
    }

    override fun updateRating(rate: String, countVotes: Int, percentStars: Double) {
        txtRating.visibility = View.VISIBLE
        txtCountVotes.visibility = View.VISIBLE
        ratingMovie.visibility = View.VISIBLE

        txtRating.text = rate
        txtCountVotes.text = String.format(getString(R.string.txt_count_votaciones), countVotes)
        ratingMovie.rating = percentStars.toFloat()
    }

    override fun updateTitle(title: String) {
        if (title.isNotEmpty()) {
            collapsingToolbar.title = title
        } else {
            collapsingToolbar.title = getString(R.string.txt_default_result)
        }
    }

    override fun showGenres(genders: ArrayList<String>) {
        recyclerGenres.visibility = View.VISIBLE
        genresList.clear()
        genresList.addAll(genders)
        recyclerGenres.adapter?.notifyDataSetChanged()
        recyclerGenres.scheduleLayoutAnimation()
    }

    override fun updateOverview(overView: String) {
        txtDescription.visibility = View.VISIBLE
        if (overView.isNotEmpty()) {
            txtDescription.text = overView
        } else {
            txtDescription.text = getString(R.string.txt_default_result)
        }
    }

    override fun updateOriginalTitle(original: String) {
        txtOriginalTitle.visibility = View.VISIBLE
        if (original.isNotEmpty()) {
            txtOriginalTitle.text = original
        } else {
            txtOriginalTitle.text = getString(R.string.txt_default_result)
        }
    }

    override fun updateTagline(tagline: String) {
        txtTagline.visibility = View.VISIBLE
        txtTagline.text = tagline
    }

    override fun updateOriginaLanguage(language: String) {
        txtOriginalLanguage.visibility = View.VISIBLE
        if (language.isNotEmpty()) {
            txtOriginalLanguage.text = language
        } else {
            txtOriginalLanguage.text = getString(R.string.txt_default_result)
        }
    }

    override fun updatePromotionalPage(page: String) {
        txtHomePage.visibility = View.VISIBLE
        if (page.isNotEmpty()) {
            txtHomePage.text = page
        } else {
            txtHomePage.text = getString(R.string.txt_default_result)
        }
    }

    override fun showLoading() {
        loadingDetailsMovie.playAnimation()
        loadingDetailsMovie.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        delay(50) {
            loadingDetailsMovie.cancelAnimation()
            loadingDetailsMovie.visibility = View.GONE
        }
    }

    override fun showView() {

    }

    override fun hideView() {

    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        onBackPressed()
    }

    override fun onReadyVideoList(videos: ArrayList<Video>) {
        this.showStringDialogList(getString(R.string.select_a_video), videos) { key ->
            this.openYTVideo(key)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBack -> {
                onBackPressed()
            }
            R.id.btnPlayTrailer -> {
                presenter.loadVideos(movieId)
            }
        }
    }

}