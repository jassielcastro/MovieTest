package net.api

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import extensions.toArrayList
import extensions.toObject
import com.example.tmdb.models.Movie
import com.example.tmdb.models.MovieDetail
import com.example.tmdb.models.Video
import customviews.ImageMovieView

class ApiClient(private var ctx: Context) {

    fun getTopRateMovies(
        page: Int,
        completion: (pageCount: Int, moviesList: ArrayList<Movie>, error: String?) -> Unit
    ) {
        val route = ApiRoute.GetTopRateMovies(page)
        getListOfMovies(route, completion)
    }

    fun getPopularMovies(
        page: Int,
        completion: (pageCount: Int, moviesList: ArrayList<Movie>, error: String?) -> Unit
    ) {
        val route = ApiRoute.GetPopularMovies(page)
        getListOfMovies(route, completion)
    }

    fun getUpComingMovies(
        page: Int,
        completion: (pageCount: Int, moviesList: ArrayList<Movie>, error: String?) -> Unit
    ) {
        val route = ApiRoute.GetUpcomingMovies(page)
        getListOfMovies(route, completion)
    }

    private fun getListOfMovies(
        route: ApiRoute,
        completion: (totalPages: Int, moviesList: ArrayList<Movie>, error: String?) -> Unit
    ) {
        performRequest(route) { status, response ->
            if (status) {
                val json = response.json
                val totalPages = if (json.has("total_pages")) {
                    json.getInt("total_pages")
                } else {
                    0
                }

                val array: ArrayList<Movie> = arrayListOf()
                if (json.has("results")) {
                    array.addAll(json.getJSONArray("results").toArrayList())
                }
                completion.invoke(totalPages, array, null)
            } else {
                completion.invoke(0, arrayListOf(), response.message)
            }
        }
    }

    fun loadImageUrl(into: ImageMovieView, imageUrl: String, fileSize: String) {
        val route = ApiRoute.DownloadImage(imageUrl, fileSize)
        into.renderImage(route.fullUrl)
    }

    fun getMovieById(movieId: Int, completion: (movie: MovieDetail?, genres: ArrayList<String>) -> Unit) {
        val route = ApiRoute.GetMovieById(movieId.toString())
        performRequest(route) { status, response ->
            if (status) {
                val json = response.json

                val genres: ArrayList<String> = if (json.has("genres")) {
                    val gnr = arrayListOf<String>()
                    val array = json.getJSONArray("genres")
                    for (i in 0 until array.length()) {
                        gnr.add(array.getJSONObject(i).getString("name"))
                    }
                    gnr
                } else {
                    arrayListOf()
                }

                val detail: MovieDetail = json.toString().toObject()

                val languages: String = if (json.has("spoken_languages")) {
                    val spoken = json.getJSONArray("spoken_languages")
                    val builder = StringBuilder()
                    val length = spoken.length()
                    for ((count, i) in (0 until length).withIndex()) {
                        builder.append(spoken.getJSONObject(i).getString("name"))
                        if (count < length - 1) {
                            builder.append(", ")
                        }
                    }
                    builder.toString()
                } else {
                    detail.original_language
                }
                detail.original_language = languages

                completion.invoke(detail, genres)
            } else {
                completion.invoke(null, arrayListOf())
            }
        }
    }

    fun sortMoviesBy(
        query: String,
        page: Int, hasConnection: Boolean,
        completion: (pageCount: Int, moviesList: ArrayList<Movie>, error: String?) -> Unit
    ) {
        if (hasConnection) {
            val route = ApiRoute.SortMovieBy(query, page)
            getListOfMovies(route, completion)
        } else {
            getUpComingMovies(page) { _, upcoming, _ ->
                getPopularMovies(page) { _, populates, _ ->
                    getTopRateMovies(page) { _, topRates, _ ->
                        val cacheMovies: ArrayList<Movie> = arrayListOf()
                        cacheMovies.addAll(upcoming)
                        populates.map {
                         if (!cacheMovies.contains(it)) {
                             cacheMovies.add(it)
                         }
                        }
                        topRates.map {
                            if (!cacheMovies.contains(it)) {
                                cacheMovies.add(it)
                            }
                        }
                        val sortedCacheMovies = cacheMovies.filter { movie ->
                            movie.title.contains(query, true)
                        }
                        completion.invoke(1, sortedCacheMovies.toArrayList(), null)
                    }
                }
            }
        }
    }

    fun getVideoMovies(movieId: Int, completion: (videos: ArrayList<Video>) -> Unit) {
        val route = ApiRoute.GetVideos(movieId.toString())
        performRequest(route) { status, response ->
            if (status) {
                val json = response.json
                println("ApiClient.getVideoMovies --> $json")
                if (json.has("results")) {
                    completion.invoke(json.getJSONArray("results").toArrayList())
                } else {
                    completion.invoke(arrayListOf())
                }
            } else {
                completion.invoke(arrayListOf())
            }
        }
    }

    /***
     * PERFORM REQUEST
     */
    private fun performRequest(route: ApiRoute, completion: (success: Boolean, apiResponse: ApiResponse) -> Unit) {
        val parameters = StringBuilder()
        var count = 0
        route.params.map {
            parameters.append("${it.key}=${it.value}")
            if (count < route.params.size) {
                parameters.append("&")
            }
            count++
        }

        val request = object : StringRequest(route.httpMethod, "${route.fullUrl}?$parameters", { response ->
            this.handle(response, completion)
        }, {
            it.printStackTrace()
            if (it.networkResponse != null && it.networkResponse.data != null)
                this.handle(String(it.networkResponse.data), completion)
            else
                this.handle(getStringError(it), completion)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                return route.headers
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            route.timeOut,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        request.tag = route.tag

        val rq = getRequestQueue()
        rq.cancelAll(route.tag)
        rq.add(request)
    }

    /**
     * This method will make the creation of the answer as ApiResponse
     **/
    private fun handle(response: String, completion: (success: Boolean, apiResponse: ApiResponse) -> Unit) {
        val ar = ApiResponse(response)
        completion.invoke(ar.success, ar)
    }

    /**
     * This method will return the error as String
     **/
    private fun getStringError(volleyError: VolleyError): String {
        return when (volleyError) {
            is TimeoutError -> "The connection timed out."
            is NoConnectionError -> "The connection could not be established."
            is AuthFailureError -> "There was an authentication failure in your request."
            is ServerError -> "Error while processing the server response."
            is NetworkError -> "Network error, please verify your connection."
            is ParseError -> "Error while processing the server response."
            else -> "Internet error"
        }
    }

    /**
     * We create and return a new instance for the queue of Volley requests.
     **/
    private fun getRequestQueue(): RequestQueue {
        val maxCacheSize = 20 * 1024 * 1024
        val cache = DiskBasedCache(ctx.cacheDir, maxCacheSize)
        val netWork = BasicNetwork(HurlStack())
        val mRequestQueue = RequestQueue(cache, netWork)
        mRequestQueue.start()
        System.setProperty("http.keepAlive", "false")
        return mRequestQueue
    }

}