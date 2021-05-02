package com.woody.cat.holic.presentation.main.like

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
import com.woody.cat.holic.databinding.FragmentLikeBinding
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

class LikeFragment : BaseFragment() {

    private lateinit var binding: FragmentLikeBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var postingViewModel: PostingViewModel

    private lateinit var postingAdapter: PostingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentLikeBinding>(inflater, R.layout.fragment_like, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        mainViewModel = ViewModelProvider(activity, viewModelFactory).get(MainViewModel::class.java).apply {
            binding.mainViewModel = this

            eventChangeLikePostingOrder.observeEvent(viewLifecycleOwner, {
                likeViewModel.changeToNextPostingOrder()
                mainViewModel.refreshVisiblePostingOrder(MainTab.TAB_LIKE)
                postingAdapter.refresh()
            })
        }

        signViewModel = ViewModelProvider(activity, viewModelFactory).get(SignViewModel::class.java).apply {
            binding.userViewModel = this

            eventSignInSuccess.observeEvent(viewLifecycleOwner, {
                likeViewModel.initPagingFlow()
            })

            eventSignOutSuccess.observeEvent(viewLifecycleOwner, {
                likeViewModel.initPagingFlow()
            })
        }

        postingViewModel = ViewModelProvider(this, viewModelFactory).get(PostingViewModel::class.java).apply {
            postingAdapter = PostingAdapter(viewLifecycleOwner, this)

            eventShowPostingDetail.observeEvent(viewLifecycleOwner, { postingItem ->
                PostingDetailDialog.Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(parentFragmentManager, PostingDetailDialog::class.java.name)
            })

            eventShowCommentDialog.observeEvent(viewLifecycleOwner, { postingItem ->
                CommentDialog.Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(parentFragmentManager, CommentDialog::class.java.name)
            })

            eventShowLikeListDialog.observeEvent(viewLifecycleOwner, { postingItem ->
                LikeListDialog.Builder()
                    .setLikeUserList(postingItem.likedUserIds)
                    .create()
                    .show(parentFragmentManager, LikeListDialog::class.java.name)
            })

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })

            eventMoveToSignInTabWithToast.observeEvent(viewLifecycleOwner, {
                mainViewModel.callEventMoveToSignInTabWithToast()
            })
        }

        likeViewModel = ViewModelProvider(activity, viewModelFactory).get(LikeViewModel::class.java).apply {
            binding.likeViewModel = this

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

                        if (loadStates.refresh is LoadState.NotLoading) {
                            setIsListEmpty(postingAdapter.itemCount == 0)
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

    private fun LikeViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            getLikedPostingFlow().collectLatest { pagingData ->
                postingAdapter.submitData(pagingData)
            }
        }
    }
}