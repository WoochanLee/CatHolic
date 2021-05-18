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
import com.woody.cat.holic.presentation.main.follow.FollowFragment
import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.main.like.LikeFragment
import com.woody.cat.holic.presentation.main.notification.NotificationListDialog
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.UserFragment
import com.woody.cat.holic.presentation.main.user.UserViewModel
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import com.woody.cat.holic.presentation.service.fcm.CatHolicFirebaseMessagingService.Companion.DEEP_LINK_QUERY_COMMENT_ID
import com.woody.cat.holic.presentation.service.fcm.CatHolicFirebaseMessagingService.Companion.DEEP_LINK_QUERY_POSTING_ID
import com.woody.cat.holic.presentation.service.fcm.CatHolicFirebaseMessagingService.Companion.DEEP_LINK_QUERY_USER_ID
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

            eventShowNotificationDialog.observeEvent(this@MainActivity, {
                NotificationListDialog.newInstance(supportFragmentManager)
            })

            eventMoveToFollowTab.observeEvent(this@MainActivity, {
                moveToFollowTab()
            })

            eventMoveToLikeTab.observeEvent(this@MainActivity, {
                moveToLikeTab()
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
                mainViewModel.setVisibleGuide(true)
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

            eventShowCommentDialog.observeEvent(this@MainActivity, { postingItem ->
                CommentDialog.newInstance(supportFragmentManager, postingItem)
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

        checkDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            checkDeepLink(intent)
        }
    }

    private fun initMainTab() {
        binding.tlMain.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) = onTabSelected(tab)
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val iconId = when (MainTab.tabFromPosition(tab.position)) {
                    MainTab.TAB_GALLERY -> R.drawable.ic_cloud_data_empty
                    MainTab.TAB_FOLLOW -> R.drawable.ic_friends_empty
                    MainTab.TAB_LIKE -> R.drawable.ic_heart_empty
                    MainTab.TAB_USER -> R.drawable.ic_user_empty
                }

                tab.icon = ContextCompat.getDrawable(this@MainActivity, iconId)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedTab = MainTab.tabFromPosition(tab.position)
                mainViewModel.currentTab = selectedTab

                //val iconId: Int
                when (selectedTab) {
                    MainTab.TAB_GALLERY -> moveToGalleryTab()
                    MainTab.TAB_FOLLOW -> mainViewModel.onClickFollowTab()
                    MainTab.TAB_LIKE -> mainViewModel.onClickLikeTab()
                    MainTab.TAB_USER -> moveToUserTab()
                }
            }
        })
    }

    private fun moveToGalleryTab() {
        val selectedTab = binding.tlMain.getTabAt(MainTab.TAB_GALLERY.position)

        showFragment(GalleryFragment::class)
        mainViewModel.apply {
            setToolbarTitle(getString(R.string.gallery))
            setVisibleUploadFab(true)
            setVisibleOrderSwitch(true)
            setVisibleNotification(true)
            setVisibleEditProfile(false)
            refreshVisiblePostingOrder(MainTab.TAB_GALLERY)
        }
        selectedTab?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_cloud_data_fill)
    }

    private fun moveToFollowTab() {
        val selectedTab = binding.tlMain.getTabAt(MainTab.TAB_FOLLOW.position)

        showFragment(FollowFragment::class)
        mainViewModel.apply {
            setToolbarTitle(getString(R.string.following).toUpperCase(Locale.getDefault()))
            setVisibleUploadFab(true)
            setVisibleOrderSwitch(false)
            setVisibleNotification(false)
            setVisibleEditProfile(false)
        }
        selectedTab?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_friends_fill)
    }

    private fun moveToLikeTab() {
        val selectedTab = binding.tlMain.getTabAt(MainTab.TAB_LIKE.position)

        showFragment(LikeFragment::class)
        mainViewModel.apply {
            setToolbarTitle(getString(R.string.like))
            setVisibleUploadFab(true)
            setVisibleOrderSwitch(true)
            setVisibleNotification(false)
            setVisibleEditProfile(false)
            refreshVisiblePostingOrder(MainTab.TAB_LIKE)
        }
        selectedTab?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_heart_fill)
    }

    private fun moveToUserTab() {
        val selectedTab = binding.tlMain.getTabAt(MainTab.TAB_USER.position)

        showFragment(UserFragment::class)
        mainViewModel.apply {
            setToolbarTitle(getString(R.string.profile))
            setVisibleUploadFab(false)
            setVisibleOrderSwitch(false)
            setVisibleNotification(false)
            setVisibleEditProfile(true)
        }
        selectedTab?.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_user_fill)
    }

    private fun createFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, GalleryFragment(), GalleryFragment::class.java.name)
            .add(R.id.main_container, FollowFragment(), FollowFragment::class.java.name)
            .add(R.id.main_container, LikeFragment(), LikeFragment::class.java.name)
            .add(R.id.main_container, UserFragment(), UserFragment::class.java.name)
            .commitNow()

        showFragment(GalleryFragment::class)
    }

    private fun checkDeepLink(intent: Intent) {
        intent.data?.run {
            val postingId = getQueryParameter(DEEP_LINK_QUERY_POSTING_ID)
            val commentId = getQueryParameter(DEEP_LINK_QUERY_COMMENT_ID)
            val userId = getQueryParameter(DEEP_LINK_QUERY_USER_ID)
            postingId?.let {
                //TODO: highlight
                postingViewModel.handleDeepLinkToPostingDetail(postingId, commentId != null)
            }

            userId?.let {
                startActivity(ProfileActivity.getIntent(this@MainActivity, userId))
            }
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
        if (mainViewModel.isVisibleGuide.value == true) {
            mainViewModel.setMainGuideStatus(false)
            mainViewModel.setVisibleGuide(false)
            return
        }

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
}