package com.aqua.hoophelper.team.child.tactic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.aqua.hoophelper.R
import com.aqua.hoophelper.util.Arrow
import com.aqua.hoophelper.util.Tactic
import com.aqua.hoophelper.util.VALUE_TEN
import com.aqua.hoophelper.util.VALUE_ZERO
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class TacticCanvas(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var path: Path = Path()
    private var paint: Paint = Paint()
    private var paintStart: Paint = Paint()
    private var paintEnd: Paint = Paint()
    private var bitmap: Bitmap
    private var tacticCanvas: Canvas

    private var originX: Float = 0f
    private var originY: Float = 0f

    private var startX: Float = 0f
    private var startY: Float = 0f

    init {
        // bitmap
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Canvas
        tacticCanvas = Canvas(bitmap)
        tacticCanvas.drawColor(resources.getColor(R.color.basil_background, null))

        // Paint
        paint.apply {
            color = resources.getColor(R.color.basil_green_dark, null)
            strokeWidth = 5f
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        paintStart.apply {
            color = resources.getColor(R.color.basil_bg, null)
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }

        paintEnd.apply {
            color = resources.getColor(R.color.basil_orange, null)
            strokeWidth = 5f
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeCap = Paint.Cap.ROUND
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, 0f, 0f, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Tactic.vPagerSwipe.value = false
                tacticCanvas.drawCircle(event.x, event.y, 10f, paintStart)
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
            tacticCanvas.drawPath(path, paint)
            startX = curlX
            startY = curlY
        } else {
            path.quadTo(startX, startY, stopX, stopY)
            tacticCanvas.drawPath(path, paint)
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
            var crossX = VALUE_TEN
            var crossY = VALUE_TEN

            // quadrant
            when {
                (dx < 0f && abs(dy) <= 10f) || (dx > 0f && abs(dy) <= 10f) -> {
                    crossX = VALUE_ZERO
                    crossY = VALUE_TEN
                }
                (abs(dx) <= 10f && dy > 0f) || (abs(dx) <= 10f && dy < 0f) -> {
                    crossX = VALUE_TEN
                    crossY = VALUE_ZERO
                }
                (dx > 0f && dy > 0f) || (dx < 0f && dy < 0f) -> {
                    crossX = VALUE_TEN
                    crossY = VALUE_TEN
                }

                (dx < 0f && dy > 0f) || (dx > 0f && dy < 0f) -> {
                    crossX = -VALUE_TEN
                    crossY = VALUE_TEN
                }
            }
            tacticCanvas.drawLine(
                startX - crossX,
                startY - crossY,
                startX + crossX,
                startY + crossY,
                paintEnd
            )
        } else {
            val arrowX = VALUE_TEN
            val arrowY = VALUE_TEN
            tacticCanvas.drawLine(
                startX - arrowX,
                startY - arrowY,
                startX + arrowX,
                startY + arrowY,
                paintEnd
            )
            tacticCanvas.drawLine(
                startX - arrowX,
                startY + arrowY,
                startX + arrowX,
                startY - arrowY,
                paintEnd
            )
        }
    }

    fun clear() {
        tacticCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        tacticCanvas.drawColor(resources.getColor(R.color.basil_background, null))
    }
}
