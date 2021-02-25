package net.grandcentrix.blutufintegration.ui.detail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DetailViewPagerAdapter (fragment: Fragment, private val pages: List<Fragment>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position]

    fun getFragment(position: Int) = pages[position]
}