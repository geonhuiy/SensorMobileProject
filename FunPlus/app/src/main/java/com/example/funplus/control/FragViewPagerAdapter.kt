package com.example.funplus.control

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class FragViewPagerAdapter(fragManager: FragmentManager): FragmentStatePagerAdapter(fragManager) {
    private lateinit var fragment: Fragment
    private lateinit var tabTitle: String
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
    /**
     * Sets tab title
     */
    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 ->
                tabTitle = "123"
            1 ->
                tabTitle = "ABC"
        }
        return tabTitle
    }

    override fun getItemPosition(`object`: Any): Int {
        if (`object` is NumberFrag) {
            return PagerAdapter.POSITION_NONE
        }
        if(`object` is LetterFrag) {
            return  PagerAdapter.POSITION_NONE
        }
        return PagerAdapter.POSITION_UNCHANGED
    }
}