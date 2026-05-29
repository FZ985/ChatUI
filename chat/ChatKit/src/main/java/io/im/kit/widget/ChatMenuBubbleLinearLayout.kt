package io.im.kit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.LinearLayout


/**
 * by DAD FZ
 * 2026/5/28
 * desc：
 **/
class ChatMenuBubbleLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val bubblePath = Path()

    private val contentClipPath = Path()

    /**
     * 三角是否在顶部
     */
    var triangleOnTop = true
        set(value) {
            if (field == value) return
            field = value

            updateTrianglePadding()

            requestLayout()
            invalidate()
        }

    /**
     * 三角大小
     */
    private val triangleSize = dp(8)

    /**
     * 圆角
     */
    private val radius = dp(8)

    /**
     * 三角中心点 X
     */
    var triangleOffset = dp(40)
        set(value) {
            field = value
            invalidate()
        }

    init {
        orientation = VERTICAL
        setWillNotDraw(false)

        updateTrianglePadding()
    }

    private fun updateTrianglePadding() {

        if (triangleOnTop) {
            setPadding(
                paddingLeft,
                triangleSize.toInt(),
                paddingRight,
                0
            )
        } else {
            setPadding(
                paddingLeft,
                0,
                paddingRight,
                triangleSize.toInt()
            )
        }
    }

    override fun dispatchDraw(canvas: Canvas) {

        buildBubblePath()

        buildClipPath()

        // 画背景
        canvas.drawPath(bubblePath, paint)

        canvas.save()

        // 裁剪内容区域
        canvas.clipPath(contentClipPath)

        super.dispatchDraw(canvas)

        canvas.restore()
    }

    /**
     * 构建整体气泡
     */
    private fun buildBubblePath() {

        bubblePath.reset()

        val w = width.toFloat()
        val h = height.toFloat()

        val left = 0f
        val right = w

        val rectTop = paddingTop.toFloat()
        val rectBottom = h - paddingBottom

        val centerX = triangleOffset.coerceIn(
            radius + triangleSize,
            w - radius - triangleSize
        )

        if (triangleOnTop) {

            bubblePath.moveTo(left + radius, rectTop)

            // 到三角左边
            bubblePath.lineTo(centerX - triangleSize, rectTop)

            // 三角
            bubblePath.lineTo(centerX, 0f)
            bubblePath.lineTo(centerX + triangleSize, rectTop)

            // 顶边右侧
            bubblePath.lineTo(right - radius, rectTop)

            // 右上圆角
            bubblePath.quadTo(
                right,
                rectTop,
                right,
                rectTop + radius
            )

            // 右边
            bubblePath.lineTo(right, rectBottom - radius)

            // 右下圆角
            bubblePath.quadTo(
                right,
                rectBottom,
                right - radius,
                rectBottom
            )

            // 底边
            bubblePath.lineTo(left + radius, rectBottom)

            // 左下圆角
            bubblePath.quadTo(
                left,
                rectBottom,
                left,
                rectBottom - radius
            )

            // 左边
            bubblePath.lineTo(left, rectTop + radius)

            // 左上圆角
            bubblePath.quadTo(
                left,
                rectTop,
                left + radius,
                rectTop
            )

        } else {

            bubblePath.moveTo(left + radius, 0f)

            // 顶边
            bubblePath.lineTo(right - radius, 0f)

            // 右上圆角
            bubblePath.quadTo(
                right,
                0f,
                right,
                radius
            )

            // 右边
            bubblePath.lineTo(right, rectBottom - radius)

            // 右下圆角
            bubblePath.quadTo(
                right,
                rectBottom,
                right - radius,
                rectBottom
            )

            // 到三角右边
            bubblePath.lineTo(centerX + triangleSize, rectBottom)

            // 三角
            bubblePath.lineTo(centerX, h)
            bubblePath.lineTo(centerX - triangleSize, rectBottom)

            // 左下边
            bubblePath.lineTo(left + radius, rectBottom)

            // 左下圆角
            bubblePath.quadTo(
                left,
                rectBottom,
                left,
                rectBottom - radius
            )

            // 左边
            bubblePath.lineTo(left, radius)

            // 左上圆角
            bubblePath.quadTo(
                left,
                0f,
                left + radius,
                0f
            )
        }

        bubblePath.close()
    }

    /**
     * 内容裁剪区域
     */
    private fun buildClipPath() {

        contentClipPath.reset()

        val w = width.toFloat()
        val h = height.toFloat()

        val rectTop = paddingTop.toFloat()

        val rectBottom = h - paddingBottom

        val rect = RectF(
            0f,
            rectTop,
            w,
            rectBottom
        )

        contentClipPath.addRoundRect(
            rect,
            radius,
            radius,
            Path.Direction.CW
        )
    }

    /**
     * 顶部三角
     */
    fun setTopOffset(offset: Float) {

        triangleOnTop = true

        triangleOffset = offset

        invalidate()
    }

    /**
     * 底部三角
     */
    fun setBottomOffset(offset: Float) {

        triangleOnTop = false

        triangleOffset = offset

        invalidate()
    }

    fun setBubbleColor(color: Int) {
        paint.color = color
        invalidate()
    }

    private fun dp(value: Int): Float {
        return value * resources.displayMetrics.density
    }
}