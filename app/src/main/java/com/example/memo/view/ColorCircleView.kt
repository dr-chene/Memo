package com.example.memo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import kotlin.math.min

/**
Created by chene on @date 20-11-21 上午10:52
 **/
class ColorCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : View(context, attrs, defStyleAttr) {
    private var radius: Float = 0f
    var color: String = "#FFFFFF"
        set(value) {
            field = value
            paint.color = Color.parseColor(value)
            invalidate()
        }
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    val length: MutableLiveData<Float> = MutableLiveData(0f)
    private var edge = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWith = MeasureSpec.getSize(widthMeasureSpec)
        val mHeight = MeasureSpec.getSize(heightMeasureSpec)
        length.postValue(0.5f * min(mWith, mHeight))
        edge = 0.5f * min(mWith, mHeight)
        radius = 0.35f * min(mWith, mHeight)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawCircle(edge, edge, radius, paint)
        }
    }

    fun selected() {
        radius = edge / 1.5f
        paint.apply {
            Log.d("TAG_05", "selected: $radius")
            strokeWidth = radius / 1.5f
            style = Paint.Style.STROKE
        }
        invalidate()
    }

    fun unSelected() {
        radius = 0.7f * edge
        paint.apply {
            strokeWidth = 0f
            style = Paint.Style.FILL
        }
        invalidate()
    }
}