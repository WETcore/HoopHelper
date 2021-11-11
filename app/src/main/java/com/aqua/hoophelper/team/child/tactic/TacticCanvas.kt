package com.aqua.hoophelper.team.child.tactic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.aqua.hoophelper.R

class TacticCanvas(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var path: Path
    private var paint: Paint
    private var paintF: Paint
    private var bitmap: Bitmap
    private var mCanvas: Canvas

    private var startX:Float = 0f
    private var startY:Float = 0f

    private var paintToggle:Boolean = false

    init {
        path = Path()

        // bitmap
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Canvas
        mCanvas = Canvas(bitmap)
        mCanvas.drawColor(resources.getColor(R.color.basil_green_light))

        // Paint
        paint = Paint()
        paint.apply {
            color = resources.getColor(R.color.basil_green_dark)
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        paintF = Paint()
        paintF.apply {
            color = resources.getColor(R.color.basil_orange)
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("canvas", "${paintToggle}")
        when (paintToggle) {
            false -> {
                canvas!!.drawBitmap(bitmap, 0f, 0f, paint)
            }
            true -> {
                canvas!!.drawBitmap(bitmap, 0f, 0f, paintF)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                path.reset()
                path.moveTo(event.x, event.y)
                startX = event.x
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                getSpline(event.x, event.y)
                // call onDraw
                paintToggle = false
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mCanvas.drawCircle(event.x, event.y, 10f, paintF)
                // call onDraw
                paintToggle = true
                invalidate()
            }
        }
        return true
    }

    private fun getSpline(x: Float, y: Float) {
        path.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2)
        mCanvas.drawPath(path, paint)
        startX = x
        startY = y
    }
}
