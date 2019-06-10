package com.example.rappimoviewer.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.rappimoviewer.R
import com.example.rappimoviewer.extensions.dp
import com.example.rappimoviewer.extensions.launch
import com.example.rappimoviewer.extensions.setLayoutHorizontal
import com.example.rappimoviewer.extensions.toPx
import com.example.rappimoviewer.models.Movie
import com.example.rappimoviewer.presenter.MoviesListPresenter
import com.example.rappimoviewer.ui.adapter.AdapterFullMovie
import com.example.rappimoviewer.ui.adapter.AdapterSimpleMovie
import com.example.rappimoviewer.ui.adapter.ViewHolder
import com.example.rappimoviewer.view.MoviesListViewImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MoviesListViewImpl, View.OnClickListener {

    private val presenter: MoviesListPresenter by lazy {
        MoviesListPresenter(this, baseContext)
    }

    private val upcomingList: ArrayList<Movie?> = arrayListOf()
    private val topRateList: ArrayList<Movie?> = arrayListOf()
    private val populateList: ArrayList<Movie?> = arrayListOf()

    private val clickUpcoming: ViewHolder.OnClickListener = object : ViewHolder.OnClickListener {
        override fun onItemClick(view: View, position: Int) {
            presenter.handleClickMovies(
                upcomingList[position],
                this@MainActivity
            )
        }
    }

    private val clickTopRate: ViewHolder.OnClickListener = object : ViewHolder.OnClickListener {
        override fun onItemClick(view: View, position: Int) {
            presenter.handleClickMovies(
                topRateList[position],
                this@MainActivity
            )
        }
    }

    private val clickPopulates: ViewHolder.OnClickListener = object : ViewHolder.OnClickListener {
        override fun onItemClick(view: View, position: Int) {
            presenter.handleClickMovies(
                populateList[position],
                this@MainActivity
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter.createView()
    }

    override fun setUpViews() {
        cardSearchMovies.setOnClickListener(this)

        ViewCompat.setElevation(containerUpComing, 8.dp.toPx.toFloat())
        recyclerUpcoming.setLayoutHorizontal()
        recyclerPopulates.setLayoutHorizontal()
        recyclerTopRate.setLayoutHorizontal()

        recyclerUpcoming.adapter = AdapterFullMovie(upcomingList, clickUpcoming) {
            presenter.requestSearchViewCategory(this, SearchActivity.CATEGORY_UPCOMING)
        }
        recyclerPopulates.adapter = AdapterSimpleMovie(populateList, clickPopulates) {
            presenter.requestSearchViewCategory(this, SearchActivity.CATEGORY_POPULATE)
        }
        recyclerTopRate.adapter = AdapterSimpleMovie(topRateList, clickTopRate) {
            presenter.requestSearchViewCategory(this, SearchActivity.CATEGORY_TOP_RATE)
        }
    }

    override fun onReadyUpComingMovies(moviesList: ArrayList<Movie>) {
        upcomingList.clear()
        if (moviesList.isNotEmpty()) {
            upcomingList.addAll(moviesList)
            upcomingList.add(Movie.getEmptyMoview())
        } else {
            upcomingList.add(null)
        }
        recyclerUpcoming.visibility = View.VISIBLE
        recyclerUpcoming.adapter?.notifyDataSetChanged()
        recyclerUpcoming.scheduleLayoutAnimation()
    }

    override fun onReadyTopRateMovies(moviesList: ArrayList<Movie>) {
        topRateList.clear()
        if (moviesList.isNotEmpty()) {
            topRateList.addAll(moviesList)
            topRateList.add(Movie.getEmptyMoview())
        } else {
            topRateList.add(null)
        }
        recyclerTopRate.visibility = View.VISIBLE
        recyclerTopRate.adapter?.notifyDataSetChanged()
        recyclerTopRate.scheduleLayoutAnimation()
    }

    override fun onReadyPopulateMovies(moviesList: ArrayList<Movie>) {
        populateList.clear()
        if (moviesList.isNotEmpty()) {
            populateList.addAll(moviesList)
            populateList.add(Movie.getEmptyMoview())
        } else {
            populateList.add(null)
        }
        recyclerPopulates.visibility = View.VISIBLE
        recyclerPopulates.adapter?.notifyDataSetChanged()
        recyclerPopulates.scheduleLayoutAnimation()
    }

    override fun showLoadingUpComing() {
        loadingUpcoming.visibility = View.VISIBLE
        loadingUpcoming.playAnimation()
    }

    override fun showLoadingTopRate() {
        loadingTopRate.visibility = View.VISIBLE
        loadingTopRate.playAnimation()
    }

    override fun showLoadingPopulate() {
        loadingPopulate.visibility = View.VISIBLE
        loadingPopulate.playAnimation()
    }

    override fun hideLoadingUpComing() {
        loadingUpcoming.visibility = View.GONE
        loadingUpcoming.cancelAnimation()
    }

    override fun hideLoadingTopRate() {
        loadingTopRate.visibility = View.GONE
        loadingTopRate.cancelAnimation()
    }

    override fun hideLoadingPopulate() {
        loadingPopulate.visibility = View.GONE
        loadingPopulate.cancelAnimation()
    }

    override fun hideViews() {
        hideLoadingUpComing()
        hideLoadingTopRate()
        hideLoadingPopulate()
        recyclerUpcoming.visibility = View.GONE
        recyclerPopulates.visibility = View.GONE
        recyclerTopRate.visibility = View.GONE
    }

    override fun startActivityDetail(intent: Intent) {
        this.launch(intent)
    }

    override fun startActivityDetailTransition(intent: Intent, options: ActivityOptions) {
        this.launch(intent, false, options)
    }

    override fun onClick(v: View?) {
        if (R.id.cardSearchMovies == v?.id) {
            presenter.requestSearchView(this, cardSearchMovies)
        }
    }

}
