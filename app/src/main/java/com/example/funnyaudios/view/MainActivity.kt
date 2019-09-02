package com.example.funnyaudios.view

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.funnyaudios.R
import com.example.funnyaudios.adapter.TabAdapter
import com.example.funnyaudios.model.Audio
import com.example.funnyaudios.viewmodel.MainViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*

interface MediaListener {
    fun onPlayClicked(audio: Audio)
    fun onSeek(progress: Int)
    fun needUpdate()
}

class MainActivity : AppCompatActivity(), MediaListener {

    private lateinit var viewModel: MainViewModel
    private val adapter = TabAdapter(supportFragmentManager)
    private var mediaPlayer: MediaPlayer? = null
    private var nowPlaying: String? = null

    var subject: BehaviorSubject<PlayerState> = BehaviorSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.liveDataAuthors.observe(this, Observer { authors -> updateAuthors(authors) })

        viewPager.adapter = this.adapter
        tabs.setupWithViewPager(viewPager)

        val observable: Observable<PlayerState> =
            Observable.create { emitter -> emitter.onNext(getPlayerState()) }
        observable.subscribe(subject)

    }

    private fun updateAuthors(authors: List<String>) {
        adapter.setAuthors(authors)
    }

    override fun onPlayClicked(audio: Audio) {
        when {
            audio.id != nowPlaying -> initPlayer(audio)
            mediaPlayer?.isPlaying == true -> {
                playerPause()
            }
            else -> {
                playerPlay()
            }
        }
    }

    override fun onSeek(progress: Int) {
        if (mediaPlayer?.isPlaying == true) mediaPlayer?.seekTo(progress)
        subject.onNext(getPlayerState())
    }

    override fun needUpdate() {
        subject.onNext(getPlayerState())
    }

    private fun initPlayer(audio: Audio) {
        killMediaPlayer()
        mediaPlayer = MediaPlayer.create(this, Uri.parse(audio.url))
        mediaPlayer?.setOnCompletionListener { killMediaPlayer() }
        nowPlaying = audio.id
        playerPlay()
    }


    private fun getPlayerState(): PlayerState {
        val playerState = PlayerState(
            nowPlaying,
            mediaPlayer?.isPlaying == true,
            mediaPlayer?.currentPosition ?: 0,
            mediaPlayer?.duration ?: 0
        )
        Log.d("GAGA", playerState.toString())
        return playerState
    }

    private fun killMediaPlayer() {
        playerPause()
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
        nowPlaying = null
        subject.onNext(getPlayerState())
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            playerPause()
        }
    }

    private fun playerPlay() {
        mediaPlayer?.start()
        subject.onNext(getPlayerState())
    }

    private fun playerPause() {
        mediaPlayer?.pause()
        subject.onNext(getPlayerState())
    }

    override fun onDestroy() {
        super.onDestroy()
        killMediaPlayer()
    }
}

data class PlayerState(
    val audioId: String? = null,
    val isPlaying: Boolean = false,
    var progress: Int = 0,
    val duration: Int = 0
)
