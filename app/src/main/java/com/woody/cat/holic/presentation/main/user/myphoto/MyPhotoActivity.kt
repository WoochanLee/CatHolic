package com.woody.cat.holic.presentation.main.user.myphoto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMyPhotoBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.service.download.PhotoDownloadService
import com.woody.cat.holic.presentation.upload.UploadActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyPhotoActivity : BaseActivity() {

    private lateinit var binding: ActivityMyPhotoBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MyPhotoViewModel

    private lateinit var postingAdapter: MyPhotoPostingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMyPhotoBinding>(this, R.layout.activity_my_photo).apply {
            lifecycleOwner = this@MyPhotoActivity
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(MyPhotoViewModel::class.java).apply {
            binding.viewModel = this
            postingAdapter = MyPhotoPostingAdapter(this@MyPhotoActivity, this)

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

            eventRefreshData.observeEvent(this@MyPhotoActivity, {
                initPagingFlow()
            })

            eventShowCommentDialog.observeEvent(this@MyPhotoActivity, { postingItem ->
                CommentDialog.newInstance(supportFragmentManager, postingItem)
            })

            eventShowLikeListDialog.observeEvent(this@MyPhotoActivity, { postingItem ->
                LikeListDialog.newInstance(supportFragmentManager, postingItem)
            })

            eventStartUploadActivity.observeEvent(this@MyPhotoActivity, {
                startActivity(Intent(this@MyPhotoActivity, UploadActivity::class.java))
            })

            eventStartPhotoDownload.observeEvent(this@MyPhotoActivity, { imageUrl ->
                startService(PhotoDownloadService.getIntent(this@MyPhotoActivity, imageDownloadUrl = imageUrl))
            })

            eventShowDeleteWarningDialog.observeEvent(this@MyPhotoActivity, { (userId, postingId) ->
                AlertDialog.Builder(this@MyPhotoActivity)
                    .setTitle(getString(R.string.delete_photo))
                    .setMessage(getString(R.string.do_you_really_want_to_delete_this_photo))
                    .setPositiveButton(getString(R.string.delete_2)) { _, _ ->
                        viewModel.deletePosting(userId, postingId)
                    }.setNegativeButton(R.string.cancel, null)
                    .show()
            })
        }

        binding.rvMainGallery.adapter = postingAdapter

        initToolbar()
    }

    private var pagingJob: Job? = null

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
}