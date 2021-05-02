package com.woody.cat.holic.presentation.upload

import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityUploadBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.makeCustomAlbumWidget
import com.woody.cat.holic.framework.base.observeEvent
import com.yanzhenjie.album.Album
import javax.inject.Inject


class UploadActivity : BaseActivity() {

    companion object {
        const val ALBUM_COLUMN_COUNT = 3
        const val ALBUM_MAX_SELECT_COUNT = 10
    }

    lateinit var viewModel: UploadViewModel
    lateinit var binding: ActivityUploadBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val uploadSmallPreviewAdapter: UploadSmallPreviewAdapter by lazy {
        UploadSmallPreviewAdapter(this, viewModel)
    }
    private val uploadBigPreviewAdapter: UploadBigPreviewAdapter by lazy {
        UploadBigPreviewAdapter(this, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityUploadBinding>(this, R.layout.activity_upload).apply {
            lifecycleOwner = this@UploadActivity
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(UploadViewModel::class.java).apply {
            binding.viewModel = this

            eventSelectImage.observeEvent(this@UploadActivity, {
                getAlbumPhotos()
            })

            eventMoveToNextPreviewPage.observeEvent(this@UploadActivity, {
                moveToNextPreviewPage()
            })

            eventMoveToPrevPreviewPage.observeEvent(this@UploadActivity, {
                moveToPrevPreviewPage()
            })

            eventMoveToTargetPreviewPage.observeEvent(this@UploadActivity, { position ->
                moveToTargetPreviewPage(position)
            })

            eventRemoveTargetPreviewPage.observeEvent(this@UploadActivity, { position ->
                removeTargetPreviewPage(position)
            })

            eventShowToast.observeEvent(this@UploadActivity, { stringRes ->
                Toast.makeText(applicationContext, stringRes, Toast.LENGTH_LONG).show()
            })

            eventFinish.observeEvent(this@UploadActivity, {
                finish()
            })
        }

        initToolbar()
        initRecyclerView()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.tbUpload)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun initRecyclerView() {
        binding.rvUploadSmall.adapter = uploadSmallPreviewAdapter
        binding.vpUploadBig.apply {
            adapter = uploadBigPreviewAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    uploadBigPreviewAdapter.changeArrowButtonStatus(position)
                }
            })
        }
    }

    private fun moveToNextPreviewPage() {
        binding.vpUploadBig.apply {
            setCurrentItem(currentItem + 1, true)
        }
    }

    private fun moveToPrevPreviewPage() {
        binding.vpUploadBig.apply {
            setCurrentItem(currentItem - 1, true)
        }
    }

    private fun moveToTargetPreviewPage(targetPage: Int) {
        binding.vpUploadBig.setCurrentItem(targetPage, true)
    }

    private fun removeTargetPreviewPage(targetPage: Int) {
        viewModel.removePreviewData(targetPage)
        refreshAdapterStatus()
    }

    private fun getAlbumPhotos() {
        Album.image(this)
            .multipleChoice()
            .widget(makeCustomAlbumWidget(title = R.string.select_cats))
            .camera(true)
            .columnCount(ALBUM_COLUMN_COUNT)
            .selectCount(ALBUM_MAX_SELECT_COUNT)
            .onResult breaker@{ checkedList ->
                if (checkedList.size == 0) {
                    return@breaker
                }

                viewModel.addPreviewData(checkedList.map { it.path })
                refreshAdapterStatus()
            }
            .start()
    }

    private fun refreshAdapterStatus() {
        uploadSmallPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.changeArrowButtonStatus(binding.vpUploadBig.currentItem)
    }
}