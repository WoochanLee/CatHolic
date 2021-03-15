package com.woody.cat.holic.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMainBinding
import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel =
            ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                eventStartUploadActivity.observe(this@MainActivity, {
                    startActivity(Intent(this@MainActivity, UploadActivity::class.java))
                })
            }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, GalleryFragment())
            .commit()
    }
}