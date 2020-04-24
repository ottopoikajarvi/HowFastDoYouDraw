package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random


/**
 * This is the custom game view
 */
class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint = Paint()

    //Variables for difficulties
    private var diffSetting = 0
    private var drawAmount = 3
    private var gameLength = 60
    private var circleRad = 100.0f

    private var paintLine = Paint() //Paint for outlines
    private var path = Path()
    private var pairs = mutableListOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(1, 0))

    //Variables needed for the game
    private var circlePaint = Paint()
    private var fingerPath = Path()
    private var movement = Path()
    private var movementMeas = PathMeasure()
    private var pathMeas = PathMeasure()
    private var currentCircle = Path()
    private var distance = 0f
    private var victory = false
    private var start = true
    private var startPos = floatArrayOf(0f, 0f)

    //Tracks points
    private var points = 0

    //For skipping
    private var skipAmount = 3
    private var skipping = false

    //initialize the paint and everything else needed
    init {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.settings_file), Context.MODE_PRIVATE)

        //Get the chosen difficulty
        if (sharedPref.contains(context.getString(R.string.chosen_diff))) {
            diffSetting = sharedPref.getInt(context.getString(R.string.chosen_diff), 1)
        }

        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paintLine.color = Color.BLACK
        paintLine.style = Paint.Style.STROKE
        //Different paint for the circle
        circlePaint.color = Color.RED
        circlePaint.strokeWidth = 20.0f
        circlePaint.style = Paint.Style.STROKE
        if (diffSetting == 1) {  //Normal difficulty
            drawAmount = 3
            circleRad = 100.0f

            paint.strokeWidth = 40.0f
            paintLine.strokeWidth = 48.0f
        }
        else if (diffSetting == 0) { //Easy
            drawAmount = 2
            circleRad = 120.0f

            paint.strokeWidth = 50.0f
            paintLine.strokeWidth = 60.0f
        }
        else if (diffSetting == 2) { //Hard
            drawAmount = 4
            circleRad = 80.0f

            paint.strokeWidth = 36.0f
            paintLine.strokeWidth = 42.0f
        }
        else { //Custom
            if (sharedPref.contains(context.getString(R.string.custom_shape))) {
                val shapeSetting = sharedPref.getInt(context.getString(R.string.custom_shape), -1)
                if (shapeSetting == 0) drawAmount = 2
                else if (shapeSetting == 1) drawAmount = 3
                else if (shapeSetting == 2) drawAmount = 4
            }

            if (sharedPref.contains(context.getString(R.string.custom_width))) {
                val lineSetting = sharedPref.getInt(context.getString(R.string.custom_width), -1)
                if (lineSetting == 0) {
                    circleRad = 120.0f
                    paint.strokeWidth = 50.0f
                    paintLine.strokeWidth = 60.0f
                }
                else if (lineSetting == 1) {
                    circleRad = 100.0f
                    paint.strokeWidth = 40.0f
                    paintLine.strokeWidth = 48.0f
                }
                else if (lineSetting == 2) {
                    circleRad = 80.0f
                    paint.strokeWidth = 36.0f
                    paintLine.strokeWidth = 42.0f
                }
            }
        }


    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            //First path of the game
            if (start) {
                createNewPath()

                start = false
            }

            drawPath(path, paintLine)
            drawPath(path, paint)

            //If the red circle is defined
            if (!currentCircle.isEmpty) {
                drawPath(currentCircle, circlePaint)
            }

        }
    }

    //This function gets called when user touches on the screen
    //Involves the game logic
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                //Start the movement tracking
                movement.moveTo(x!!, y!!)
                fingerPath.reset()
                //Circle around the current position of the finger
                fingerPath.addCircle(x!!, y!!, circleRad, Path.Direction.CW)
                var region = Region()
                val clip = Region(0, 0, width, height)
                region.setPath(fingerPath, clip)
                var region2 = Region()
                region2.setPath(currentCircle, clip)
                //Check whether the circle around the finger position and the red circle
                //intersect
                if (region.op(region2, Region.Op.INTERSECT)) {
                    //Move the red circle further
                    currentCircle.reset()
                    var pos: FloatArray = floatArrayOf(0f, 0f)
                    distance += 10.0f
                    pathMeas.getPosTan(distance, pos, null)
                    currentCircle.addCircle(pos[0], pos[1], 10.0f, Path.Direction.CW)

                }
                //Draw the game components
                invalidate()

            }
            //User lifts their finger
            MotionEvent.ACTION_UP -> {
                //If the user did not follow the path to the end, reset all variables
                if (!victory) {
                    fingerPath.reset()
                    movement.reset()
                    currentCircle.reset()
                    currentCircle.addCircle(startPos[0], startPos[1], 50.0f, Path.Direction.CW)
                    distance = 0f
                }
                invalidate()

            }
            MotionEvent.ACTION_MOVE -> {
                fingerPath.reset()
                //Circle around the current position of the finger
                fingerPath.addCircle(x!!, y!!, circleRad, Path.Direction.CW)
                //add last movement to the movement PathMeasure
                movement.lineTo(x!!, y!!)
                movementMeas.setPath(movement, false)
                movement.reset()
                movement.moveTo(x!!, y!!)
                var region = Region()
                val clip = Region(0, 0, width, height)
                region.setPath(fingerPath, clip)
                var region2 = Region()
                region2.setPath(currentCircle, clip)
                //Check whether the circle around the finger position and the red circle
                //intersect
                if (region.op(region2, Region.Op.INTERSECT)) {
                    // Collision!
                    //Move the red circle further
                    currentCircle.reset()
                    var pos: FloatArray = floatArrayOf(0f, 0f)
                    distance += movementMeas.length
                    //If the user has followed the whole path, award a point and create a new
                    //path
                    if (distance > pathMeas.length) {
                        points += 1
                        setPointsAmount(points)
                        victory = true
                        distance = 0f
                        path.reset()
                        createNewPath()
                        return true
                    }
                    //If the user is not at the end of the path yet, move the red circle
                    //further
                    pathMeas.getPosTan(distance, pos, null)
                    currentCircle.addCircle(pos[0], pos[1], 50.0f, Path.Direction.CW)
                } else {
                    //The user missed the circle, reset all variables
                    if (!victory) {
                        fingerPath.reset()
                        movement.reset()
                        currentCircle.reset()
                        currentCircle.addCircle(startPos[0], startPos[1], 50.0f, Path.Direction.CW)
                        distance = 0f
                    }
                }
                invalidate()

            }
        }


        return true
    }

    //Creates a path for the user to solve
    private fun createNewPath() {
        //Generates a path that is then drawn to canvas
        pairs.shuffle()
        path.reset()
        var xPoint = 0.0f
        var yPoint = 0.0f
        var linesDrawn = 0
        var pair = Pair(0, 0)
        //path.moveTo(xPoint, yPoint)
        //4 points per path
        for (i in 0..drawAmount){
            var lastXPoint = xPoint
            var lastYPoint = yPoint
            pair = if (i > 3) {
                pairs[1]
            } else {
                pairs[i]
            }
            //Log.d("pair", pair.toString())

            //Log.d("size", canvas.width.toString() + canvas.height.toString())
            xPoint = Random.nextInt(50, (width / 2) - 40).toFloat() + (pair.first * (width / 2))
            yPoint = Random.nextInt(50, (height / 2) - 40).toFloat() + (pair.second * (height / 2))

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

        pathMeas.setPath(path, false)

        pathMeas.getPosTan(0f, startPos, null)
        currentCircle.addCircle(startPos[0], startPos[1], 50.0f, Path.Direction.CW)
        victory = false


    }

    //Skip the current shape, max 3 skips per game
    fun skipCurrent(){
        skipping = true
        skipAmount -= 1
        if (skipAmount < 0) {
            return
        }
        //Reset variables
        distance = 0f
        path.reset()
        currentCircle.reset()
        createNewPath()
        invalidate()
    }

    //Used by PlayActivity to get the amount of points at the end of the game
    companion object {
        var points = 0
        fun setPointsAmount(pointAmount: Int) {
            points = pointAmount
        }

        fun getPointsEnd(): Int = points


    }





}