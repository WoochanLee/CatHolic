package com.woody.cat.holic.presentation.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMainBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.main.like.LikeFragment
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.PostingViewModelFactory
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.UserFragment
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import com.woody.cat.holic.presentation.upload.UploadActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var postingViewModel: PostingViewModel

    private lateinit var galleryFragment: GalleryFragment
    private lateinit var likeFragment: LikeFragment
    private lateinit var userFragment: UserFragment
    private lateinit var fragments: Array<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
        }

        mainViewModel = ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java).apply {
            binding.mainViewModel = this
            setToolbarTitle(getString(R.string.gallery))

            eventStartUploadActivity.observeEvent(this@MainActivity, {
                startActivity(Intent(this@MainActivity, UploadActivity::class.java))
            })

            eventMoveToSignInTabWithToast.observeEvent(this@MainActivity, {
                binding.tlMain.getTabAt(MainTab.TAB_USER.position)?.select()
                Toast.makeText(applicationContext, R.string.need_to_sign_in, Toast.LENGTH_LONG).show()
            })

            eventStartProfileActivity.observeEvent(this@MainActivity, { userId ->
                startActivity(ProfileActivity.getIntent(this@MainActivity, userId))
            })

            eventShowLikeListDialog.observeEvent(this@MainActivity, { postingItem ->
                LikeListDialog.Builder()
                    .setLikeUserList(postingItem.likedUserIds)
                    .create()
                    .show(supportFragmentManager, LikeListDialog::class.java.name)
            })
        }

        signViewModel = ViewModelProvider(this, SignViewModelFactory()).get(SignViewModel::class.java).apply {
            binding.signViewModel = this
        }

        postingViewModel = ViewModelProvider(this, PostingViewModelFactory()).get(PostingViewModel::class.java).apply {
            eventMoveToSignInTabWithToast.observeEvent(this@MainActivity, {
                binding.tlMain.getTabAt(MainTab.TAB_USER.position)?.select()
                Toast.makeText(applicationContext, R.string.need_to_sign_in, Toast.LENGTH_LONG).show()
            })
        }

        if (savedInstanceState == null) {
            initFragments()
        } else {
            restoreFragments()
        }
        initMainTab()
        if (savedInstanceState != null) {
            binding.tlMain.getTabAt(MainTab.TAB_USER.position)?.select()
        }
    }

    private fun initMainTab() {
        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) = onTabSelected(tab)
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val iconId = when (MainTab.tabFromPosition(tab.position)) {
                    MainTab.TAB_GALLERY -> R.drawable.ic_cloud_data_empty
                    MainTab.TAB_LIKE -> R.drawable.ic_heart_empty
                    MainTab.TAB_USER -> R.drawable.ic_user_empty
                }

                tab.icon = ContextCompat.getDrawable(this@MainActivity, iconId)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedTab = MainTab.tabFromPosition(tab.position)
                mainViewModel.currentFragment = selectedTab

                val iconId = when (selectedTab) {
                    MainTab.TAB_GALLERY -> {
                        showFragment(galleryFragment)
                        mainViewModel.apply {
                            setToolbarTitle(getString(R.string.gallery))
                            setVisibleUploadFab(true)
                            setVisibleOrderSwitch(true)
                            refreshVisiblePostingOrder(MainTab.TAB_GALLERY)
                        }
                        R.drawable.ic_cloud_data_fill
                    }
                    MainTab.TAB_LIKE -> {
                        showFragment(likeFragment)
                        mainViewModel.apply {
                            setToolbarTitle(getString(R.string.like))
                            setVisibleUploadFab(true)
                            setVisibleOrderSwitch(true)
                            refreshVisiblePostingOrder(MainTab.TAB_LIKE)
                        }
                        R.drawable.ic_heart_fill
                    }
                    MainTab.TAB_USER -> {
                        showFragment(userFragment)
                        mainViewModel.apply {
                            setToolbarTitle(getString(R.string.profile))
                            setVisibleUploadFab(false)
                            setVisibleOrderSwitch(false)
                        }
                        R.drawable.ic_user_fill
                    }
                }

                tab.icon = ContextCompat.getDrawable(this@MainActivity, iconId)
            }
        })
    }

    private fun initFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, GalleryFragment().apply { galleryFragment = this }, GalleryFragment::class.java.name)
            .add(R.id.main_container, LikeFragment().apply { likeFragment = this }, LikeFragment::class.java.name)
            .add(R.id.main_container, UserFragment().apply { userFragment = this }, UserFragment::class.java.name)
            .commit()

        supportFragmentManager.fragments.size

        fragments = arrayOf(galleryFragment, likeFragment, userFragment)

        showFragment(galleryFragment)
    }

    private fun restoreFragments() {
        galleryFragment = supportFragmentManager.findFragmentByTag(GalleryFragment::class.java.name) as GalleryFragment
        likeFragment = supportFragmentManager.findFragmentByTag(LikeFragment::class.java.name) as LikeFragment
        userFragment = supportFragmentManager.findFragmentByTag(UserFragment::class.java.name) as UserFragment

        fragments = arrayOf(galleryFragment, likeFragment, userFragment)
    }

    private fun showFragment(fragment: Fragment) {
        fragments.forEach {
            supportFragmentManager.beginTransaction()
                .hide(it)
                .commit()
        }

        supportFragmentManager.beginTransaction()
            .show(fragment)
            .commit()
    }
/*
    private fun showProfileMenuPopup(userId: String) {
        val popup = PopupMenu(this, binding.ibProfileMenu)
        popup.inflate(R.menu.my_profile_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_my_profile -> {
                    startActivity(ProfileActivity.getIntent(this, userId))
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        popup.show()
    }*/
}