package com.example.funnyaudios.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.funnyaudios.view.AudioListFragment

class TabAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val authors = mutableListOf<String>()
    private val fragments = mutableListOf<Fragment>()

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = authors.size

    override fun getPageTitle(position: Int) = authors[position]

    fun setAuthors(authors: List<String>) {
        this.authors.clear()
        this.authors.addAll(authors)
        fragments.addAll(authors.map { AudioListFragment.newInstance(it) })
        notifyDataSetChanged()
    }

}
