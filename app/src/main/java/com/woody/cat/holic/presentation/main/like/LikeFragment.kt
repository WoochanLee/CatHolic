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
import com.woody.cat.holic.presentation.main.PostingAdapter
import com.woody.cat.holic.presentation.main.like.viewmodel.LikeViewModel
import com.woody.cat.holic.presentation.main.like.viewmodel.LikeViewModelFactory
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModel
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModelFactory

class LikeFragment : Fragment() {

    private lateinit var binding: FragmentLikeBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var likeViewModel: LikeViewModel

    private val postingAdapter: PostingAdapter by lazy {
        PostingAdapter(this, mainViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentLikeBinding>(inflater, R.layout.fragment_like, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mainViewModel.setCurrentVisiblePostingOrder(likeViewModel.currentPostingOrder)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        activity?.let {
            mainViewModel = ViewModelProvider(it, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                binding.mainViewModel = this

                eventChangeLikePostingOrder.observe(viewLifecycleOwner, {
                    likeViewModel.changeToNextPostingOrder()
                    setCurrentVisiblePostingOrder(likeViewModel.currentPostingOrder)
                })
            }

            signViewModel = ViewModelProvider(it, SignViewModelFactory()).get(SignViewModel::class.java).apply {
                binding.userViewModel = this

                eventSignInSuccess.observe(viewLifecycleOwner, {
                    likeViewModel.initData()
                })

                eventSignOutSuccess.observe(viewLifecycleOwner, {
                    likeViewModel.initData()
                })
            }

            likeViewModel = ViewModelProvider(it, LikeViewModelFactory()).get(LikeViewModel::class.java).apply {
                binding.likeViewModel = this

                postingList.observe(viewLifecycleOwner, { list ->
                    postingAdapter.submitList(list)
                })
            }
        }

        binding.rvMainGallery.adapter = postingAdapter
    }
}