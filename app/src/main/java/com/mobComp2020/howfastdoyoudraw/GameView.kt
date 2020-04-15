package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.lang.Math.abs
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * This is the custom game view
 */
class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint = Paint()
    private var paintLine = Paint() //Paint for outlines
    private var path = Path()
    private var pairs = mutableListOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(1, 0))
    //initialize the paint and everything else needed
    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 40.0f
        paint.style = Paint.Style.STROKE
        paintLine.color = Color.BLACK
        paintLine.strokeWidth = 48.0f
        paintLine.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        //Generates a path that is then drawn to canvas
        pairs.shuffle()
        path.reset()
        var xPoint = 0.0f
        var yPoint = 0.0f
        var linesDrawn = 0
        //path.moveTo(xPoint, yPoint)
        //4 points per path
        for (i in 0..3){
            var lastXPoint = xPoint
            var lastYPoint = yPoint
            var pair = pairs[i]
            //Log.d("pair", pair.toString())

            //Log.d("size", canvas.width.toString() + canvas.height.toString())
            xPoint = Random.nextInt(40, (canvas.width / 2) - 40).toFloat() + (pair.first * (canvas.width / 2))
            yPoint = Random.nextInt(40, (canvas.height / 2) - 40).toFloat() + (pair.second * (canvas.height / 2))

            /* Original variant of shape generation, removed becuz bad
            next@ while(true) {
                xPoint += Random.nextInt(-canvas.width / 2, canvas.width / 2)
                yPoint += Random.nextInt(-canvas.height / 2, canvas.height / 2)
                if (xPoint > 10 && xPoint < canvas.width - 10 && yPoint > 10 && yPoint < canvas.height - 10) {
                    if ((xPoint - lastXPoint).absoluteValue > (canvas.width / 8) && (yPoint - lastYPoint).absoluteValue > (canvas.height / 8)) {
                        break@next
                    }
                }
                xPoint = lastXPoint
                yPoint = lastYPoint
                limit += 1
                if (limit > 3 && linesDrawn > 2) break@next
            }*/
            if (i == 0) { //Starting location for drawing
                path.moveTo(xPoint, yPoint)
            }
            else if (i == 2 || i == 3) {
                path.quadTo(xPoint, lastYPoint, xPoint, yPoint)}
            else {
                path.quadTo(yPoint, lastXPoint, xPoint, yPoint)
            }


        }

        canvas.apply {
            drawPath(path, paintLine)
            drawPath(path, paint)
        }
    }

}