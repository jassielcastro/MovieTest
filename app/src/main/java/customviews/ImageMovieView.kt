package customviews

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.rappimoviewer.extensions.dp
import com.example.rappimoviewer.extensions.toPx
import com.example.rappimoviewer.presenter.LoadImagePresenter
import com.example.rappimoviewer.view.LoadImageViewImpl

class ImageMovieView : RelativeLayout, LoadImageViewImpl {

    private val animationLoading = "simple_loader.json"
    private val animationError = "not_found.json"

    private val presenter: LoadImagePresenter by lazy {
        LoadImagePresenter(this)
    }

    private val params: LayoutParams by lazy {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        params.addRule(CENTER_IN_PARENT, TRUE)
        params
    }

    private val imageView: AppCompatImageView by lazy {
        val image = AppCompatImageView(context)
        image.layoutParams = params
        image.scaleType = ImageView.ScaleType.CENTER_CROP
        image
    }

    private val imageViewLoader: LottieAnimationView by lazy {
        val image = LottieAnimationView(context)
        image.layoutParams = params
        image.repeatCount = LottieDrawable.INFINITE
        image
    }

    private var sourceWidth: Int = 180.dp.toPx
    private var sourceHeight: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        presenter.createView()
    }

    override fun createViews() {
        addView(imageView)
        addView(imageViewLoader)
        hideView()
    }

    override fun renderImage(url: String) {
        presenter.renderImage(url, this.sourceWidth, this.sourceHeight)
    }

    override fun showSuccessImage(bmp: Bitmap) {
        imageView.visibility = View.VISIBLE
        imageView.setImageBitmap(bmp)
    }

    override fun showErrorImage() {
        imageView.visibility = View.GONE
        imageViewLoader.setAnimation(animationError)
        imageViewLoader.visibility = View.VISIBLE
        imageViewLoader.playAnimation()
    }

    override fun showLoading() {
        imageViewLoader.setAnimation(animationLoading)
        imageViewLoader.visibility = View.VISIBLE
        imageViewLoader.playAnimation()
    }

    override fun hideLoading() {
        imageViewLoader.visibility = View.GONE
        imageViewLoader.cancelAnimation()
    }

    override fun showView() {
        imageView.visibility = View.VISIBLE
    }

    override fun hideView() {
        imageView.visibility = View.GONE
        imageViewLoader.visibility = View.GONE
    }

    override fun showErrorMessage(message: String) {

    }

}