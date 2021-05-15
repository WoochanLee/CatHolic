package com.woody.cat.holic.presentation.upload

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityUploadBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import javax.inject.Inject


class UploadActivity : BaseActivity() {

    companion object {
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

    private var cropResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            CropImage.getActivityResult(result.data)?.getUriFilePath(this, true)?.let { imageUri ->
                viewModel.addPreviewData(listOf(imageUri))
                refreshAdapterStatus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityUploadBinding>(this, R.layout.activity_upload).apply {
            lifecycleOwner = this@UploadActivity
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(UploadViewModel::class.java).apply {
            binding.viewModel = this

            eventSelectImage.observeEvent(this@UploadActivity, { currentSelectedImageCount ->
                getAlbumPhotos(currentSelectedImageCount)
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

    private fun getAlbumPhotos(currentSelectedImageCount: Int) {
        if(currentSelectedImageCount >= ALBUM_MAX_SELECT_COUNT) {
            return
        }

        val intent = CropImage.activity()
            .setOutputCompressQuality(100)
            .setGuidelines(CropImageView.Guidelines.ON)
            .getIntent(this)

        cropResultLauncher.launch(intent)
    }

    private fun refreshAdapterStatus() {
        uploadSmallPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.changeArrowButtonStatus(binding.vpUploadBig.currentItem)
    }
}