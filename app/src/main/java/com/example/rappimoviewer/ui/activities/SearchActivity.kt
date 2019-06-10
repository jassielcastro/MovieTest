package com.example.rappimoviewer.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.example.rappimoviewer.R
import com.example.rappimoviewer.extensions.addTextWatcher
import com.example.rappimoviewer.extensions.launch
import com.example.rappimoviewer.extensions.setGridLayout
import com.example.rappimoviewer.models.Movie
import com.example.rappimoviewer.presenter.SearchMoviesPresenter
import com.example.rappimoviewer.ui.adapter.AdapterSimpleMovie
import com.example.rappimoviewer.ui.adapter.ViewHolder
import com.example.rappimoviewer.view.SearchMovieViewImpl
import customviews.ActivityBase
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : ActivityBase(), SearchMovieViewImpl {

    companion object {
        const val CARD_TRANSITION_NAME = "field_search"

        const val EXTRA_CATEGORY_SEARCH = "EXTRA_CATEGORY_SEARCH"
        const val NO_CATEGORY_SEARCH = -1
        const val CATEGORY_UPCOMING = 0
        const val CATEGORY_TOP_RATE = 1
        const val CATEGORY_POPULATE = 2
    }

    private var categorySearch = -1
    private val maxRowCount = 3
    private val searchMovies: ArrayList<Movie?> = arrayListOf()
    private val presenter: SearchMoviesPresenter by lazy {
        SearchMoviesPresenter(this)
    }

    private val clickPopulates: ViewHolder.OnClickListener = object : ViewHolder.OnClickListener {
        override fun onItemClick(view: View, position: Int) {
            presenter.handleClickMovies(view, searchMovies[position], this@SearchActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (intent.hasExtra(EXTRA_CATEGORY_SEARCH)) {
            categorySearch = intent.getIntExtra(EXTRA_CATEGORY_SEARCH, NO_CATEGORY_SEARCH)
        }

        presenter.createView()
    }

    override fun setUpViews() {
        edtSearchMovie.addTextWatcher {
            if (categorySearch == NO_CATEGORY_SEARCH) {
                presenter.searchMovieBy(it, this)
            } else {
                presenter.searchMovieByCategory(it, categorySearch, this)
            }
        }
        recyclerSearch.setGridLayout(maxRowCount)
        recyclerSearch.adapter = AdapterSimpleMovie(searchMovies, clickPopulates, null)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardSearchMovies.transitionName = CARD_TRANSITION_NAME
        }
    }

    override fun onReadySortedMovies(movies: ArrayList<Movie>) {
        searchMovies.clear()
        searchMovies.addAll(movies)
        recyclerSearch.visibility = View.VISIBLE
        recyclerSearch.adapter?.notifyDataSetChanged()
    }

    override fun showError() {
        searchMovies.clear()
        searchMovies.add(null)
        recyclerSearch.visibility = View.VISIBLE
        recyclerSearch.adapter?.notifyDataSetChanged()
    }

    override fun showLoading() {
        loadingSearch.playAnimation()
        loadingSearch.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingSearch.cancelAnimation()
        loadingSearch.visibility = View.GONE
    }

    override fun showView() {

    }

    override fun hideView() {
        recyclerSearch.visibility = View.GONE
        loadingSearch.visibility = View.GONE
    }

    override fun startActivityDetail(intent: Intent) {
        this.launch(intent)
    }

    override fun startActivityDetailTransition(intent: Intent, options: ActivityOptions) {
        this.launch(intent, false, options)
    }

    override fun showErrorMessage(message: String) {

    }

}