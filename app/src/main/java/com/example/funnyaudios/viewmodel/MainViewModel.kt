package com.example.funnyaudios.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.funnyaudios.model.Audio
import com.google.firebase.firestore.FirebaseFirestore

class MainViewModel : ViewModel() {

    private val TAG = MainViewModel::class.java.simpleName

    private val db = FirebaseFirestore.getInstance()
    var liveDataAuthors = MutableLiveData<List<String>>()

    init {
        db.collection("audio")
            .get()
            .addOnSuccessListener { response ->
                val audios = response.toObjects(Audio::class.java)
                val authors = audios.groupBy { it.author }.keys.toList().sorted()
                liveDataAuthors.postValue(authors)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting audios", exception)
            }
    }
}
