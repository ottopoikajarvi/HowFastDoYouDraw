package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * This is the custom game view
 */
class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint = Paint()
    private var path = Path()

    //initialize the paint and everything else needed
    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 20.0f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        //

        path.reset()
        var xPoint = Random.nextFloat()*canvas.width
        var yPoint = Random.nextFloat()*canvas.height
        path.moveTo(xPoint, yPoint)
        for (i in 1..5){
            var lastXPoint = xPoint
            var lastYPoint = yPoint
            next@ while(true) {
                xPoint += Random.nextInt(-canvas.width / 2, canvas.width / 2)
                yPoint += Random.nextInt(-canvas.height / 2, canvas.height / 2)
                if (xPoint > 0 && xPoint < canvas.width && yPoint > 0 && yPoint < canvas.height) {
                    break@next
                }
                xPoint = lastXPoint
                yPoint = lastYPoint
            }
            path.cubicTo(xPoint, lastYPoint, yPoint, lastXPoint, xPoint, yPoint)

        }

        canvas.apply {
            drawPath(path, paint)
        }
    }

}