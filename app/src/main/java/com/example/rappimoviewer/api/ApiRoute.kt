package com.example.rappimoviewer.api

import com.android.volley.Request

sealed class ApiRoute {

    private val apiKey: String
        get() {
            return "0fe985cbb1472382e0f85e8f01aa1ce4"
        }

    val timeOut: Int
        get() {
            return 3000
        }

    private val baseUrl: String
        get() {
            return when (this) {
                is DownloadImage -> "https://image.tmdb.org/t/p/"
                else -> "https://api.themoviedb.org/3/"
            }
        }

    val tag: String
        get() {
            return this::class.java.canonicalName ?: this.fullUrl
        }

    val fullUrl: String
        get() {
            return "$baseUrl${
            when (this) {
                is GetTopRateMovies -> "movie/top_rated"
                is GetPopularMovies -> "movie/popular"
                is GetUpcomingMovies -> "movie/upcoming"
                is GetMovieById -> "movie/${this.movieId}"
                is DownloadImage -> "${this.fileSize}/${this.imageUrl}?api_key=$apiKey"
                is SortMovieBy -> "search/movie"
                is GetVideos -> "movie/${this.movieId}/videos"
            }}"
        }

    val httpMethod: Int
        get() {
            return Request.Method.GET
        }

    val params: HashMap<String, String>
        get() {
            val map = hashMapOf<String, String>()
            map["api_key"] = apiKey
            map["language"] = "en-US"
            return when (this) {
                is SortMovieBy -> {
                    map["query"] = this.query
                    map["page"] = this.page.toString()
                    map
                }
                else -> map
            }
        }

    val headers: HashMap<String, String>
        get() {
            val map = hashMapOf<String, String>()
            map["Content-Type"] = "application/json;charset=utf-8"
            return when (this) {
                is GetTopRateMovies -> {
                    map["page"] = this.page.toString()
                    map
                }
                is GetPopularMovies -> {
                    map["page"] = this.page.toString()
                    map
                }
                is GetUpcomingMovies -> {
                    map["page"] = this.page.toString()
                    map
                }
                else -> {
                    map
                }
            }
        }

    data class GetTopRateMovies(var page: Int) : ApiRoute()

    data class GetPopularMovies(var page: Int) : ApiRoute()

    data class GetUpcomingMovies(var page: Int) : ApiRoute()

    data class GetMovieById(var movieId: String) : ApiRoute()

    data class GetVideos(var movieId: String) : ApiRoute()

    data class DownloadImage(var imageUrl: String, var fileSize: String) : ApiRoute()

    data class SortMovieBy(var query: String, var page: Int) : ApiRoute()
}