package com.woody.cat.holic.presentation.main.user.myphoto

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMyPhotoBinding
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPhotoBinding
    private lateinit var viewModel: MyPhotoViewModel

    private lateinit var postingAdapter: MyPhotoPostingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMyPhotoBinding>(this, R.layout.activity_my_photo).apply {
            lifecycleOwner = this@MyPhotoActivity
        }

        viewModel = ViewModelProvider(this, MyPhotoViewModelFactory()).get(MyPhotoViewModel::class.java).apply {
            binding.viewModel = this
            postingAdapter = MyPhotoPostingAdapter(this@MyPhotoActivity, this)

            initPagingFlow()

            lifecycleScope.launch {
                postingAdapter.loadStateFlow.collectLatest { loadStates ->
                    setLoading(loadStates.refresh is LoadState.Loading)

                    if (loadStates.refresh is LoadState.Error) {
                        //TODO: handle network error
                    }
                }
            }

            eventRefreshData.observeEvent(this@MyPhotoActivity, {
                initPagingFlow()
            })

            eventShowCommentDialog.observeEvent(this@MyPhotoActivity, { postingItem ->
                CommentDialog.Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(supportFragmentManager, CommentDialog::class.java.name)
            })

            eventShowLikeListDialog.observeEvent(this@MyPhotoActivity, { postingItem ->
                LikeListDialog.Builder()
                    .setLikeUserList(postingItem.likedUserIds)
                    .create()
                    .show(supportFragmentManager, LikeListDialog::class.java.name)
            })
        }

        binding.rvMainGallery.adapter = postingAdapter

        initToolbar()
    }

    var pagingJob: Job? = null

    private fun MyPhotoViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = lifecycleScope.launch {
            getUploadedPostings().collectLatest { pagingData ->
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}