package com.woody.cat.holic.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMainBinding
import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.main.like.LikeFragment
import com.woody.cat.holic.presentation.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    val galleryFragment = GalleryFragment()
    val likeFragment = LikeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .apply {
                lifecycleOwner = this@MainActivity
            }

        viewModel =
            ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                binding.viewModel = this
                eventStartUploadActivity.observe(this@MainActivity, {
                    startActivity(Intent(this@MainActivity, UploadActivity::class.java))
                })
            }

        initMainTab()
        replaceFragment(galleryFragment)
    }

    private fun initMainTab() {
        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon = when (tab?.position) {
                    0 -> ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.ic_cloud_data_empty
                    )
                    else -> ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_heart_empty)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        tab.icon = ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.ic_cloud_data_fill
                        )
                        replaceFragment(galleryFragment)
                    }
                    else -> {
                        tab?.icon =
                            ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_heart_fill)
                        replaceFragment(likeFragment)
                    }
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }
}