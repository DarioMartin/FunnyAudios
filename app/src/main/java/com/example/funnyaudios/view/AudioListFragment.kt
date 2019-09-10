package com.example.funnyaudios.view

import android.content.Context
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
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.audio_list_fragment.*

class AudioListFragment : Fragment() {

    companion object {
        val AUTHOR_NAME = "author_name"

        fun newInstance(author: String): Fragment {
            val fragment = AudioListFragment()
            val args = Bundle()
            args.putString(AUTHOR_NAME, author)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: AudioListViewModel
    private lateinit var adapter: AudioListAdapter
    private lateinit var author: String
    private var listener: MediaListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MediaListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        author = arguments?.getString(AUTHOR_NAME) ?: ""
        return inflater.inflate(R.layout.audio_list_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        adapter.addSubscription((activity as MainActivity).subject)
    }

    override fun onPause() {
        super.onPause()
        adapter.disposeSubscription()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AudioListViewModel::class.java)

        viewModel.liveDataAudios.observe(
            viewLifecycleOwner,
            Observer { audios -> updateList(audios) })

        viewModel.getAudios(author)

        recyclerViewAudio.layoutManager = LinearLayoutManager(context)
        adapter = AudioListAdapter(listener)
        recyclerViewAudio.adapter = this.adapter
    }

    private fun updateList(audios: List<Audio>) {
        progressBar.visibility = View.GONE
        adapter.setAudios(audios)
    }

}
