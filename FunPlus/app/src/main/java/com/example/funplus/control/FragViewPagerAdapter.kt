package com.example.funplus.control

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class FragViewPagerAdapter(fragManager: FragmentManager): FragmentStatePagerAdapter(fragManager) {
    private lateinit var fragment: Fragment
    /**
     * Return the Fragment associated with a specified position.
     */
    override fun getItem(position: Int): Fragment {
        when(position) {
            0 ->
                fragment = NumberFrag()
            1 ->
                fragment = LetterFrag()
        }
        return fragment
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return 2
    }
}