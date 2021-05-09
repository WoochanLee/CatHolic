package com.woody.cat.holic.presentation.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMainBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.main.like.LikeFragment
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.UserFragment
import com.woody.cat.holic.presentation.main.user.UserViewModel
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import com.woody.cat.holic.presentation.service.fcm.CatHolicFirebaseMessagingService.Companion.DEEP_LINK_QUERY_POSTING_ID
import com.woody.cat.holic.presentation.upload.UploadActivity
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

class MainActivity : BaseActivity() {

    companion object {
        private const val KEY_CURRENT_TAB_POSITION = "KEY_CURRENT_TAB_POSITION"
    }

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var postingViewModel: PostingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
            lifecycleOwner = this@MainActivity
        }

        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java).apply {
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
                LikeListDialog.newInstance(supportFragmentManager, postingItem)
            })

            eventShowToast.observeEvent(this@MainActivity, { stringRes ->
                Toast.makeText(this@MainActivity, stringRes, Toast.LENGTH_SHORT).show()
            })

            eventFinishApp.observeEvent(this@MainActivity, {
                finish()
            })
        }

        signViewModel = ViewModelProvider(this, viewModelFactory).get(SignViewModel::class.java).apply {
            binding.signViewModel = this
        }

        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java).apply {
            eventShowGuide.observeEvent(this@MainActivity, {
                binding.tlMain.getTabAt(MainTab.TAB_GALLERY.position)?.select()
                mainViewModel.setGuideVisible(true)
            })
        }

        postingViewModel = ViewModelProvider(this, viewModelFactory).get(PostingViewModel::class.java).apply {
            binding.postingViewModel = this

            eventMoveToSignInTabWithToast.observeEvent(this@MainActivity, {
                binding.tlMain.getTabAt(MainTab.TAB_USER.position)?.select()
                Toast.makeText(applicationContext, R.string.need_to_sign_in, Toast.LENGTH_LONG).show()
            })

            eventShowPostingDetail.observeEvent(this@MainActivity, { postingItem ->
                PostingDetailDialog.newInstance(supportFragmentManager, postingItem)
            })
        }

        if (savedInstanceState == null) {
            createFragments()
            initMainTab()
        } else {
            initMainTab()
            val beforeTabPosition = savedInstanceState.getInt(KEY_CURRENT_TAB_POSITION)
            if (beforeTabPosition >= 0) {
                binding.tlMain.getTabAt(beforeTabPosition)?.select()
            }
        }

        checkDeepLink()
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
                mainViewModel.currentTab = selectedTab

                val iconId = when (selectedTab) {
                    MainTab.TAB_GALLERY -> {
                        showFragment(GalleryFragment::class)
                        mainViewModel.apply {
                            setToolbarTitle(getString(R.string.gallery))
                            setVisibleUploadFab(true)
                            setVisibleOrderSwitch(true)
                            refreshVisiblePostingOrder(MainTab.TAB_GALLERY)
                        }
                        R.drawable.ic_cloud_data_fill
                    }
                    MainTab.TAB_LIKE -> {
                        showFragment(LikeFragment::class)
                        mainViewModel.apply {
                            setToolbarTitle(getString(R.string.like))
                            setVisibleUploadFab(true)
                            setVisibleOrderSwitch(true)
                            refreshVisiblePostingOrder(MainTab.TAB_LIKE)
                        }
                        R.drawable.ic_heart_fill
                    }
                    MainTab.TAB_USER -> {
                        showFragment(UserFragment::class)
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

    private fun createFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, GalleryFragment(), GalleryFragment::class.java.name)
            .add(R.id.main_container, LikeFragment(), LikeFragment::class.java.name)
            .add(R.id.main_container, UserFragment(), UserFragment::class.java.name)
            .commitNow()

        showFragment(GalleryFragment::class)
    }

    private fun checkDeepLink() {
        intent.data?.getQueryParameter(DEEP_LINK_QUERY_POSTING_ID)?.let { postingId ->
            postingViewModel.handleDeepLinkToPostingDetail(postingId)
        }
    }

    private fun showFragment(fragmentClass: KClass<*>) {
        supportFragmentManager.apply {
            fragments.forEach { fragment ->
                beginTransaction()
                    .hide(fragment)
                    .commit()
            }

            beginTransaction()
                .show(findFragmentByTag(fragmentClass.java.name)!!)
                .commit()
        }
    }

    override fun onBackPressed() {
        binding.tlMain.apply {
            if (selectedTabPosition == 0) {
                mainViewModel.handleBackKeyFinish()
            } else {
                selectTab(getTabAt(0))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_CURRENT_TAB_POSITION, binding.tlMain.selectedTabPosition)
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