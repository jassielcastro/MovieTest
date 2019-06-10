package com.example.tmdb.presenter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.example.tmdb.view.LoadImageViewImpl
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class LoadImagePresenter(private var view: LoadImageViewImpl) : BasePresenter {

    private val targetImage: TargetImage by lazy {
        TargetImage()
    }

    override fun createView() {
        view.createViews()
        hideAllViews()
    }

    override fun hideAllViews() {
        view.hideView()
    }

    fun renderImage(imageUrl: String, width: Int, height: Int) {
        hideAllViews()
        Picasso.get()
            .load(imageUrl)
            .resize(width, height)
            .into(targetImage)
    }

    inner class TargetImage : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            view.showLoading()
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            hideAllViews()
            view.showErrorImage()
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            hideAllViews()
            if (bitmap != null) {
                view.showSuccessImage(bitmap)
            } else {
                view.showErrorImage()
            }
        }
    }
}