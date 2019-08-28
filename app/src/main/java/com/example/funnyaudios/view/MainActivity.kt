package com.example.funnyaudios.view

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.funnyaudios.R
import com.example.funnyaudios.adapter.TabAdapter
import com.example.funnyaudios.model.Audio
import com.example.funnyaudios.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

interface MediaListener {
    fun onPlayClicked(audio: Audio)
}

class MainActivity : AppCompatActivity(), MediaListener {

    private lateinit var viewModel: MainViewModel
    private val adapter = TabAdapter(supportFragmentManager)
    private var mediaPlayer: MediaPlayer? = null
    private var nowPlaying: String? = null


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

    override fun onPlayClicked(audio: Audio) {
        when {
            audio.id != nowPlaying -> initPlayer(audio)
            mediaPlayer?.isPlaying == true -> mediaPlayer?.pause()
            else -> mediaPlayer?.start()
        }
    }

    private fun initPlayer(audio: Audio) {
        killMediaPlayer()
        mediaPlayer = MediaPlayer.create(this, Uri.parse(audio.url))
        mediaPlayer?.setOnCompletionListener { killMediaPlayer() }
        mediaPlayer?.start()
        nowPlaying = audio.id
    }

    private fun killMediaPlayer() {
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
