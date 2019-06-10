package com.example.tmdb.models

import extensions.JSONConvertable
import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("key")
    var key: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("site")
    var site: String,
    @SerializedName("type")
    var type: String
) : JSONConvertable