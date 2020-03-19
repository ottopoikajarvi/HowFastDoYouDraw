package com.mobComp2020.howfastdoyoudraw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.highscore_fragment.*

private const val ARG_OBJECT = "object" //For bundle to fragment

//Fragment showing a leaderboard for a difficulty
class highScoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.highscore_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = arguments
        textView6.text = bundle?.getInt(ARG_OBJECT).toString()
    }
}

//Creates fragments for ViewPager
class scorePagerAdapter(fragmentManager: FragmentManager, private val difficulties: List<Int>) :
    FragmentPagerAdapter(fragmentManager) {

    // 2
    override fun getItem(position: Int): Fragment {
        val fragment = highScoreFragment()
        fragment.arguments = Bundle().apply{
            putInt(ARG_OBJECT, difficulties[position]) //Different difficulties correspond to different integers
        }
        return fragment
    }

    // 3
    override fun getCount(): Int {
        return difficulties.size
    }
}