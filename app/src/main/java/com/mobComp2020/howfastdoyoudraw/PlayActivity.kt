package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
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

    //Helping variables
    private var isTimerRunning = false
    private var timeRemaining = 60
    private var points = 0
    private lateinit var  sensorManager:SensorManager
    private lateinit var linearAcceleration: Sensor

    //Tracks whether motion sensor is enabled
    private var sensorEnabled = true

    //For skipping feature
    private var skipAmount = 3
    private val skipThreshold = 30f
    private val minTimeBetweenSkips = 1000
    private var lastSkipTime: Long = 0

    private lateinit var sharedPref:SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        //Get the settings
        sharedPref = getSharedPreferences(
            getString(R.string.settings_file), Context.MODE_PRIVATE)

        //Get the difficulty and set the game time based on it
        val diffSetting = intent.extras?.getInt("timerset")
        if (diffSetting == 1) timeRemaining = 60
        else if (diffSetting == 2) timeRemaining = 90
        else if (diffSetting == 0) timeRemaining = 45
        //if custom difficulty
        else if (diffSetting == 3) {
            //set user set difficulty
            if (sharedPref.contains(getString(R.string.custom_length))) {
                timeRemaining = sharedPref.getInt(getString(R.string.custom_length), -1)
            }
        } //default to 60 seconds
        else timeRemaining = 60

        //format time left for the timer
        val startTime = turnIntoTime(timeRemaining)
        timer.text = startTime


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

        //Check whether the user has set motion sensor enabled/disabled (enabled by default)
        sensorEnabled = sharedPref.getBoolean(getString(R.string.motion_set), true)
        //Acceleration sensor
        //Sensor stuff largely adapted from
        // https://stackoverflow.com/questions/51710147/kotlin-using-motion-sensor
        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.let {
            this.linearAcceleration = it
        }



    }

    //This is called when the game ends
    private fun gameEnd() {

        //Get the points the player got
        points = GameView.getPointsEnd()
        //Switch to game end view
        setContentView(R.layout.game_end)
        scoreText.setText(points.toString())

        //Put the player name from last time as default
        if (sharedPref.contains(getString(R.string.player_name))) {
            nameEdit.setText(sharedPref.getString(getString(R.string.player_name), "ABC"))
        }

        //Submit the score to the database when user clicks the submit score button
        submit_results_button.setOnClickListener {
            //Get difficulty level, player name and the score
            val difficulty = intent.extras?.getInt("timerset")
            val name = nameEdit.text
            val score = points

            //Save the name for the next time for the convenience of the player
            with (sharedPref.edit()) {
                putString(getString(R.string.player_name), name.toString())
                apply()
            }

            //Save the score
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

        //If the player clicks the back arrow in the left corner, don't save the score
        back_from_game_end.setOnClickListener {
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        //Register the sensor, if using it is enabled
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
        //Unregister the sensor
        sensorManager.unregisterListener(this)
    }


    //Timer how to do from https://stackoverflow.com/a/6702767
    //Handles the game time and setting the timer text
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            timer.setText(turnIntoTime(timeRemaining)) //this is the textview
            if (timeRemaining < 0) {
                gameEnd()
            }
        }
    }

    protected fun startTimer() {
        isTimerRunning = true
        //Game timer
        Timer().schedule(object : TimerTask() {
            override fun run() {
                timeRemaining -= 1 //decrease every sec
                //Stop the timer when the timer is out
                if (timeRemaining < 0) {
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
        while (timeLeft > 59) {
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

    //Check the sensor values for skipping feature
    override fun onSensorChanged(event: SensorEvent?) {
        //Adapted from https://stackoverflow.com/a/32803134
        if (event?.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION)  {
            var currentTime = System.currentTimeMillis()
            //Enough time since last skip
            if ((currentTime - lastSkipTime) > minTimeBetweenSkips) {
                var x = event.values[0]
                var y = event.values[1]
                var z = event.values[2]
                var acceleration = sqrt(
                    x.pow(2) + y.pow(2) + z.pow(2)
                )
                //If the acceleration value is high enough
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
