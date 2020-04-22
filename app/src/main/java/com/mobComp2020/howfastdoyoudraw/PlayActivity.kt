package com.mobComp2020.howfastdoyoudraw

import android.content.Context
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

class PlayActivity : AppCompatActivity() {

    private var isTimerRunning = false
    private var elapsedTime = 60
    private var points = 0

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


        //Skip current figure
        skip_button.setOnClickListener {
            //TODO
            //End game for demonstration purposes
            //Change content view for the game end

            gameEnd()
        }

        //Back button back to main menu
        back_from_play.setOnClickListener {
            finish()
        }
    }

    //This is called when the game ends
    private fun gameEnd() {

        points = GameView.getPointsEnd()

        Log.d("pointAmount", points.toString())
        //Switch to game end view
        setContentView(R.layout.game_end)
        scoreText.setText(points.toString())
        submit_results_button.setOnClickListener {
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
                    difficulty = 3,
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




}
