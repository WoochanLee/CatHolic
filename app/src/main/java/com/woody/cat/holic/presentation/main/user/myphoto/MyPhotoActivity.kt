package com.woody.cat.holic.presentation.main.user.myphoto

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.map
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityMyPhotoBinding
import com.woody.cat.holic.presentation.main.MainPostingAdapter
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.presentation.main.user.myphoto.viewmodel.MyPhotoViewModel
import com.woody.cat.holic.presentation.main.user.myphoto.viewmodel.MyPhotoViewModelFactory
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

            lifecycleScope.launch {
                flow.collectLatest { pagingData ->
                    postingAdapter.submitData(pagingData.map { it.mapToPostingItem(viewModel.getCurrentUser()?.userId) })
                }
            }

            lifecycleScope.launch {
                postingAdapter.loadStateFlow.collectLatest { loadStates ->
                    setLoading(loadStates.refresh is LoadState.Loading)

                    if(loadStates.refresh is LoadState.Error) {
                        //TODO: handle network error
                    }
                }
            }

            eventRefreshData.observe(this@MyPhotoActivity, {
                postingAdapter.refresh()
            })
        }

        binding.rvMainGallery.adapter = postingAdapter

        initToolbar()
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