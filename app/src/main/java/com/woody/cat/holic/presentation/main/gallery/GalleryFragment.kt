package com.woody.cat.holic.presentation.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentGalleryBinding
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory
import com.woody.cat.holic.presentation.main.PostingAdapter
import com.woody.cat.holic.presentation.main.viewmodel.UserViewModel
import com.woody.cat.holic.presentation.main.viewmodel.UserViewModelFactory

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var userViewModel: UserViewModel

    private val postingAdapter: PostingAdapter by lazy {
        PostingAdapter(this, mainViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentGalleryBinding>(inflater, R.layout.fragment_gallery, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            mainViewModel = ViewModelProvider(it, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                binding.mainViewModel = this

                postingsLiveData.observe(this@GalleryFragment, { list ->
                    postingAdapter.refreshData(list)
                })

                currentPostingOrder.observe(this@GalleryFragment, {
                    initPostings()
                })
            }

            userViewModel = ViewModelProvider(it, UserViewModelFactory()).get(UserViewModel::class.java).apply {
                binding.userViewModel = this

                eventGoogleSignIn.observe(this@GalleryFragment, {
                    mainViewModel.initPostings()
                })

                eventSignOutSuccess.observe(this@GalleryFragment, {
                    mainViewModel.initPostings()
                })
            }
        }

        binding.rvMainGallery.adapter = postingAdapter
    }
}