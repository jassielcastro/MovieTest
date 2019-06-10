package customviews.autoresize

import android.text.TextPaint
import android.util.AttributeSet

interface ResizeBase {

    fun init(attrs: AttributeSet?)

    fun setMaxTextSize(maxTextSize: Float)

    fun getMaxTextSize(): Float

    fun setMinTextSize(minTextSize: Float)

    fun getMinTextSize(): Float

    fun setAddEllipsis(addEllipsis: Boolean)

    fun getAddEllipsis(): Boolean

    fun resetTextSize()

    fun resizeText(width: Int, height: Int)

    fun getTextHeight(source: CharSequence, paint: TextPaint, width: Int, textSize: Float): Int

}