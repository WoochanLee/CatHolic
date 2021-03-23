package com.woody.cat.holic.presentation.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.map
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentGalleryBinding
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory
import com.woody.cat.holic.presentation.main.PostingAdapter
import com.woody.cat.holic.presentation.main.gallery.viewmodel.GalleryViewModel
import com.woody.cat.holic.presentation.main.gallery.viewmodel.GalleryViewModelFactory
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModel
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var galleryViewModel: GalleryViewModel

    private val postingAdapter: PostingAdapter by lazy {
        PostingAdapter(this, mainViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentGalleryBinding>(inflater, R.layout.fragment_gallery, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            mainViewModel.setCurrentVisiblePostingOrder(galleryViewModel.getCurrentPostingOrder())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { activity ->
            mainViewModel = ViewModelProvider(activity, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                binding.mainViewModel = this

                eventChangeGalleryPostingOrder.observe(viewLifecycleOwner, {
                    galleryViewModel.changeToNextPostingOrder()
                    setCurrentVisiblePostingOrder(galleryViewModel.getCurrentPostingOrder().getNextPostingOrder())
                    postingAdapter.refresh()
                })
            }

            signViewModel = ViewModelProvider(activity, SignViewModelFactory()).get(SignViewModel::class.java).apply {
                binding.userViewModel = this

                eventSignInSuccess.observe(viewLifecycleOwner, {
                    postingAdapter.refresh()
                })

                eventSignOutSuccess.observe(viewLifecycleOwner, {
                    postingAdapter.refresh()
                })
            }

            galleryViewModel = ViewModelProvider(activity, GalleryViewModelFactory()).get(GalleryViewModel::class.java).apply {
                binding.galleryViewModel = this

                viewLifecycleOwner.lifecycleScope.launch {
                    flow.collectLatest { pagingData ->
                        postingAdapter.submitData(pagingData.map { it.mapToPostingItem(signViewModel.userData.value?.userId) })
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    postingAdapter.loadStateFlow.collectLatest { loadStates ->
                        setLoading(loadStates.refresh is LoadState.Loading)

                        if(loadStates.refresh is LoadState.Error) {
                            //TODO: handle network error
                        }
                    }
                }

                eventRefreshData.observe(viewLifecycleOwner, {
                    postingAdapter.refresh()
                })
            }
        }

        binding.rvMainGallery.adapter = postingAdapter
    }
}