package com.woody.cat.holic.presentation.main.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentUserBinding
import com.woody.cat.holic.framework.base.BaseFragment
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.base.shareDynamicLink
import com.woody.cat.holic.presentation.main.SignViewModel
import com.woody.cat.holic.presentation.main.user.myphoto.MyPhotoActivity
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import com.woody.cat.holic.presentation.main.user.profile.follower.FollowerListDialog
import com.woody.cat.holic.presentation.main.user.profile.following.FollowingListDialog
import com.woody.cat.holic.presentation.main.user.profile.photo.UserPhotoActivity
import javax.inject.Inject

class UserFragment : BaseFragment() {

    private lateinit var binding: FragmentUserBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var signViewModel: SignViewModel
    private lateinit var userViewModel: UserViewModel

    private lateinit var startGoogleSignInForResult: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = DataBindingUtil.inflate<FragmentUserBinding>(inflater, R.layout.fragment_user, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root

        startGoogleSignInForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            signViewModel.handleGoogleSignInResult(task)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        signViewModel = ViewModelProvider(activity, viewModelFactory).get(SignViewModel::class.java).apply {
            binding.signViewModel = this

            initFirebaseAuth(getString(R.string.default_web_client_id))

            eventSignIn.observeEvent(viewLifecycleOwner, {
                signIn(startGoogleSignInForResult, requireActivity())
            })

            eventSignOut.observeEvent(viewLifecycleOwner, {
                signOut()
            })

            eventSignInFail.observeEvent(viewLifecycleOwner, {
                Toast.makeText(activity, "fail to sign in", Toast.LENGTH_SHORT).show()
            })
        }

        userViewModel = ViewModelProvider(activity, viewModelFactory).get(UserViewModel::class.java).apply {
            binding.userViewModel = this

            eventChangeDarkMode.observeEvent(viewLifecycleOwner, { isDarkMode ->
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            })

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(activity, userId))
            })

            eventStartUserPhotoActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(UserPhotoActivity.getIntent(requireContext(), userId))
            })

            eventShowFollowerDialog.observeEvent(viewLifecycleOwner, { followerList ->
                FollowerListDialog.newInstance(parentFragmentManager, followerList)
            })

            eventShowFollowingDialog.observeEvent(viewLifecycleOwner, { followingList ->
                FollowingListDialog.newInstance(parentFragmentManager, followingList)
            })

            eventStartMyCatPhotos.observeEvent(viewLifecycleOwner, {
                startActivity(Intent(requireContext(), MyPhotoActivity::class.java))
            })

            eventStartNotificationSetting.observeEvent(viewLifecycleOwner, {
                startNotificationSetting()
            })

            eventSharePosting.observeEvent(viewLifecycleOwner, { dynamicLink ->
                context?.shareDynamicLink(R.string.cat_photo_sharing_sns_s, dynamicLink)
            })
        }

        signViewModel.refreshSignInStatus()
    }

    private fun signIn(activityResultLauncher: ActivityResultLauncher<Intent>, activity: Activity) {
        signViewModel.let { signViewModel ->
            val googleSignInClient = GoogleSignIn.getClient(activity, signViewModel.gso)
            activityResultLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun signOut() {
        signViewModel.let { signViewModel ->
            GoogleSignIn.getClient(requireActivity(), signViewModel.gso).signOut()
            signViewModel.signOutFirebase()
        }
    }

    private fun startNotificationSetting() {
        requireContext().run {
            Intent().apply {
                action = "android.settings.APP_NOTIFICATION_SETTINGS"
                putExtra("app_package", packageName)
                putExtra("app_uid", applicationInfo.uid)
                putExtra("android.provider.extra.APP_PACKAGE", packageName)
                startActivity(this)
            }
        }
    }
}