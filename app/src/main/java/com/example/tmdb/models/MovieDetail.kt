package com.example.tmdb.models

import extensions.JSONConvertable
import com.google.gson.annotations.SerializedName

data class MovieDetail(
    @SerializedName("id")
    var id: Int,
    @SerializedName("title")
    var title: String,
    @SerializedName("overview")
    var overview: String,
    @SerializedName("release_date")
    var release_date: String,
    @SerializedName("original_language")
    var original_language: String,
    @SerializedName("original_title")
    var original_title: String,
    @SerializedName("poster_path")
    var poster_path: String?,
    @SerializedName("backdrop_path")
    var backdrop_path: String?,
    @SerializedName("vote_count")
    var vote_count: Int,
    @SerializedName("vote_average")
    var vote_average: Double,
    @SerializedName("popularity")
    var popularity: Double,
    @SerializedName("homepage")
    var homepage: String?,
    @SerializedName("tagline")
    var tagline: String?
) : JSONConvertable