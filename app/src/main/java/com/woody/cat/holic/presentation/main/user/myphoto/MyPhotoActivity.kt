package com.woody.cat.holic.presentation.main.user.myphoto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.upload.UploadActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyPhotoActivity : BaseActivity() {

    private lateinit var binding: ActivityMyPhotoBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var myPhotoViewModel: MyPhotoViewModel

    private lateinit var postingAdapter: MyPhotoPostingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMyPhotoBinding>(this, R.layout.activity_my_photo).apply {
            lifecycleOwner = this@MyPhotoActivity
        }

        myPhotoViewModel = ViewModelProvider(this, viewModelFactory).get(MyPhotoViewModel::class.java).apply {
            binding.viewModel = this
            postingAdapter = MyPhotoPostingAdapter(this@MyPhotoActivity, this)

            initPagingFlow()

            lifecycleScope.launch {
                postingAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val refreshState = loadStates.refresh
                        setLoading(refreshState is LoadState.Loading)

                        if (refreshState is LoadState.Error) {
                            Toast.makeText(this@MyPhotoActivity, R.string.network_fail, Toast.LENGTH_LONG).show()
                        }

                        if (refreshState is LoadState.NotLoading) {
                            setIsListEmpty(postingAdapter.itemCount == 0)
                            binding.rvMyPhoto.scrollToPosition(0)
                        }
                    }
            }

            eventRefreshData.observeEvent(this@MyPhotoActivity, {
                initPagingFlow()
            })

            eventShowPostingDetail.observeEvent(this@MyPhotoActivity, { postingItem ->
                PostingDetailDialog.newInstance(this@MyPhotoActivity, supportFragmentManager, postingItem)
            })

            eventStartUploadActivity.observeEvent(this@MyPhotoActivity, {
                startActivity(Intent(this@MyPhotoActivity, UploadActivity::class.java))
            })

            eventShowDeleteWarningDialog.observeEvent(this@MyPhotoActivity, { (userId, postingId) ->
                AlertDialog.Builder(this@MyPhotoActivity)
                    .setTitle(getString(R.string.delete_posting))
                    .setMessage(getString(R.string.do_you_really_want_to_delete_this_posting))
                    .setPositiveButton(getString(R.string.delete_2)) { _, _ ->
                        myPhotoViewModel.deletePosting(userId, postingId)
                    }.setNegativeButton(R.string.cancel, null)
                    .show()
            })

            eventChangeUserPostingOrder.observeEvent(this@MyPhotoActivity, {
                myPhotoViewModel.changeToNextPostingOrder()
                myPhotoViewModel.refreshVisiblePostingOrder()
                postingAdapter.refresh()
            })

            eventShowToast.observeEvent(this@MyPhotoActivity, { stringRes ->
                Toast.makeText(this@MyPhotoActivity, stringRes, Toast.LENGTH_SHORT).show()
            })
        }

        binding.rvMyPhoto.adapter = postingAdapter

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