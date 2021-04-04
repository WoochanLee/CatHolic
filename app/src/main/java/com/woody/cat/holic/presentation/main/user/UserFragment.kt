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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentUserBinding
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModel
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModelFactory

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var signViewModel: SignViewModel

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            signViewModel.refreshSignInStatus()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        signViewModel = ViewModelProvider(activity, SignViewModelFactory()).get(SignViewModel::class.java).apply {
            binding.userViewModel = this

            initFirebaseAuth(getString(R.string.default_web_client_id))

            eventSignIn.observe(viewLifecycleOwner, {
                signIn(startGoogleSignInForResult, requireActivity())
            })

            eventSignOut.observe(viewLifecycleOwner, {
                signOut()
                refreshSignInStatus()
                onSignOutSuccess()
            })

            eventSignInFail.observe(viewLifecycleOwner, {
                Toast.makeText(activity, "fail to sign in", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun signIn(activityResultLauncher: ActivityResultLauncher<Intent>, activity: Activity) {
        val googleSignInClient = GoogleSignIn.getClient(activity, signViewModel.gso)
        activityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun signOut() {
        GoogleSignIn.getClient(requireActivity(), signViewModel.gso).signOut()
        signViewModel.signOutFirbase()
    }
}