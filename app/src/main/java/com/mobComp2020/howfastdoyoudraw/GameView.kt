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
    private var paintLine = Paint()
    private var path = Path()

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

        path.reset()
        var xPoint = (Random.nextFloat()*0.85f + 0.13f)*canvas.width
        var yPoint = (Random.nextFloat()*0.85f + 0.13f)*canvas.height
        var linesDrawn = 0
        path.moveTo(xPoint, yPoint)
        //4 draws per path
        for (i in 1..4){
            var lastXPoint = xPoint
            var lastYPoint = yPoint
            var limit = 0
            //Log.d("size", canvas.width.toString() + canvas.height.toString())
            xPoint = Random.nextInt(Math.max(((lastXPoint - canvas.width) / 2).toInt(), 20),
                Math.min(((lastXPoint + canvas.width) / 2).toInt(), canvas.width - 20)).toFloat()
            yPoint = Random.nextInt(Math.max(((lastYPoint - canvas.height) / 2).toInt(), 20),
                Math.min(((lastYPoint + canvas.height) / 2).toInt(), canvas.height - 20)).toFloat()

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
            path.quadTo(xPoint, lastYPoint, xPoint, yPoint)
            linesDrawn += 1

        }

        canvas.apply {
            drawPath(path, paintLine)
            drawPath(path, paint)
        }
    }

}