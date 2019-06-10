package customviews.autoresize

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import com.example.tmdb.R

class AutoResizeTextView : AppCompatTextView, ResizeBase {

    private val minDefaultTextSize = 20f

    // Our ellipse string
    private val mEllipsis = "..."

    // Flag for text and/or size changes to force a resize
    private var mNeedsResize = false

    // Text size that is set from code. This acts as a starting point for resizing
    private var mTextSize: Float = 0.toFloat()

    // Temporary upper bounds on the starting text size
    private var mMaxTextSize = 0f

    // Lower bounds for text size
    private var mMinTextSize = minDefaultTextSize

    // Text view line spacing multiplier
    private var mSpacingMult = 1.0f

    // Text view additional line spacing
    private var mSpacingAdd = 0.0f

    // Add ellipsis to text that overflows at the smallest text size
    private var mAddEllipsis = true

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    override fun init(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AutoResizeTextView,
            0, 0
        )

        mTextSize = textSize

        try {
            val minTextSize = a.getDimension(R.styleable.AutoResizeTextView_minSize, minDefaultTextSize)
            val maxTextSize = a.getDimension(R.styleable.AutoResizeTextView_maxSize, minDefaultTextSize)

            setMinTextSize(minTextSize)
            setMaxTextSize(maxTextSize)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            a.recycle()
        }

    }

    /**
     * When text changes, set the force resize flag to true and reset the text size.
     */
    override fun onTextChanged(text: CharSequence, start: Int, before: Int, after: Int) {
        mNeedsResize = true
        // Since this view may be reused, it is good to reset the text size
        resetTextSize()
    }

    /**
     * If the text view size changed, set the force resize flag to true
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw || h != oldh) {
            mNeedsResize = true
        }
    }

    /**
     * Override the set text size to update our internal reference values
     */
    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        mTextSize = textSize
    }

    /**
     * Override the set text size to update our internal reference values
     */
    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        mTextSize = textSize
    }

    /**
     * Override the set line spacing to update our internal reference values
     */
    override fun setLineSpacing(add: Float, mult: Float) {
        super.setLineSpacing(add, mult)
        mSpacingMult = mult
        mSpacingAdd = add
    }

    override fun setMaxTextSize(maxTextSize: Float) {
        mMaxTextSize = maxTextSize
        requestLayout()
        invalidate()
    }

    override fun getMaxTextSize(): Float {
        return mMaxTextSize
    }

    override fun setMinTextSize(minTextSize: Float) {
        mMinTextSize = minTextSize
        requestLayout()
        invalidate()
    }

    override fun getMinTextSize(): Float {
        return mMinTextSize
    }

    override fun setAddEllipsis(addEllipsis: Boolean) {
        mAddEllipsis = addEllipsis
    }

    override fun getAddEllipsis(): Boolean {
        return mAddEllipsis
    }

    override fun resetTextSize() {
        if (mTextSize > 0) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize)
            mMaxTextSize = mTextSize
        }
    }

    /**
     * Resize text after measuring
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed || mNeedsResize) {
            val widthLimit = right - left - compoundPaddingLeft - compoundPaddingRight
            val heightLimit = bottom - top - compoundPaddingBottom - compoundPaddingTop
            resizeText(widthLimit, heightLimit)
        }
        super.onLayout(changed, left, top, right, bottom)
    }

    @SuppressLint("SetTextI18n")
    override fun resizeText(width: Int, height: Int) {
        val text = text
        // Do not resize if the view does not have dimensions or there is no text
        if (text == null || text.isEmpty() || height <= 0 || width <= 0 || mTextSize == 0f) {
            return
        }

        // Get the text view's paint object
        val textPaint = paint

        // Store the current text size
        val oldTextSize = textPaint.textSize
        // If there is a max text size set, use the lesser of that and the default text size
        var targetTextSize = if (mMaxTextSize > 0) Math.min(mTextSize, mMaxTextSize) else mTextSize

        // Get the required text height
        var textHeight = getTextHeight(text, textPaint, width, targetTextSize)

        // Until we either fit within our text view or we had reached our min text size, incrementally try smaller sizes
        while (textHeight > height && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize)
            textHeight = getTextHeight(text, textPaint, width, targetTextSize)
        }

        // If we had reached our minimum text size and still don't fit, append an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Draw using a static layout
            // modified: use a copy of TextPaint for measuring
            val paint = TextPaint(textPaint)
            // Draw using a static layout
            val layout =
                StaticLayout(text, paint, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false)
            // Check that we have a least one line of rendered text
            if (layout.lineCount > 0) {
                // Since the line at the specific vertical position would be cut off,
                // we must trim up to the previous line
                val lastLine = layout.getLineForVertical(height) - 1
                // If the text would not even fit on a single line, clear it
                if (lastLine < 0) {
                    setText("")
                } else {
                    val start = layout.getLineStart(lastLine)
                    var end = layout.getLineEnd(lastLine)
                    var lineWidth = layout.getLineWidth(lastLine)
                    val ellipseWidth = textPaint.measureText(mEllipsis)

                    // Trim characters off until we have enough room to draw the ellipsis
                    while (width < lineWidth + ellipseWidth) {
                        lineWidth = textPaint.measureText(text.subSequence(start, --end + 1).toString())
                    }
                    setText("${text.subSequence(0, end)}$mEllipsis")
                }// Otherwise, trim to the previous line and add an ellipsis
            }
        }

        // Some devices try to auto adjust line spacing, so force default line spacing
        // and invalidate the layout as a side effect
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize)
        setLineSpacing(mSpacingAdd, mSpacingMult)

        // Reset force resize flag
        mNeedsResize = false
    }

    override fun getTextHeight(source: CharSequence, paint: TextPaint, width: Int, textSize: Float): Int {
        val paintCopy = TextPaint(paint)
        paintCopy.textSize = textSize
        val layout =
            StaticLayout(source, paintCopy, width, Layout.Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true)
        return layout.height
    }
}