package com.example.funnyaudios.view

import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.funnyaudios.R
import com.example.funnyaudios.adapter.AudioListAdapter
import com.example.funnyaudios.model.Audio
import com.example.funnyaudios.viewmodel.AudioListViewModel
import kotlinx.android.synthetic.main.audio_list_fragment.*

class AudioListFragment : Fragment(), AudioListAdapter.AuidoListener {

    companion object {
        fun newInstance() = AudioListFragment()
    }

    private lateinit var viewModel: AudioListViewModel
    private lateinit var adapter: AudioListAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var nowPlaying: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewAudio.layoutManager = LinearLayoutManager(context)
        adapter = AudioListAdapter(this)
        recyclerViewAudio.adapter = this.adapter
    }


    override fun onPlayClicked(audio: Audio) {
        when {
            audio.id != nowPlaying -> initPlayer(audio)
            mediaPlayer?.isPlaying == true -> mediaPlayer?.pause()
            else -> mediaPlayer?.start()
        }
    }

    override fun onSharedClicked(audio: Audio) {
    }

    private fun initPlayer(audio: Audio) {
        killMediaPlayer()
        mediaPlayer = MediaPlayer.create(context, Uri.parse(audio.url))
        mediaPlayer?.setOnCompletionListener { killMediaPlayer() }
        mediaPlayer?.start()
        nowPlaying = audio.id
    }

    private fun killMediaPlayer() {
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AudioListViewModel::class.java)

        viewModel.liveDataAudios.observe(
            viewLifecycleOwner,
            Observer { audios -> updateList(audios) })
    }

    private fun updateList(audios: List<Audio>) {
        adapter.addAudios(audios)
    }

}
