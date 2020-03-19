package com.mobComp2020.howfastdoyoudraw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        //Start the game
        start_button.setOnClickListener {
            start_button.visibility = View.GONE
        }

        //Submit high scores
        submit_results_button.setOnClickListener {
            //TODO
        }

        //Skip current figure
        skip_button.setOnClickListener {
            //TODO
        }

        //Back button back to main menu
        back_from_play.setOnClickListener {
            finish()
        }
    }
}
