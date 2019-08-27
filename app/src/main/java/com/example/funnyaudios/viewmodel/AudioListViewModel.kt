package com.example.funnyaudios.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.funnyaudios.model.Audio
import com.google.firebase.firestore.FirebaseFirestore

class AudioListViewModel : ViewModel() {

    private val TAG = AudioListViewModel::class.java.simpleName

    private val db = FirebaseFirestore.getInstance()
    var liveDataAudios = MutableLiveData<List<Audio>>()

    init {
        db.collection("audio")
            .get()
            .addOnSuccessListener { response ->
                val audios = response.toObjects(Audio::class.java)
                liveDataAudios.postValue(audios)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting audios", exception)
            }
    }
}
