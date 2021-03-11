package com.woody.cat.holic.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.woody.cat.holic.R
import com.woody.cat.holic.data.PhotoRepository
import com.woody.cat.holic.databinding.ActivityMainBinding
import com.woody.cat.holic.framework.FirebaseStoragePhotoDataSource
import com.woody.cat.holic.presentation.gallery.GalleryFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, GalleryFragment())
            .commit()
    }
}