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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class TacticCanvas(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var path: Path = Path()
    private var paint: Paint = Paint()
    private var paintStart: Paint = Paint()
    private var paintEnd: Paint = Paint()
    private var bitmap: Bitmap
    private var mCanvas: Canvas

    private var originX:Float = 0f
    private var originY:Float = 0f

    private var startX:Float = 0f
    private var startY:Float = 0f

    init {
        // bitmap
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Canvas
        mCanvas = Canvas(bitmap)
        mCanvas.drawColor(resources.getColor(R.color.basil_background))

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
                originX = event.x
                originY = event.y
                startX = event.x
                startY = event.y
                Arrow.wave = 1f
            }
            MotionEvent.ACTION_MOVE -> {
                getDashedLine()
                getSpline(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                Tactic.vPagerSwipe.value = true
                if (Arrow.isScreen) {
                    getScreenLine(event.x, event.y, Arrow.isScreen)
                } else {
//                    mCanvas.drawCircle(startX, startY, 10f, paintEnd)
                    getScreenLine(event.x, event.y, Arrow.isScreen)
                }
            }
        }
        invalidate()
        return true
    }

    private fun getSpline(stopX: Float, stopY: Float) {

        if (Arrow.isCurl) {
            // wave freq.
            Arrow.wave += 3f
            // add curl and set amplitude
            val curlX = stopX + sin(Arrow.wave * PI / 18).toFloat() * 40f
            val curlY = stopY + cos(Arrow.wave * PI / 18).toFloat() * 40f
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
        if (Arrow.isDash) {
            paint.pathEffect = DashPathEffect(floatArrayOf(40f, 20f), 0f)
        } else {
            paint.pathEffect = DashPathEffect(floatArrayOf(0f, 0f), 0f)
        }
    }

    private fun getScreenLine(stopX: Float, stopY: Float, screen: Boolean) {
        val dx = stopX - originX
        val dy = -(stopY - originY)

        if (screen) {
            var crossX = 10f
            var crossY = 10f
            Log.d("cross", "${dy} ${dx}")

            // quadrant
            when {
                (dx < 0f && abs(dy) <= 10f) || (dx > 0f && abs(dy) <= 10f) -> {
                    Log.d("cross", "x")
                    crossX = 0f
                    crossY = 10f
                }
                (abs(dx) <= 10f && dy > 0f) || (abs(dx) <= 10f && dy < 0f) -> {
                    Log.d("cross", "y")
                    crossX = 10f
                    crossY = 0f
                }
                (dx > 0f && dy > 0f) || (dx < 0f && dy < 0f) -> {
                    Log.d("cross", "1 3")
                    crossX = 10f
                    crossY = 10f
                }

                (dx < 0f && dy > 0f) || (dx > 0f && dy < 0f) -> {
                    Log.d("cross", "2 4")
                    crossX = -10f
                    crossY = 10f
                }
            }
            mCanvas.drawLine(startX - crossX, startY - crossY, startX + crossX,startY + crossY, paintEnd)
        } else {
            var arrowX = 10f
            var arrowY = 10f
            Log.d("cross", "${dy} ${dx}")
            mCanvas.drawLine(startX - arrowX, startY - arrowY, startX + arrowX,startY + arrowY, paintEnd)
            mCanvas.drawLine(startX - arrowX, startY + arrowY, startX + arrowX,startY - arrowY, paintEnd)
        }
    }

    fun clear() {
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        mCanvas.drawColor(resources.getColor(R.color.basil_background))
    }
}

object Arrow {
    var wave = 1f
    var isDash = false
    var isCurl = false
    var isScreen = false
}
