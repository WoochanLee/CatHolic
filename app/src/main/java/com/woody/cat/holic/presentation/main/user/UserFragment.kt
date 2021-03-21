package com.woody.cat.holic.presentation.main.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentUserBinding
import com.woody.cat.holic.framework.FirebaseUserManager
import com.woody.cat.holic.presentation.main.viewmodel.UserViewModel
import com.woody.cat.holic.presentation.main.viewmodel.UserViewModelFactory

class UserFragment : Fragment() {

    private lateinit var binding: FragmentUserBinding
    private lateinit var userViewModel: UserViewModel

    private lateinit var startGoogleSignInForResult: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = DataBindingUtil.inflate<FragmentUserBinding>(inflater, R.layout.fragment_user, container, false).apply {
            binding = this
            lifecycleOwner = this@UserFragment
        }.root

        startGoogleSignInForResult = FirebaseUserManager.startGoogleSignInForResult(this, onSuccess = {
            userViewModel.apply {
                refreshSignInStatus()
                onSignInSuccess()
            }
        }, onError = {
            it.printStackTrace()
            //TODO: handle error
        })

        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            userViewModel.refreshSignInStatus()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let{ activity ->
            userViewModel = ViewModelProvider(activity, UserViewModelFactory()).get(UserViewModel::class.java).apply {
                binding.userViewModel = this

                eventGoogleSignIn.observe(this@UserFragment, {
                    FirebaseUserManager.signIn(startGoogleSignInForResult, requireActivity())
                })

                eventSignOutSuccess.observe(this@UserFragment, {
                    FirebaseUserManager.signOut(requireActivity())
                    refreshSignInStatus()
                })
            }
        }
    }
}