package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Create testing database
        /*
        val names = listOf("Mike", "John", "Jessica", "Alice", "Annie")
        val scores = listOf(5, 15, 7, 12, 5)
        doAsync {
            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "highScores"
            ).build()

            for (x in 0..4) {
                val hscore = HighScore(
                    uid = null,
                    username = names[x],
                    score = scores[x],
                    difficulty = 3
                )
                db.highScoreDao().insert(hscore)
            }
            db.close()
        }*/


        val sharedPref = getSharedPreferences(
            getString(R.string.settings_file), Context.MODE_PRIVATE)

        //Radiogroup for difficulty selection, chosen difficulty is saved to prefs
        if (sharedPref.contains(getString(R.string.chosen_diff))) {
            val diffSetting = sharedPref.getInt(getString(R.string.chosen_diff), -1)
            radioGroup.check(radioGroup.getChildAt(diffSetting).id)
        }
        else {
            radioGroup.check(radioGroup.getChildAt(1).id)
            with (sharedPref.edit()) {
                putInt(getString(R.string.chosen_diff), 1)
                apply()
            }
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            val idx: Int = radioGroup.indexOfChild(radioButton)
            with (sharedPref.edit()) {
                putInt(getString(R.string.chosen_diff), idx)
                apply()
            }
            //Log.d("radio", checkedId.toString())
        }

        //Play button
        play_button.setOnClickListener {
            val intent = Intent(applicationContext, PlayActivity::class.java)

            //Bundle for game timer
            val checkedId = radioGroup.checkedRadioButtonId
            val radioButton = radioGroup.findViewById<RadioButton>(checkedId)
            val idx: Int = radioGroup.indexOfChild(radioButton)
            intent.putExtra("timerset", idx)
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
