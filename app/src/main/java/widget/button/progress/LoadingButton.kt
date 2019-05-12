package widget.button.progress

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.animation.ObjectAnimator
import android.graphics.*
import android.widget.TextView


class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0/*android.R.attr.buttonStyle*/
) : TextView(context, attrs, defStyle) {

    /**
     * parameters from attrs
     */
    private var colorBgLoading = Color.GRAY
    private var colorBgDefault = Color.WHITE
    private var colorProgressBar = Color.GRAY
    private var colorProgressBackground = Color.WHITE
    private var progressHeight = 16f
    private var progressWidth = 400f
    private var progressDuration = 1000L

    private var mPaint = Paint()
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = progressHeight
    private var valueAnimator = ValueAnimator()
    private var drawProgressBg = false
    private var porterDuff: PorterDuffXfermode? = null

    private val showListener = object : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator?) {
            val value = animation?.animatedValue as? Int ?: return
            startX = value.toFloat()
            endX = startX - progressWidth
            Log.e("tag", "$value startX$startX  endX$endX")
            invalidate()
        }
    }

    private val lifeListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            isEnabled = true
            drawProgressBg = false
            normalBackground()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }

    init {
        initAttrs(attrs)
        mPaint.color = colorProgressBar
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        PorterDuffXfermode(PorterDuff.Mode.SRC)
        porterDuff = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

        normalBackground()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs ?: return
        val array = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        colorBgLoading = array.getColor(R.styleable.LoadingButton_colorBackgroundLoading, colorBgLoading)
        colorBgDefault = array.getColor(R.styleable.LoadingButton_colorBackground, colorBgDefault)
        colorProgressBar = array.getColor(R.styleable.LoadingButton_colorProgressBar, colorProgressBar)
        colorProgressBackground =
            array.getColor(R.styleable.LoadingButton_colorProgressBackground, colorProgressBackground)
//        progressHeight = array.getDimension(R.styleable.LoadingButton_progressHeight, progressHeight)
//        progressWidth = array.getDimension(R.styleable.LoadingButton_progressWidth, progressWidth)
        progressDuration =
            array.getInteger(R.styleable.LoadingButton_progressDuration, progressDuration.toInt()).toLong()


        array.recycle()
    }

    fun showLoading() {
        val anim = valueAnimator
        val width = width
        progressWidth = (width / 3).toFloat()
        val max = width + progressWidth
        anim.setIntValues(max.toInt())
        anim.duration = progressDuration
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.removeUpdateListener(showListener)
        anim.addUpdateListener(showListener)
        anim.start()
        isEnabled = false
    }

    fun stopLoading() {
        val anim = valueAnimator
        anim.repeatCount = 1
        anim.removeListener(lifeListener)
        anim.addListener(lifeListener)
    }

    override fun onDetachedFromWindow() {
        valueAnimator.cancel()
        stopLoading()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        mPaint.color = if (drawProgressBg) colorBgLoading else colorBgDefault
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 30f, 30f, mPaint)
        if (drawProgressBg) {
            mPaint.xfermode = porterDuff
            mPaint.color = colorProgressBackground
            canvas.drawRect(0f, startY, width.toFloat(), endY, mPaint)

            mPaint.color = colorProgressBar
            canvas.drawRect(startX, startY, endX, endY, mPaint)
        }

        // 还原混合模式
        mPaint.xfermode = null
        // 还原画布
        canvas.restoreToCount(sc)
        super.onDraw(canvas)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            loadingBackground()
            drawProgressBg = true
        }
    }

    /**
     * 修改背景色，从载入状态颜色切换到默认颜色
     */
    private fun normalBackground() {
//        val animator = ObjectAnimator.ofInt(this, "backgroundColor", colorBgLoading, colorBgDefault)
//        animator.duration = 300
//        animator.setEvaluator(ArgbEvaluator())
//        animator.start()
    }

    /**
     * 修改背景色，从默认颜色切换到载入状态颜色
     */
    private fun loadingBackground() {
//        val animator = ObjectAnimator.ofInt(this, "backgroundColor", colorBgDefault, colorBgLoading)
//        animator.duration = 300
//        //如果要颜色渐变必须要ArgbEvaluator，来实现颜色之间的平滑变化，否则会出现颜色不规则跳动
//        animator.setEvaluator(ArgbEvaluator())
//        animator.start()
    }


}