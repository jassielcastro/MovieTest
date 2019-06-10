package com.example.tmdb.view

import android.graphics.Bitmap

interface LoadImageViewImpl : LoadViewImpl {
    fun createViews()
    fun renderImage(url: String)
    fun showSuccessImage(bmp: Bitmap)
    fun showErrorImage()
}