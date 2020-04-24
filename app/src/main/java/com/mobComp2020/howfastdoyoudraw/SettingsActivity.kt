package com.mobComp2020.howfastdoyoudraw

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_activity.*
import org.jetbrains.anko.doAsync
import java.lang.Integer.valueOf


class SettingsActivity : AppCompatActivity() {

    override fun onBackPressed() { //Overridden to allow for text input to be saved
        val sharedPref = getSharedPreferences(
            getString(R.string.settings_file), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putInt(getString(R.string.custom_length), valueOf(editText.text.toString()))
            apply()
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val sharedPref = getSharedPreferences(
            getString(R.string.settings_file), Context.MODE_PRIVATE)

        if (sharedPref.contains(getString(R.string.motion_set))) {
            var motSetting = sharedPref.getBoolean(getString(R.string.motion_set), false)
            motioncontrol_switch.isChecked = motSetting
        }
        else {
            motioncontrol_switch.isChecked = true
            with (sharedPref.edit()) {
                putBoolean(getString(R.string.motion_set), true)
                apply()
            }
        }

        //Listener for motion control switch
        motioncontrol_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with (sharedPref.edit()) {
                    putBoolean(getString(R.string.motion_set), true)
                    apply()
                }
            } else {
                with (sharedPref.edit()) {
                    putBoolean(getString(R.string.motion_set), false)
                    apply()
                }
            }
        }

        //Back arrow
        back_from_settings.setOnClickListener {
            //Parempi koska ei riko firmiksen back-nappia
            with (sharedPref.edit()) {
                putInt(getString(R.string.custom_length), valueOf(editText.text.toString()))
                apply()
            }
            finish()
            /*
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)*/
        }

        scorewipe_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.confirm_string)
                .setPositiveButton(R.string.yes_string,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Delete scores
                        doAsync {
                            val db = Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java,
                                "highScores"
                            ).build()
                            db.highScoreDao().deleteHighScores()

                            db.close()
                        }
                    })
                .setNegativeButton(R.string.cancel_string,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Do nothing
                    })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }

        //Settings initialization and listeners for the radiogroups
        //Both are almost identical, maybe refactor with functions?
        if (sharedPref.contains(getString(R.string.custom_shape))) {
            val shapeSetting = sharedPref.getInt(getString(R.string.custom_shape), -1)
            radioGroupShape.check(radioGroupShape.getChildAt(shapeSetting).id)
        }
        else {
            radioGroupShape.check(radioGroupShape.getChildAt(1).id)
            with (sharedPref.edit()) {
                putInt(getString(R.string.custom_shape), 1)
                apply()
            }
        }
        //Listener always saves the change to prefs
        radioGroupShape.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = radioGroupShape.findViewById<RadioButton>(checkedId)
            val idx: Int = radioGroupShape.indexOfChild(radioButton)
            //Log.d("radio", idx.toString())
            with (sharedPref.edit()) {
                putInt(getString(R.string.custom_shape), idx)
                apply()
            }
        }

        if (sharedPref.contains(getString(R.string.custom_width))) {
            val lineSetting = sharedPref.getInt(getString(R.string.custom_width), -1)
            radioGroupLine.check(radioGroupLine.getChildAt(lineSetting).id)
        }
        else {
            radioGroupLine.check(radioGroupLine.getChildAt(1).id)
            with (sharedPref.edit()) {
                putInt(getString(R.string.custom_width), 1)
                apply()
            }
        }

        radioGroupLine.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = radioGroupLine.findViewById<RadioButton>(checkedId)
            val idx: Int = radioGroupLine.indexOfChild(radioButton)
            with (sharedPref.edit()) {
                putInt(getString(R.string.custom_width), idx)
                apply()
            }
        }

        //For game length setting
        //Text input
        if (sharedPref.contains(getString(R.string.custom_length))) {
            val lengthSetting = sharedPref.getInt(getString(R.string.custom_length), -1)
            editText.setText(lengthSetting.toString())
        }
        else {
            editText.setText("60")
            with (sharedPref.edit()) {
                putInt(getString(R.string.custom_length), 60)
                apply()
            }
        }
    }
}