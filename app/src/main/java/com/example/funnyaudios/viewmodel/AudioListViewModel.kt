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

    fun getAudios(author: String) {
        db.collection("audio")
            .whereEqualTo("author", author)
            .get()
            .addOnSuccessListener { documents ->
                val audios =
                    documents.map {
                        Audio(
                            id = it.id,
                            name = it.data["name"].toString(),
                            url = it.data["url"].toString(),
                            author = it.data["author"].toString(),
                            duration = it.data["duration"].toString()
                        )
                    }
                liveDataAudios.postValue(audios)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting audios", exception)
            }
    }
}
