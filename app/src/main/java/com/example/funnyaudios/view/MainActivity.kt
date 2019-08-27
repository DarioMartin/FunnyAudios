package com.example.funnyaudios.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.funnyaudios.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.container, AudioListFragment.newInstance())
            .commit()

    }
}
