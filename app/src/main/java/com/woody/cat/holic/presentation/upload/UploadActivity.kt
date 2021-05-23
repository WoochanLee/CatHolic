package com.woody.cat.holic.presentation.upload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBun.Companion.INTENT_PATH
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
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
                viewModel.addPreviewData(imageUri)
                refreshAdapterStatus()
                viewModel.previewData.value?.size?.takeIf { it > 0 }?.let { lastPosition ->
                    scrollToTargetSmallPreviewItem(lastPosition)
                    moveToTargetPreviewPage(lastPosition)
                }

                checkAndStartCropImage()
            } ?: viewModel.waitingForCropImageUriList.clear()
        } else {
            viewModel.waitingForCropImageUriList.clear()
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

    private fun scrollToTargetSmallPreviewItem(position: Int) {
        binding.rvUploadSmall.smoothScrollToPosition(position)
    }

    private fun removeTargetPreviewPage(targetPage: Int) {
        viewModel.removePreviewData(targetPage)
        refreshAdapterStatus()
    }

    private fun getAlbumPhotos(currentSelectedImageCount: Int) {
        if (currentSelectedImageCount >= ALBUM_MAX_SELECT_COUNT) {
            return
        }

        FishBun.with(this@UploadActivity)
            .setImageAdapter(GlideAdapter())
            .setMaxCount(ALBUM_MAX_SELECT_COUNT - currentSelectedImageCount)
            .setMinCount(1)
            .setActionBarColor(
                ContextCompat.getColor(this@UploadActivity, R.color.black),
                ContextCompat.getColor(this@UploadActivity, R.color.black),
                false
            )
            .setActionBarTitleColor(ContextCompat.getColor(this@UploadActivity, R.color.white))
            .setPickerSpanCount(3)
            .setAlbumSpanCount(1, 1)
            .setButtonInAlbumActivity(false)
            .setAllViewTitle(getString(R.string.all))
            .textOnImagesSelectionLimitReached(getString(R.string.selection_full))
            .setActionBarTitle(getString(R.string.select_photo))
            .textOnNothingSelected(getString(R.string.please_select_photos))
            .exceptMimeType(listOf(MimeType.GIF))
            .startAlbum()
    }

    private fun checkAndStartCropImage() {
        if (viewModel.waitingForCropImageUriList.size == 0) {
            return
        }

        val intent = CropImage.activity(viewModel.waitingForCropImageUriList[0])
            .setOutputCompressQuality(100)
            .setGuidelines(CropImageView.Guidelines.ON)
            .getIntent(this)

        viewModel.waitingForCropImageUriList.removeAt(0)

        cropResultLauncher.launch(intent)
    }

    private fun refreshAdapterStatus() {
        uploadSmallPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.changeArrowButtonStatus(binding.vpUploadBig.currentItem)
    }

    override fun onBackPressed() {
        if (viewModel.isVisibleGuide.value == true) {
            viewModel.setUploadGuideStatus(false)
            viewModel.setVisibleGuide(false)
            return
        }

        showStopWarningDialog()
    }

    private fun showStopWarningDialog() {
        AlertDialog.Builder(this@UploadActivity)
            .setTitle(getString(R.string.stop_uploading))
            .setMessage(getString(R.string.do_you_really_want_to_stop_uploading))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                finish()
            }.setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FishBun.FISHBUN_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                val path = data?.getParcelableArrayListExtra<Uri>(INTENT_PATH) ?: emptyList()

                viewModel.waitingForCropImageUriList.addAll(path.toList())
                checkAndStartCropImage()
            }
        }
    }
}