package com.Hexagone

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.Region
import android.util.AttributeSet
import android.view.MotionEvent

class HexagonMaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private val hexagonPath = Path()
    private val hexagonBorderPath = Path()
    private val mBorderPaint = Paint().apply {
        color = Color.WHITE
        strokeCap = Paint.Cap.BUTT // Changed to ROUND for rounded corners
        strokeWidth = 10f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(110f, 60f), 0f) // Dotted line effect
    }
    private val mFillPaint = Paint().apply {
        color = Color.parseColor("#898989")
        style = Paint.Style.FILL
    }

    fun setRadius(radius: Float) {
        calculatePath(radius)
    }

    fun setBorderColor(color: Int) {
        mBorderPaint.color = color
        invalidate()
    }

    private fun calculatePath(radius: Float) {
        val halfRadius = radius / 2f
        val triangleHeight = Math.sqrt(3.0) * halfRadius.toFloat()
        val centerX = measuredWidth / 2f
        val centerY = measuredHeight / 2f
        hexagonPath.reset()
        hexagonPath.moveTo(centerX, centerY + radius)
        hexagonPath.lineTo((centerX - triangleHeight).toFloat(), centerY + halfRadius)
        hexagonPath.lineTo((centerX - triangleHeight).toFloat(), centerY - halfRadius)
        hexagonPath.lineTo(centerX, centerY - radius)
        hexagonPath.lineTo((centerX + triangleHeight).toFloat(), centerY - halfRadius)
        hexagonPath.lineTo((centerX + triangleHeight).toFloat(), centerY + halfRadius)
        hexagonPath.close()
        val radiusBorder = radius - 5f
        val halfRadiusBorder = radiusBorder / 2f
        val triangleBorderHeight = Math.sqrt(3.0) * halfRadiusBorder.toFloat()
        hexagonBorderPath.reset()
        hexagonBorderPath.moveTo(centerX, centerY + radiusBorder)
        hexagonBorderPath.lineTo((centerX - triangleBorderHeight).toFloat(), centerY + halfRadiusBorder)
        hexagonBorderPath.lineTo((centerX - triangleBorderHeight).toFloat(), centerY - halfRadiusBorder)
        hexagonBorderPath.lineTo(centerX, centerY - radiusBorder)
        hexagonBorderPath.lineTo((centerX + triangleBorderHeight).toFloat(), centerY - halfRadiusBorder)
        hexagonBorderPath.lineTo((centerX + triangleBorderHeight).toFloat(), centerY + halfRadiusBorder)
        hexagonBorderPath.close()
        invalidate()
    }

    @Suppress("DEPRECATION")
    override fun onDraw(c: Canvas) {
        c.drawPath(hexagonPath, mFillPaint) // Fill the hexagon with white
        c.drawPath(hexagonBorderPath, mBorderPaint) // Draw the dotted border
        c.clipPath(hexagonPath, Region.Op.INTERSECT)
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.LIGHTEN)
        super.onDraw(c)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
        calculatePath(Math.min(width / 2f, height / 2f) - 10f)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            if (isPointInHexagon(x, y)) {
                true
            } else {
                false
            }
        } else {
            false
        }
        super.onTouchEvent(event)
    }

    private fun isPointInHexagon(x: Float, y: Float): Boolean {
        val region = Region()
        region.setPath(hexagonPath, Region())
        if (region.contains(x.toInt(), y.toInt())) {
            return true
        }
        return false
    }

}