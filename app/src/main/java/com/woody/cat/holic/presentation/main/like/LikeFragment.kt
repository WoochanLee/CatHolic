package com.woody.cat.holic.presentation.main.like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentLikeBinding
import com.woody.cat.holic.framework.FirebaseUserManager

class LikeFragment : Fragment() {

    lateinit var binding: FragmentLikeBinding

    //TODO: move FirebaseUserManager to viewModel
    private val startGoogleSignInForResult =
        FirebaseUserManager.startGoogleSignInForResult(this, onSuccess = {

        }, onError = {

        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<FragmentLikeBinding>(
            inflater,
            R.layout.fragment_like,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            FirebaseUserManager.signIn(startGoogleSignInForResult, requireActivity())
        }

        //CatHolicGoogleUserManager.signOut(requireActivity())
    }
}