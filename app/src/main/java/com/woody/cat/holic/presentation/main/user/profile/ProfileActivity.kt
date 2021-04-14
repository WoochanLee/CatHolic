package com.woody.cat.holic.presentation.main.user.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityProfileBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.makeCustomAlbumWidget
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.upload.UploadActivity
import com.yanzhenjie.album.Album

class ProfileActivity : BaseActivity() {

    companion object {

        private const val KEY_USER_ID = "KEY_USER_ID"

        fun getIntent(context: Context, userId: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY_USER_ID, userId)
            }
        }
    }

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityProfileBinding>(this, R.layout.activity_profile).apply {
            lifecycleOwner = this@ProfileActivity
        }

        val userId = intent.getStringExtra(KEY_USER_ID) ?: run {
            finish()
            return
        }

        viewModel = ViewModelProvider(this, ProfileViewModelFactory()).get(ProfileViewModel::class.java).apply {
            binding.viewModel = this
            getProfile(userId)

            eventSelectBackgroundPhoto.observeEvent(this@ProfileActivity, {
                getUserBackgroundPhotoFromAlbum()
            })
        }
        initToolbar()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.tbUserProfile)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun getUserBackgroundPhotoFromAlbum() {
        Album.image(this)
            .singleChoice()
            .widget(makeCustomAlbumWidget())
            .camera(true)
            .columnCount(UploadActivity.ALBUM_COLUMN_COUNT)
            .onResult breaker@{ checkedList ->
                if (checkedList.size == 0) {
                    return@breaker
                }

                viewModel.uploadUserBackgroundPhoto(checkedList[0].path)
            }
            .start()
    }
}