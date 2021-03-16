package com.woody.cat.holic.presentation.upload

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityUploadBinding
import com.woody.cat.holic.presentation.upload.dialog.UploadImageDialog
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget


class UploadActivity : AppCompatActivity() {

    companion object {
        const val ALBUM_COLUMN_COUNT = 3
        const val ALBUM_MAX_SELECT_COUNT = 10
    }

    lateinit var viewModel: UploadViewModel
    lateinit var binding: ActivityUploadBinding
    private val uploadSmallPreviewAdapter: UploadSmallPreviewAdapter by lazy {
        UploadSmallPreviewAdapter(this, viewModel)
    }
    private val uploadBigPreviewAdapter: UploadBigPreviewAdapter by lazy {
        UploadBigPreviewAdapter(this, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView<ActivityUploadBinding>(this, R.layout.activity_upload)
                .apply {
                    lifecycleOwner = this@UploadActivity
                }

        viewModel =
            ViewModelProvider(this, UploadViewModelFactory()).get(UploadViewModel::class.java)
                .apply {
                    binding.viewModel = this

                    eventSelectImage.observe(this@UploadActivity, {
                        getAlbumPhotos()
                    })

                    eventMoveToNextPreviewPage.observe(this@UploadActivity, {
                        moveToNextPreviewPage()
                    })

                    eventMoveToPrevPreviewPage.observe(this@UploadActivity, {
                        moveToPrevPreviewPage()
                    })

                    eventMoveToTargetPreviewPage.observe(this@UploadActivity, {
                        moveToTargetPreviewPage(it)
                    })

                    eventRemoveTargetPreviewPage.observe(this@UploadActivity, {
                        removeTargetPreviewPage(it)
                    })

                    eventCancel.observe(this@UploadActivity, {
                        onBackPressed()
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
                    uploadBigPreviewAdapter.checkAndChangeArrowButtonStatus(position)
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
            .widget(makeCustomAlbumWidget())
            .camera(true)
            .columnCount(ALBUM_COLUMN_COUNT)
            .selectCount(ALBUM_MAX_SELECT_COUNT)
            .onResult breaker@{ checkedList ->
                if (checkedList.size == 0) {
                    return@breaker
                }

                viewModel.refreshPreviewData(checkedList.map { it.path })
                refreshAdapterStatus()
                // startUploadImages(checkedList.map { it.path })
            }
            .start()
    }

    private fun makeCustomAlbumWidget(): Widget {
        return Widget.newDarkBuilder(this)
            .title("Select Cats")
            .statusBarColor(Color.BLACK)
            .toolBarColor(Color.BLACK)
            .navigationBarColor(Color.BLACK)
            .build()
    }

    private fun refreshAdapterStatus() {
        uploadSmallPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.notifyDataSetChanged()
        uploadBigPreviewAdapter.checkAndChangeArrowButtonStatus(binding.vpUploadBig.currentItem)
    }

    private fun startUploadImages(fileList: List<String>) {
        UploadImageDialog().apply {
            arguments = Bundle().apply {
                putStringArrayList(UploadImageDialog.KEY_FILE_LIST, ArrayList(fileList))
            }
        }.show(supportFragmentManager, UploadImageDialog.TAG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}