package com.example.funnyaudios.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.funnyaudios.R
import com.example.funnyaudios.adapter.TabAdapter
import com.example.funnyaudios.viewmodel.AudioListViewModel
import com.example.funnyaudios.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val adapter = TabAdapter(supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.liveDataAuthors.observe(this, Observer { authors -> updateAuthors(authors) })

        viewPager.adapter = this.adapter
        tabs.setupWithViewPager(viewPager)

    }

    private fun updateAuthors(authors: List<String>) {
        adapter.setAuthors(authors)
    }
}
