package widget.button.progress

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
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
    private var progressHeight = 8f
    private var progressWidth = 400f
    private var progressDuration = 1000L
    private var corners = 10f

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
            endY = progressHeight
            invalidate()
        }
    }

    private val lifeListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            isEnabled = true
            drawProgressBg = false
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
        porterDuff = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        attrs ?: return
        val array = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        colorBgLoading = array.getColor(R.styleable.LoadingButton_colorBackgroundLoading, colorBgLoading)
        colorBgDefault = array.getColor(R.styleable.LoadingButton_colorBackground, colorBgDefault)
        colorProgressBar = array.getColor(R.styleable.LoadingButton_colorProgressBar, colorProgressBar)
        colorProgressBackground =
            array.getColor(R.styleable.LoadingButton_colorProgressBackground, colorProgressBackground)
        progressHeight = array.getDimension(R.styleable.LoadingButton_progressHeight, progressHeight)
        // todo 暂时不支持动态设置进度条的长2019-05-12 现在是控件长度的一般，width/2
        progressWidth = array.getDimension(R.styleable.LoadingButton_progressWidth, progressWidth)
        progressDuration =
            array.getInteger(R.styleable.LoadingButton_progressDuration, progressDuration.toInt()).toLong()
        corners = array.getDimension(R.styleable.LoadingButton_corners, corners)
        array.recycle()
    }

    fun showLoading() {
        isEnabled = false
        drawProgressBg = true
        val anim = valueAnimator
        val width = width
        // 设置进度条的长度
        progressWidth = (width / 2).toFloat()
        val max = width + progressWidth
        anim.setIntValues(max.toInt())
        anim.duration = progressDuration
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.removeUpdateListener(showListener)
        anim.addUpdateListener(showListener)
        anim.start()
    }

    fun stopLoading() {
        val anim = valueAnimator
        anim.removeListener(lifeListener)
        anim.addListener(lifeListener)
        // 这里通过总播放时间除以单次播放时间得到播放次数，在"停止loading"时，为了看到完整的从左到右的进度条，使其播放次数为已经播放的次数
        val currentPlayTime = anim.currentPlayTime
        val playTime = currentPlayTime.toDouble()
        val duration = anim.duration
        if (duration == 0L) {
            anim.repeatCount = 1
            return
        }
        val playedCount = Math.ceil(playTime / duration)
        anim.repeatCount = playedCount.toInt()
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
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), corners, corners, mPaint)
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