package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.game_end.*
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class PlayActivity : AppCompatActivity(), SensorEventListener {

    private var isTimerRunning = false
    private var elapsedTime = 60
    private var points = 0
    private lateinit var  sensorManager:SensorManager
    private lateinit var linearAcceleration: Sensor

    private var sensorEnabled = true

    private var skipAmount = 3

    private val skipThreshold = 30f
    private val minTimeBetweenSkips = 1000
    private var lastSkipTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val diffSetting = intent.extras?.getInt("timerset")
        if (diffSetting == 1) elapsedTime = 60
        else if (diffSetting == 2) elapsedTime = 90
        else if (diffSetting == 0) elapsedTime = 45
        else if (diffSetting == 3) {
            val sharedPref = getSharedPreferences(
                getString(R.string.settings_file), Context.MODE_PRIVATE)
            if (sharedPref.contains(getString(R.string.custom_length))) {
                elapsedTime = sharedPref.getInt(getString(R.string.custom_length), -1)
            }
        }
        else elapsedTime = 60


        //Start the game
        start_button.setOnClickListener {
            start_button.visibility = View.GONE
            startTimer()
        }


        //Skip current figure if skips left
        skip_button.setOnClickListener {
            var currentTime = System.currentTimeMillis()
            if ((currentTime - lastSkipTime) > minTimeBetweenSkips) {
                if (skipCheck()) {
                    this.gameView.skipCurrent()
                    lastSkipTime = currentTime
                }
            }

        }

        //Back button back to main menu
        back_from_play.setOnClickListener {
            finish()
        }

        //Acceleration sensor
        if (sensorEnabled) {

            //Sensor stuff largely adapted from
            // https://stackoverflow.com/questions/51710147/kotlin-using-motion-sensor
            this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
                this.linearAcceleration = it
            }
        }


    }

    //This is called when the game ends
    private fun gameEnd() {

        points = GameView.getPointsEnd()
        //Switch to game end view
        setContentView(R.layout.game_end)
        scoreText.setText(points.toString())


        submit_results_button.setOnClickListener {
            val difficulty = intent.extras?.getInt("timerset")
            //Submit scores here
            val name = nameEdit.text
            val score = points
            doAsync {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "highScores"
                ).build()
                val hscore = HighScore(
                    uid = null,
                    username = name.toString(),
                    score = score,
                    difficulty = difficulty!! + 1,
                    timestamp = System.currentTimeMillis()
                )
                db.highScoreDao().insert(hscore)

                db.close()
            }
            finish()
        }

        back_from_game_end.setOnClickListener {
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        if (sensorEnabled) {
            sensorManager.registerListener(
                this,
                this.linearAcceleration,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }


    override fun onPause() {
        super.onPause()
        if (sensorManager != null) {
            sensorManager.unregisterListener(this)
        }
    }


    //Timer how to do from https://stackoverflow.com/a/6702767
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            timer.setText(turnIntoTime(elapsedTime)) //this is the textview
            if (elapsedTime < 0) {
                gameEnd()
            }
        }
    }

    protected fun startTimer() {
        isTimerRunning = true
        Timer().schedule(object : TimerTask() {
            override fun run() {
                elapsedTime -= 1 //increase every sec
                //Stop the timer when the timer is out
                if (elapsedTime < 0) {
                    isTimerRunning = false
                    cancel()
                }
                mHandler.obtainMessage(1).sendToTarget()
            }
        }, 0, 1000)
    }

    //Formatting the timer on top of the screen
    private fun turnIntoTime(timeStamp: Int): String {
        var timeLeft = timeStamp
        var minutes = 0
        while (timeLeft > 60) {
            timeLeft -= 60
            minutes += 1
        }
        var timeLeftSeconds = ""
        if (timeLeft < 10) {
            timeLeftSeconds = "0" + timeLeft.toString()
        } else {
            timeLeftSeconds = timeLeft.toString()
        }
        var timerString = minutes.toString() + ":" + timeLeftSeconds
        return timerString
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Unnecessary
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //Adapted from https://stackoverflow.com/a/32803134
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION)  {
            var currentTime = System.currentTimeMillis()
            if ((currentTime - lastSkipTime) > minTimeBetweenSkips) {
                var x = event.values[0]
                var y = event.values[1]
                var z = event.values[2]
                var acceleration = sqrt(
                    x.pow(2) + y.pow(2) + z.pow(2)
                )
                if (acceleration > skipThreshold) {
                    lastSkipTime = currentTime;
                    if (skipCheck()) {
                        this.gameView.skipCurrent()
                    }
                }
            }



        }
    }

    //Check if the player has any skips left
    private fun skipCheck(): Boolean {
        if (skipAmount > 0) {
            skipAmount -= 1
            skip_text.text = "Skips left: " + skipAmount.toString()
            return true
        }
        return false
    }


}
