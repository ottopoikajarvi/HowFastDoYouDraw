package com.mobComp2020.howfastdoyoudraw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.game_end.*

class PlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)


        //Start the game
        start_button.setOnClickListener {
            start_button.visibility = View.GONE
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
        //Switch to game end view
        setContentView(R.layout.game_end)
        submit_results_button.setOnClickListener {
            //Submit scores here
            //TODO
            finish()
        }

        back_from_game_end.setOnClickListener {
            finish()
        }
    }

}
