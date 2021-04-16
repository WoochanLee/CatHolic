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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentUserBinding
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.SignViewModel
import com.woody.cat.holic.presentation.main.SignViewModelFactory
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import com.woody.cat.holic.presentation.main.user.profile.follower.FollowerListDialog
import com.woody.cat.holic.presentation.main.user.profile.following.FollowingListDialog
import com.woody.cat.holic.presentation.main.user.profile.photo.UserPhotoActivity

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
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

        signViewModel = ViewModelProvider(activity, SignViewModelFactory()).get(SignViewModel::class.java).apply {
            binding.signViewModel = this

            initFirebaseAuth(getString(R.string.default_web_client_id))

            eventSignIn.observeEvent(viewLifecycleOwner, {
                signIn(startGoogleSignInForResult, requireActivity())
            })

            eventSignOut.observeEvent(viewLifecycleOwner, {
                signOut()
                refreshSignInStatus()
                onSignOutSuccess()
            })

            eventSignInFail.observeEvent(viewLifecycleOwner, {
                Toast.makeText(activity, "fail to sign in", Toast.LENGTH_SHORT).show()
            })
        }

        userViewModel = ViewModelProvider(activity, UserViewModelFactory()).get(UserViewModel::class.java).apply {
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
                FollowerListDialog.Builder()
                    .setFollowerUserList(followerList)
                    .create()
                    .show(parentFragmentManager, FollowerListDialog::class.java.name)
            })

            eventShowFollowingDialog.observeEvent(viewLifecycleOwner, { followingList ->
                FollowingListDialog.Builder()
                    .setFollowingUserList(followingList)
                    .create()
                    .show(parentFragmentManager, FollowingListDialog::class.java.name)
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
}