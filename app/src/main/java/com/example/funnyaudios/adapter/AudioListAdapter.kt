package com.example.funnyaudios.adapter

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.funnyaudios.R
import com.example.funnyaudios.model.Audio
import com.example.funnyaudios.view.MediaListener
import com.example.funnyaudios.view.PlayerState
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.audio_list_item.view.*

class AudioListAdapter(val listener: MediaListener?) :
    RecyclerView.Adapter<AudioListAdapter.AudiosViewHolder>() {

    private var subject: BehaviorSubject<PlayerState>? = null
    private val audios = mutableListOf<Audio>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiosViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.audio_list_item, parent, false)
        return AudiosViewHolder(view, subject)
    }

    override fun getItemCount() = audios.size

    override fun onBindViewHolder(vh: AudiosViewHolder, position: Int) {
        vh.bindItem(position)
    }

    fun setAudios(audios: List<Audio>) {
        this.audios.clear()
        this.audios.addAll(audios)
        notifyDataSetChanged()
    }

    fun addSubscription(subject: BehaviorSubject<PlayerState>) {
        this.subject = subject
        notifyDataSetChanged()
    }

    fun disposeSubscription() {
        this.subject = null
        notifyDataSetChanged()
    }

    @SuppressLint("CheckResult")
    inner class AudiosViewHolder(view: View, private val subject: BehaviorSubject<PlayerState>?) :
        RecyclerView.ViewHolder(view) {
        private var latestPlayerEvent: PlayerState? = null
        private var audio: Audio? = null
        var currentProgress = 0
        private var lastUpdate = 0L

        private var handler = Handler()
        private var runnable: Runnable

        init {
            runnable = object : Runnable {
                override fun run() {
                    currentProgress += (System.currentTimeMillis() - lastUpdate).toInt()
                    lastUpdate = System.currentTimeMillis()
                    itemView.seekbar.progress = currentProgress
                    handler.postDelayed(this, 100)
                }
            }
            subject?.subscribe { playerEvent -> onPlayerUpdate(playerEvent) }
            listener?.needUpdate()
        }

        private fun onPlayerUpdate(playerEvent: PlayerState) {
            latestPlayerEvent = playerEvent
            if (audio?.id == playerEvent.audioId) {
                lastUpdate = System.currentTimeMillis()
                currentProgress = playerEvent.progress
                itemView.playPauseButton.setImageResource(if (playerEvent.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
                itemView.seekbar.isEnabled = true
                itemView.seekbar.max = playerEvent.duration
                itemView.seekbar.progress = playerEvent.progress
                if (playerEvent.isPlaying) {
                    handler.postDelayed(runnable, 100)
                } else {
                    handler.removeCallbacks(runnable)
                }
            } else {
                currentProgress = 0
                itemView.seekbar.max = 0
                itemView.seekbar.progress = currentProgress
                itemView.seekbar.isEnabled = false
                handler.removeCallbacks(runnable)
                itemView.playPauseButton.setImageResource(R.drawable.ic_play)
            }
        }

        fun bindItem(position: Int) {
            audio = audios[position]
            itemView.name.text = audio?.name
            itemView.playPauseButton.setOnClickListener {
                handler.removeCallbacks(runnable)
                listener?.onPlayClicked(audios[position])
            }
            itemView.duration.text = audio?.duration
            itemView.seekbar.isEnabled = false
            itemView.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, byUser: Boolean) {
                    if (byUser) listener?.onSeek(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    handler.removeCallbacks(runnable)
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })

            latestPlayerEvent?.let { onPlayerUpdate(it) }
        }

    }

}
