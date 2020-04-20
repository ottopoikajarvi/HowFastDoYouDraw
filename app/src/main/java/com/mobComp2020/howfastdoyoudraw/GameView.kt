package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.toast

/**
 * This is the custom game view
 */
class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint = Paint()
    private var circlePaint = Paint()
    private var fingerPath = Path()
    private var drawPath = Path()
    private var movement = Path()
    private var movementMeas = PathMeasure()
    private var pathMeas = PathMeasure()
    private var currentCircle = Path()
    private var distance = 0f
    private var victory = false

    //initialize the paint and everything else needed
    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 20.0f
        paint.style = Paint.Style.STROKE
        drawPath.moveTo(5.0f, 5.9f)
        drawPath.lineTo(500.0f, 400.0f)
        pathMeas.setPath(drawPath, false)
        currentCircle.addCircle(5.0f, 5.9f, 50.0f, Path.Direction.CW)

        circlePaint.color = Color.RED
        circlePaint.strokeWidth = 20.0f
        circlePaint.style = Paint.Style.STROKE

    }

    override fun onDraw(canvas: Canvas) {
        //
        canvas.apply {
            drawPath(drawPath, paint)
            if (!currentCircle.isEmpty) {
                drawPath(currentCircle, circlePaint)
            }

        }
    }

    //This function gets called when user touches on the screen
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                movement.moveTo(x!!, y!!)
                fingerPath.reset()
                fingerPath.addCircle(x!!, y!!, 100.0f, Path.Direction.CW)
                var region = Region()
                val clip = Region(0, 0, width, height)
                region.setPath(fingerPath, clip)
                var region2 = Region()
                region2.setPath(currentCircle, clip)
                if (region.op(region2, Region.Op.INTERSECT)) {
                    // Collision!
                    Log.d("collision", "yes")
                    currentCircle.reset()
                    var pos: FloatArray = floatArrayOf(0f, 0f)
                    distance += 10.0f
                    pathMeas.getPosTan(distance, pos, null)
                    Log.d("collisionpositions", pos[0].toString())
                    currentCircle.addCircle(pos[0], pos[1], 10.0f, Path.Direction.CW)

                }
                invalidate()

            }
            MotionEvent.ACTION_UP -> {
                if (!victory) {
                    fingerPath.reset()
                    movement.reset()
                    currentCircle.reset()
                    currentCircle.addCircle(5.0f, 5.9f, 50.0f, Path.Direction.CW)
                    distance = 0f
                }
                invalidate()

            }
            MotionEvent.ACTION_MOVE -> {
                fingerPath.reset()
                fingerPath.addCircle(x!!, y!!, 100.0f, Path.Direction.CW)
                movement.lineTo(x!!, y!!)
                movementMeas.setPath(movement, false)
                movement.reset()
                movement.moveTo(x!!, y!!)
                var region = Region()
                val clip = Region(0, 0, width, height)
                region.setPath(fingerPath, clip)
                var region2 = Region()
                region2.setPath(currentCircle, clip)
                if (region.op(region2, Region.Op.INTERSECT)) {
                    // Collision!
                    currentCircle.reset()
                    var pos: FloatArray = floatArrayOf(0f, 0f)
                    distance += movementMeas.length
                    if (distance > pathMeas.length) {
                        context.toast("VICTORY")
                        victory = true
                        distance = 0f
                        return true
                    }
                    pathMeas.getPosTan(distance, pos, null)
                    currentCircle.addCircle(pos[0], pos[1], 50.0f, Path.Direction.CW)
                }
                invalidate()

            }
        }


        return true
    }

}