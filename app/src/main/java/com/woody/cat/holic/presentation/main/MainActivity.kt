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
import com.woody.cat.holic.presentation.main.user.UserFragment
import com.woody.cat.holic.presentation.upload.UploadActivity
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val galleryFragment = GalleryFragment()
    private val likeFragment = LikeFragment()
    private val userFragment = UserFragment()

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
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val iconId = when (MainTab.tabFromPosition(tab.position)) {
                    MainTab.TAB_GALLERY -> R.drawable.ic_cloud_data_empty
                    MainTab.TAB_LIKE -> R.drawable.ic_heart_empty
                    MainTab.TAB_USER -> R.drawable.ic_user_empty
                }

                tab.icon = ContextCompat.getDrawable(this@MainActivity, iconId)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val iconId = when (MainTab.tabFromPosition(tab.position)) {
                    MainTab.TAB_GALLERY -> {
                        replaceFragment(galleryFragment)
                        R.drawable.ic_cloud_data_fill
                    }
                    MainTab.TAB_LIKE -> {
                        replaceFragment(likeFragment)
                        R.drawable.ic_heart_fill
                    }
                    MainTab.TAB_USER -> {
                        replaceFragment(userFragment)
                        R.drawable.ic_user_fill
                    }
                }

                tab.icon = ContextCompat.getDrawable(this@MainActivity, iconId)
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