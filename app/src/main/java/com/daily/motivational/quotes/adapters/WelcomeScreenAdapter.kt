package com.daily.motivational.quotes.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*


class WelcomeScreenAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    private val mList: MutableList<Fragment> = ArrayList()
    private val mTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return mList[position]
    }

    override fun getCount(): Int {
        return mList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mList.add(fragment)
        mTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitleList[position]
    }
}