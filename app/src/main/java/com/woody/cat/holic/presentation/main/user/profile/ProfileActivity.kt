package com.woody.cat.holic.presentation.main.user.profile

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityProfileBinding
import com.woody.cat.holic.framework.base.BaseActivity
import com.woody.cat.holic.framework.base.hideKeyboard
import com.woody.cat.holic.framework.base.makeCustomAlbumWidget
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.upload.UploadActivity
import com.yanzhenjie.album.Album
import kotlin.math.min


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
                getUserBackgroundPhotoFromAlbum { path ->
                    viewModel.uploadUserBackgroundPhoto(path)
                }
            })

            eventSelectProfilePhoto.observeEvent(this@ProfileActivity, {
                getUserBackgroundPhotoFromAlbum { path ->
                    viewModel.uploadUserProfilePhoto(path)
                }
            })

            eventShowEditDisplayNameDialog.observeEvent(this@ProfileActivity, {
                showEditTextAlertDialog(EditTextDialogType.DISPLAY_NAME, binding.tvProfileDisplayName.text.toString()) { displayName ->
                    viewModel.updateUserDisplayName(displayName)
                }
            })

            eventShowEditGreetingsDialog.observeEvent(this@ProfileActivity, {
                showEditTextAlertDialog(EditTextDialogType.GREETINGS, binding.tvProfileGreetings.text.toString()) { greetings ->
                    viewModel.updateUserGreetings(greetings)
                }
            })

            eventShowToast.observeEvent(this@ProfileActivity, {
                Toast.makeText(this@ProfileActivity, it, Toast.LENGTH_SHORT).show()
            })

            eventFinishActivity.observeEvent(this@ProfileActivity, {
                finish()
            })
        }
        initFullscreenOption()
    }

    private fun initFullscreenOption() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0x00000000 // transparent
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !viewModel.isDarkMode) this or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else this
        }
    }

    private fun getUserBackgroundPhotoFromAlbum(onPhotoSelected: (String) -> Unit) {
        Album.image(this)
            .singleChoice()
            .widget(makeCustomAlbumWidget())
            .camera(true)
            .columnCount(UploadActivity.ALBUM_COLUMN_COUNT)
            .onResult breaker@{ checkedList ->
                if (checkedList.size != 0) {
                    onPhotoSelected(checkedList[0].path)
                }

            }
            .start()
    }

    private fun showEditTextAlertDialog(type: EditTextDialogType, prevText: String, onConfirm: (String) -> Unit) {
        val container = FrameLayout(this@ProfileActivity)
        val editText = EditText(this).apply {

            val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.leftMargin = resources.getDimensionPixelSize(R.dimen.dp_20)
            params.rightMargin = resources.getDimensionPixelSize(R.dimen.dp_20)
            layoutParams = params

            maxLines = 8
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(type.textSizeDimen))
            setText(prevText)
            if (type == EditTextDialogType.DISPLAY_NAME) {
                setSingleLine()
            }

            addTextChangedListener {
                val trimText = it?.toString()
                    ?.run {
                        if (type == EditTextDialogType.DISPLAY_NAME) trim()
                        else this
                    }
                    ?.run {
                        substring(0, min(length, type.maxLength))
                    } ?: ""
                if (it.toString() != trimText) {
                    setText(trimText)
                    setSelection(trimText.length)
                }
            }
            setOnKeyListener { v, keyCode, _ ->
                if (keyCode == KeyEvent.ACTION_DOWN) {
                    hideKeyboard(this@ProfileActivity, v)
                }
                false
            }
        }
        container.addView(editText)

        AlertDialog.Builder(this)
            .setTitle(type.title)
            .setView(container)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { _, _ ->
                if (type == EditTextDialogType.DISPLAY_NAME && editText.text.toString().isEmpty()) {
                    Toast.makeText(this, R.string.enter_your_nickname, Toast.LENGTH_SHORT).show()
                } else {
                    onConfirm(editText.text.toString())
                }
            }.setNegativeButton(R.string.cancel, null)
            .show()
    }

    private enum class EditTextDialogType(
        @StringRes val title: Int,
        @DimenRes val textSizeDimen: Int,
        val maxLength: Int
    ) {
        DISPLAY_NAME(R.string.edit_nickname, R.dimen.dp_20, 15),
        GREETINGS(R.string.edit_greetings, R.dimen.dp_15, 1000),
    }
}