package com.aqua.hoophelper.team.child.tactic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.aqua.hoophelper.R
import java.util.logging.Handler
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class TacticCanvas(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var path: Path = Path()
    private var paint: Paint = Paint()
    private var paintStart: Paint = Paint()
    private var paintEnd: Paint = Paint()
    private var bitmap: Bitmap
    private var mCanvas: Canvas

    private var startX:Float = 0f
    private var startY:Float = 0f

    init {
        // bitmap
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Canvas
        mCanvas = Canvas(bitmap)
        mCanvas.drawColor(resources.getColor(R.color.basil_green_light))

        // Paint
        paint.apply {
            color = resources.getColor(R.color.basil_green_dark)
            strokeWidth = 5f
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        paintStart.apply {
            color = resources.getColor(R.color.basil_bg)
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }

        paintEnd.apply {
            color = resources.getColor(R.color.basil_orange)
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(bitmap, 0f, 0f, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                Tactic.vPagerSwipe.value = false
                mCanvas.drawCircle(event.x, event.y, 10f, paintStart)
                path.reset()
                path.moveTo(event.x, event.y)
                startX = event.x
                startY = event.y
                Line.wave = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                getDashedLine()
                getSpline(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                Tactic.vPagerSwipe.value = true
                Log.d("dash","${Line.isDash}")
                mCanvas.drawCircle(event.x, event.y, 10f, paintEnd)
            }
        }
        invalidate()
        return true
    }

    private fun getSpline(stopX: Float, stopY: Float) {

        if (Line.isCurl) {
            // wave freq.
            Line.wave += 6f
            // add curl and set amplitude
            val curlX = stopX + sin(Line.wave * PI / 18).toFloat() * 20f
            val curlY = stopY + cos(Line.wave * PI / 18).toFloat() * 20f
            path.quadTo(startX, startY, curlX, curlY)
            mCanvas.drawPath(path, paint)
            startX = curlX
            startY = curlY
        } else {
            path.quadTo(startX, startY, stopX, stopY)
            mCanvas.drawPath(path, paint)
            startX = stopX
            startY = stopY
        }
    }

    private fun getDashedLine() {
        if (Line.isDash) {
            paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        } else {
            paint.pathEffect = DashPathEffect(floatArrayOf(0f, 0f), 0f)
        }
    }
}

object Line {
    var wave = 1f
    var isDash = false
    var isCurl = false
    var isScreen = false
}
