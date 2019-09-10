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
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ShareCompat
import androidx.viewpager.widget.ViewPager
import android.view.WindowManager
import androidx.core.content.ContextCompat


interface MediaListener {
    fun onPlayClicked(audio: Audio)
    fun onSeek(progress: Int)
    fun needUpdate()
    fun share(audio: Audio)
}

class MainActivity : AppCompatActivity(), MediaListener {

    private lateinit var viewModel: MainViewModel
    private val adapter = TabAdapter(supportFragmentManager)
    private var mediaPlayer: MediaPlayer? = null
    private var nowPlaying: String? = null

    private val backgrounds = listOf(
        R.drawable.app_bg_1,
        R.drawable.app_bg_2,
        R.drawable.app_bg_3,
        R.drawable.app_bg_4,
        R.drawable.app_bg_5,
        R.drawable.app_bg_6,
        R.drawable.app_bg_7
    )

    private val statusBarColors = listOf(
        R.color.status_bar_1,
        R.color.status_bar_2,
        R.color.status_bar_3,
        R.color.status_bar_4,
        R.color.status_bar_5,
        R.color.status_bar_6,
        R.color.status_bar_7
    )

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

        setUpViewPager()
    }

    private fun setUpViewPager() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val selBg = position % backgrounds.size
                container.background = getDrawable(backgrounds[selBg])
                updateStatusBarColor(position)
            }
        })
    }

    private fun updateStatusBarColor(position: Int) {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val selColor = position % statusBarColors.size
        window.statusBarColor = ContextCompat.getColor(this, statusBarColors[selColor])
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


    private fun getPlayerState() = PlayerState(
        nowPlaying,
        mediaPlayer?.isPlaying == true,
        mediaPlayer?.currentPosition ?: 0,
        mediaPlayer?.duration ?: 0
    )

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

    override fun share(audio: Audio) {
        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText("Escucha ${audio.name} de ${audio.author}: ${audio.url}")
            .intent
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(shareIntent)
        }
    }
}

data class PlayerState(
    val audioId: String? = null,
    val isPlaying: Boolean = false,
    var progress: Int = 0,
    val duration: Int = 0
)
