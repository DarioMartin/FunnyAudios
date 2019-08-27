package com.example.funnyaudios.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.funnyaudios.model.Audio
import com.google.firebase.firestore.FirebaseFirestore

class AudioListViewModel : ViewModel() {

    private val TAG = AudioListViewModel::class.java.simpleName

    private val db = FirebaseFirestore.getInstance()
    lateinit var livedataAudios: MutableLiveData<List<Audio>>

    fun getAudios(){
        db.collection("audio")
            .get()
            .addOnSuccessListener { response ->
                val audios = response.toObjects(Audio::class.java)
                livedataAudios.postValue(audios)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting audios", exception)
            }
    }
}
