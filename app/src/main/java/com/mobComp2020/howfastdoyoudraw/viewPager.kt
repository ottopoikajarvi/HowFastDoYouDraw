package com.mobComp2020.howfastdoyoudraw

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.room.Room
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.highscore_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

private const val ARG_OBJECT = "object" //For bundle to fragment

//Fragment showing a leaderboard for a difficulty
class highScoreFragment : Fragment() {

    var diffiInteger: Int? = 0  //1=easy, 2=normal ...
    private lateinit var fragContext: Context  //Used for adapter and db

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.highscore_fragment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragContext = context
    }

    override fun onResume() {
        super.onResume()
        refreshList(diffiInteger)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bundle = arguments
        diffiInteger = bundle?.getInt(ARG_OBJECT)
        if (diffiInteger == 1) {
            textView6.text = getString(R.string.easy_leaderboard)
        }
        else if (diffiInteger == 2) {
            textView6.text = getString(R.string.normal_leaderboard)
        }
        else if (diffiInteger == 3) {
            textView6.text = getString(R.string.hard_leaderboard)
        }
        else if (diffiInteger == 4) {
            textView6.text = getString(R.string.custom_leaderboard)
        }
    }

    private fun refreshList(difficulty: Int?) {
        doAsync {
            val db = Room.databaseBuilder(
                fragContext,
                AppDatabase::class.java,
                "highScores"
            ).build()
            val highscores = db.highScoreDao().getHighScores(difficulty)
            db.close()

            uiThread {
                if (highscores.isNotEmpty()) {

                    val adapter = leaderboardAdapter(fragContext, highscores)
                    list.adapter = adapter
                } else{
                    list.adapter = null
                }
            }
        }

    }
}

//Creates fragments for ViewPager
class scorePagerAdapter(fragmentManager: FragmentManager, private val difficulties: List<Int>) :
    FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val fragment = highScoreFragment()
        fragment.arguments = Bundle().apply{
            putInt(ARG_OBJECT, difficulties[position]) //Different difficulties correspond to different integers
        }
        return fragment
    }

    override fun getCount(): Int {
        return difficulties.size
    }
    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0) {
            return "Easy"
        }
        else if (position == 1) {
            return "Normal"
        }
        else if (position == 2) {
            return "Hard"
        }
        else if (position == 3) {
            return "Custom"
        }
        else return "ERROR"
    }
}