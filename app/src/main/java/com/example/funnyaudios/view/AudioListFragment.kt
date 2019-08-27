package com.example.funnyaudios.view

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.example.funnyaudios.R
import com.example.funnyaudios.model.Audio

class AudioListFragment : Fragment() {

    companion object {
        fun newInstance() = AudioListFragment()
    }

    private lateinit var viewModel: AudioListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.audio_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AudioListViewModel::class.java)

        viewModel.getAudios()
    }

    private fun updateList(audios: List<Audio>) {



    }

}
