package com.mobComp2020.howfastdoyoudraw

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.settings_activity.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        //Back arrow
        back_from_settings.setOnClickListener {
            //Parempi koska ei riko firmiksen back-nappia
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
                    })
                .setNegativeButton(R.string.cancel_string,
                    DialogInterface.OnClickListener { dialog, id ->
                        // Do nothing
                    })
            // Create the AlertDialog object and return it
            builder.create()
            builder.show()
        }
    }
}