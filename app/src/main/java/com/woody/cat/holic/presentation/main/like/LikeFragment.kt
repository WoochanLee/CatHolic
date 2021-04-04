package com.woody.cat.holic.presentation.main.like

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentLikeBinding
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.presentation.main.MainPostingAdapter
import com.woody.cat.holic.presentation.main.MainTab
import com.woody.cat.holic.presentation.main.like.viewmodel.LikeViewModel
import com.woody.cat.holic.presentation.main.like.viewmodel.LikeViewModelFactory
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModel
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

class LikeFragment : Fragment() {

    private lateinit var binding: FragmentLikeBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var signViewModel: SignViewModel
    private lateinit var likeViewModel: LikeViewModel

    private lateinit var postingAdapter: MainPostingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentLikeBinding>(inflater, R.layout.fragment_like, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        mainViewModel = ViewModelProvider(activity, MainViewModelFactory()).get(MainViewModel::class.java).apply {
            binding.mainViewModel = this
            postingAdapter = MainPostingAdapter(viewLifecycleOwner, this)

            eventChangeLikePostingOrder.observe(viewLifecycleOwner, {
                likeViewModel.changeToNextPostingOrder()
                mainViewModel.refreshVisiblePostingOrder(MainTab.TAB_GALLERY)
                postingAdapter.refresh()
            })
        }

        signViewModel = ViewModelProvider(activity, SignViewModelFactory()).get(SignViewModel::class.java).apply {
            binding.userViewModel = this

            eventSignInSuccess.observe(viewLifecycleOwner, {
                likeViewModel.initPagingFlow()
            })

            eventSignOutSuccess.observe(viewLifecycleOwner, {
                likeViewModel.initPagingFlow()
            })
        }

        likeViewModel = ViewModelProvider(activity, LikeViewModelFactory()).get(LikeViewModel::class.java).apply {
            binding.likeViewModel = this

           initPagingFlow()

            viewLifecycleOwner.lifecycleScope.launch {
                postingAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val loadState = loadStates.refresh
                        setLoading(loadState is LoadState.Loading)

                        if (loadState is LoadState.Error) {
                            //TODO: handle network error
                            if(loadState.error is NotSignedInException) {
                                postingAdapter.submitData(PagingData.empty())
                            }
                        }
                    }
            }

            eventRefreshData.observe(viewLifecycleOwner, {
                initPagingFlow()
            })
        }

        binding.rvMainGallery.adapter = postingAdapter
    }

    var pagingJob: Job? = null

    private fun LikeViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            getLikedPostingFlow().collectLatest { pagingData ->
                postingAdapter.submitData(pagingData)
            }
        }
    }
}