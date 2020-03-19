package com.mobComp2020.howfastdoyoudraw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_high_score.*

class HighScoreActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: scorePagerAdapter

    private var difficulties = listOf(1, 2, 3 ,4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)

        //Back button to main menu
        back_from_stats.setOnClickListener {
            finish()
        }

        //Swipe for different difficulties using ViewPager
        //See viewPager.kt
        viewPager = findViewById(R.id.pager)
        pagerAdapter = scorePagerAdapter(supportFragmentManager, difficulties)
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 2  // Start with Hard-scores
    }
}

