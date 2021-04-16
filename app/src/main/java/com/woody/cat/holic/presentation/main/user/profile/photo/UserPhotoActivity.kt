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
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.posting.PostingAdapter
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.PostingViewModelFactory
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

        postingViewModel = ViewModelProvider(this, PostingViewModelFactory()).get(PostingViewModel::class.java).apply {
            postingAdapter = PostingAdapter(this@UserPhotoActivity, this)

            eventShowPostingDetail.observeEvent(this@UserPhotoActivity, { postingItem ->
                PostingDetailDialog.Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(supportFragmentManager, PostingDetailDialog::class.java.name)
            })

            eventShowCommentDialog.observeEvent(this@UserPhotoActivity, { postingItem ->
                CommentDialog.Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(supportFragmentManager, CommentDialog::class.java.name)
            })

            eventShowLikeListDialog.observeEvent(this@UserPhotoActivity, { postingItem ->
                LikeListDialog.Builder()
                    .setLikeUserList(postingItem.likedUserIds)
                    .create()
                    .show(supportFragmentManager, LikeListDialog::class.java.name)
            })

            eventStartProfileActivity.observeEvent(this@UserPhotoActivity, { userId ->
                startActivity(ProfileActivity.getIntent(this@UserPhotoActivity, userId))
            })

            eventMoveToSignInTabWithToast.observeEvent(this@UserPhotoActivity, {
                Toast.makeText(applicationContext, R.string.need_to_sign_in, Toast.LENGTH_LONG).show()
                finish()
            })
        }

        userPhotoViewModel = ViewModelProvider(this, UserPhotoViewModelFactory(userId)).get(UserPhotoViewModel::class.java).apply {
            binding.viewModel = this

            initPagingFlow()

            lifecycleScope.launch {
                postingAdapter.loadStateFlow.collectLatest { loadStates ->
                    val refreshState = loadStates.refresh
                    setLoading(refreshState is LoadState.Loading)

                    if (refreshState is LoadState.Error) {
                        //TODO: handle network error
                    }

                    if (refreshState is LoadState.NotLoading) {
                        setIsListEmpty(postingAdapter.itemCount == 0)
                    }
                }
            }

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