package com.woody.cat.holic.presentation.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentGalleryBinding
import com.woody.cat.holic.framework.base.BaseFragment
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.presentation.main.MainTab
import com.woody.cat.holic.presentation.main.MainViewModel
import com.woody.cat.holic.presentation.main.SignViewModel
import com.woody.cat.holic.presentation.main.posting.PostingAdapter
import com.woody.cat.holic.presentation.main.posting.PostingViewModel
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.detail.PostingDetailDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

class GalleryFragment : BaseFragment() {

    private lateinit var binding: FragmentGalleryBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var postingViewModel: PostingViewModel

    private lateinit var postingAdapter: PostingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentGalleryBinding>(inflater, R.layout.fragment_gallery, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java).apply {
            eventChangeGalleryPostingOrder.observeEvent(viewLifecycleOwner, {
                galleryViewModel.changeToNextPostingOrder()
                mainViewModel.refreshVisiblePostingOrder(MainTab.TAB_GALLERY)
                postingAdapter.refresh()
            })
        }

        signViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(SignViewModel::class.java).apply {
            eventSignInSuccess.observeEvent(viewLifecycleOwner, { //TODO: change to global refresh
                galleryViewModel.initPagingFlow()
            })

            eventSignOutSuccess.observeEvent(viewLifecycleOwner, {
                galleryViewModel.initPagingFlow()
            })
        }

        postingViewModel = ViewModelProvider(this, viewModelFactory).get(PostingViewModel::class.java).apply {
            postingAdapter = PostingAdapter(viewLifecycleOwner, this)

            eventShowPostingDetail.observeEvent(viewLifecycleOwner, { postingItem ->
                PostingDetailDialog.newInstance(parentFragmentManager, postingItem)
            })

            eventShowCommentDialog.observeEvent(viewLifecycleOwner, { postingItem ->
                CommentDialog.newInstance(parentFragmentManager, postingItem)
            })

            eventShowLikeListDialog.observeEvent(viewLifecycleOwner, { postingItem ->
                LikeListDialog.newInstance(parentFragmentManager, postingItem)
            })

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })

            eventMoveToSignInTabWithToast.observeEvent(viewLifecycleOwner, {
                mainViewModel.callEventMoveToSignInTabWithToast()
            })
        }


        galleryViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GalleryViewModel::class.java).apply {
            binding.galleryViewModel = this

            initPagingFlow()

            viewLifecycleOwner.lifecycleScope.launch {
                postingAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val refreshState = loadStates.refresh
                        setLoading(refreshState is LoadState.Loading)

                        if (refreshState is LoadState.Error) {
                            //TODO: handle network error
                            if (refreshState.error is NotSignedInException) {
                                postingAdapter.submitData(PagingData.empty())
                            }
                        }

                        if (refreshState is LoadState.NotLoading) {
                            binding.rvMainGallery.scrollToPosition(0)
                        }
                    }
            }

            eventRefreshData.observeEvent(viewLifecycleOwner, {
                initPagingFlow()
            })
        }

        binding.rvMainGallery.adapter = postingAdapter
    }

    private var pagingJob: Job? = null

    private fun GalleryViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            getGalleryPostingFlow().collectLatest { pagingData ->
                postingAdapter.submitData(pagingData)
            }
        }
    }
}