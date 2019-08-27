package com.example.funnyaudios.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.funnyaudios.R
import com.example.funnyaudios.model.Audio
import kotlinx.android.synthetic.main.audio_list_item.view.*

class AudioListAdapter(val listener: AuidoListener) :
    RecyclerView.Adapter<AudioListAdapter.AudiosViewHolder>() {

    interface AuidoListener {
        fun onPlayClicked(audio: Audio)
        fun onSharedClicked(audio: Audio)
    }

    private val audios = mutableListOf<Audio>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiosViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.audio_list_item, parent, false)
        return AudiosViewHolder(view)
    }

    override fun getItemCount() = audios.size

    override fun onBindViewHolder(vh: AudiosViewHolder, position: Int) {
        vh.bindItem(position)
    }

    fun addAudios(audios: List<Audio>) {
        this.audios.addAll(audios)
        notifyDataSetChanged()
    }

    inner class AudiosViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(position: Int) {
            val audio = audios[position]
            itemView.name.text = audio.name
            itemView.description.text = audio.description
            itemView.playButton.setOnClickListener { listener.onPlayClicked(audio) }
        }

    }

}
