package com.woody.cat.holic.presentation.upload

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityUploadBinding
import com.woody.cat.holic.presentation.upload.dialog.UploadImageDialog
import com.yanzhenjie.album.Album


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload)

        viewModel = ViewModelProvider(this, UploadViewModelFactory()).get(UploadViewModel::class.java).apply {
            eventSelectImage.observe(this@UploadActivity, {
                getAlbumPhotos()
            })
        }

        initSmallRecyclerView()
    }

    private fun initSmallRecyclerView() {
        binding.rvUploadSmall.adapter = uploadSmallPreviewAdapter
    }

    private fun getAlbumPhotos() {
        Album.image(this) // Image selection.
            .multipleChoice()
            .camera(true)
            .columnCount(ALBUM_COLUMN_COUNT)
            .selectCount(ALBUM_MAX_SELECT_COUNT)
            .onResult breaker@{ checkedList ->
                if (checkedList.size == 0) {
                    return@breaker
                }

                uploadSmallPreviewAdapter.refreshData(checkedList.map { it.path })
                // startUploadImages(checkedList.map { it.path })
            }
            .start()
    }

    private fun startUploadImages(fileList: List<String>) {
        UploadImageDialog().apply {
            arguments = Bundle().apply {
                putStringArrayList(UploadImageDialog.KEY_FILE_LIST, ArrayList(fileList))
            }
        }.show(supportFragmentManager, UploadImageDialog.TAG)
    }
}