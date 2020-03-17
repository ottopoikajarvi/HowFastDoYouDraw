package com.mobComp2020.howfastdoyoudraw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Play button
        play_button.setOnClickListener {
            val intent = Intent(applicationContext, PlayActivity::class.java)
            startActivity(intent)
        }

        //Settings
        settings_button.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }

        //High scores
        stats_button.setOnClickListener {
            val intent = Intent(applicationContext, HighScoreActivity::class.java)
            startActivity(intent)
        }



    }
}
