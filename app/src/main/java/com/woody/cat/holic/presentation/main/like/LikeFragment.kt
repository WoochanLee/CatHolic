package com.woody.cat.holic.presentation.main.like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentLikeBinding
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory
import com.woody.cat.holic.presentation.main.PostingAdapter
import com.woody.cat.holic.presentation.main.viewmodel.UserViewModel
import com.woody.cat.holic.presentation.main.viewmodel.UserViewModelFactory

class LikeFragment : Fragment() {

    private lateinit var binding: FragmentLikeBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var userViewModel: UserViewModel

    private val postingAdapter: PostingAdapter by lazy {
        PostingAdapter(this, mainViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentLikeBinding>(inflater, R.layout.fragment_like, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            mainViewModel = ViewModelProvider(it, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                binding.mainViewModel = this

                postingsLiveData.observe(this@LikeFragment, { list ->
                    postingAdapter.refreshData(list)
                })

                currentPostingOrder.observe(this@LikeFragment, {
                    initPostings()
                })
            }

            userViewModel = ViewModelProvider(it, UserViewModelFactory()).get(UserViewModel::class.java).apply {
                binding.userViewModel = this

                eventSignInSuccess.observe(this@LikeFragment, {
                    mainViewModel.initPostings()
                })

                eventSignOutSuccess.observe(this@LikeFragment, {
                    mainViewModel.initPostings()
                })
            }
        }

        binding.rvMainGallery.adapter = postingAdapter
    }
}