package com.wwinc.moviewall.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wwinc.moviewall.popular.Popular_Wallpapers
import com.wwinc.moviewall.topRated.Top_Rated

class TabLayout_Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> Popular_Wallpapers()
            1 -> Top_Rated()
            else -> Popular_Wallpapers()
        }
    }

}