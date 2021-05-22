package com.woody.cat.holic.presentation.main.user.profile.photo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityUserPhotoBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.base.shareDynamicLink
import com.woody.cat.holic.presentation.main.posting.PostingAdapter
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserPhotoActivity : BaseActivity() {

    companion object {

        private const val KEY_USER_ID = "KEY_USER_ID"

        fun getIntent(context: Context, userId: String): Intent {
            return Intent(context, UserPhotoActivity::class.java).apply {
                putExtra(KEY_USER_ID, userId)
            }
        }
    }

    private lateinit var binding: ActivityUserPhotoBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var userPhotoViewModel: UserPhotoViewModel
    private lateinit var postingViewModel: PostingViewModel

    private lateinit var postingAdapter: PostingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra(KEY_USER_ID) ?: run {
            finish()
            return
        }

        binding = DataBindingUtil.setContentView<ActivityUserPhotoBinding>(this, R.layout.activity_user_photo).apply {
            lifecycleOwner = this@UserPhotoActivity
        }

        postingViewModel = ViewModelProvider(this, viewModelFactory).get(PostingViewModel::class.java).apply {
            postingAdapter = PostingAdapter(this@UserPhotoActivity, this)

            eventShowPostingDetail.observeEvent(this@UserPhotoActivity, { postingItem ->
                PostingDetailDialog.newInstance(this@UserPhotoActivity, supportFragmentManager, postingItem)
            })

            eventShowCommentDialog.observeEvent(this@UserPhotoActivity, { postingItem ->
                CommentDialog.newInstance(supportFragmentManager, postingItem)
            })

            eventShowLikeListDialog.observeEvent(this@UserPhotoActivity, { postingItem ->
                LikeListDialog.newInstance(supportFragmentManager, postingItem)
            })

            eventStartProfileActivity.observeEvent(this@UserPhotoActivity, { userId ->
                startActivity(ProfileActivity.getIntent(this@UserPhotoActivity, userId))
            })

            eventMoveToSignInTabWithToast.observeEvent(this@UserPhotoActivity, {
                Toast.makeText(applicationContext, R.string.need_to_sign_in, Toast.LENGTH_LONG).show()
                finish()
            })

            eventSharePosting.observeEvent(this@UserPhotoActivity, { dynamicLink ->
                shareDynamicLink(R.string.I_really_want_to_show_you_this_cat, dynamicLink)
            })
        }

        userPhotoViewModel = ViewModelProvider(this, viewModelFactory).get(UserPhotoViewModel::class.java).apply {
            binding.viewModel = this

            this.userId = userId

            getProfile()

            initPagingFlow()

            lifecycleScope.launch {
                postingAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val refreshState = loadStates.refresh
                        setLoading(refreshState is LoadState.Loading)

                        if (refreshState is LoadState.Error) {
                            //TODO: handle network error
                        }

                        if (refreshState is LoadState.NotLoading) {
                            setIsListEmpty(postingAdapter.itemCount == 0)
                            binding.rvUserPhoto.scrollToPosition(0)
                        }
                    }
            }

            eventChangeUserPostingOrder.observeEvent(this@UserPhotoActivity, {
                userPhotoViewModel.changeToNextPostingOrder()
                userPhotoViewModel.refreshVisiblePostingOrder()
                postingAdapter.refresh()
            })

            eventRefreshData.observeEvent(this@UserPhotoActivity, {
                initPagingFlow()
            })
        }

        binding.rvUserPhoto.adapter = postingAdapter

        initToolbar()
    }

    private var pagingJob: Job? = null

    private fun UserPhotoViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = lifecycleScope.launch {
            getUserPostings().collectLatest { pagingData ->
                postingAdapter.submitData(pagingData)
            }
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.tbMyCatPhotos)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }
}