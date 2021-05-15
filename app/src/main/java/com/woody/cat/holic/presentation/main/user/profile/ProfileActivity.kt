package com.woody.cat.holic.presentation.main.user.profile

import android.app.Activity
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.ActivityProfileBinding
import com.woody.cat.holic.framework.base.*
import com.woody.cat.holic.presentation.main.user.profile.follower.FollowerListDialog
import com.woody.cat.holic.presentation.main.user.profile.following.FollowingListDialog
import com.woody.cat.holic.presentation.main.user.profile.photo.UserPhotoActivity
import com.woody.cat.holic.presentation.main.user.profile.photozoom.PhotoZoomDialog
import javax.inject.Inject
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

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProfileViewModel

    private var profilePhotoSelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            CropImage.getActivityResult(result.data)?.getUriFilePath(this)?.let { imageUri ->
                viewModel.uploadUserProfilePhoto(imageUri)
            }
        }
    }

    private var backgroundPhotoSelectLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            CropImage.getActivityResult(result.data)?.getUriFilePath(this)?.let { imageUri ->
                viewModel.uploadUserBackgroundPhoto(imageUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityProfileBinding>(this, R.layout.activity_profile).apply {
            lifecycleOwner = this@ProfileActivity
        }

        val userId = intent.getStringExtra(KEY_USER_ID) ?: run {
            finish()
            return
        }

        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java).apply {
            binding.viewModel = this
            getProfile(userId)

            eventSelectBackgroundPhoto.observeEvent(this@ProfileActivity, {
                startPhotoSelect(backgroundPhotoSelectLauncher)
            })

            eventSelectProfilePhoto.observeEvent(this@ProfileActivity, {
                startPhotoSelect(profilePhotoSelectLauncher)
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

            eventStartUserPhotoActivity.observeEvent(this@ProfileActivity, { userId ->
                startActivity(UserPhotoActivity.getIntent(this@ProfileActivity, userId))
            })

            eventShowFollowerDialog.observeEvent(this@ProfileActivity, { followerList ->
                FollowerListDialog.newInstance(supportFragmentManager, followerList)
            })

            eventShowFollowingDialog.observeEvent(this@ProfileActivity, { followingList ->
                FollowingListDialog.newInstance(supportFragmentManager, followingList)
            })

            eventShowToast.observeEvent(this@ProfileActivity, { stringRes ->
                Toast.makeText(applicationContext, stringRes, Toast.LENGTH_SHORT).show()
            })

            eventShowUnfollowAlertDialog.observeEvent(this@ProfileActivity, { (myUserId, targetUserId) ->
                showUnfollowAlertDialog(myUserId, targetUserId)
            })

            eventShowPhotoZoomDialog.observeEvent(this@ProfileActivity, { imageUrl ->
                PhotoZoomDialog.newInstance(supportFragmentManager, imageUrl)
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

    private fun startPhotoSelect(launcher: ActivityResultLauncher<Intent>) {
        val intent = CropImage.activity()
            .setOutputCompressQuality(100)
            .setGuidelines(CropImageView.Guidelines.ON)
            .getIntent(this)

        launcher.launch(intent)
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

    private fun showUnfollowAlertDialog(myUserId: String, targetUserId: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.unfollow_2))
            .setMessage(getString(R.string.do_you_really_want_to_unfollow))
            .setPositiveButton(getString(R.string.unfollow_2)) { _, _ ->
                viewModel.unfollowUser(myUserId, targetUserId)
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