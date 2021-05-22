package com.woody.cat.holic.presentation.main.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentFollowBinding
import com.woody.cat.holic.framework.base.BaseFragment
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.presentation.main.user.profile.photo.UserPhotoActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

class FollowFragment @Inject constructor() : BaseFragment() {

    private lateinit var binding: FragmentFollowBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var followViewModel: FollowViewModel

    private lateinit var followAdapter: FollowAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentFollowBinding>(inflater, R.layout.fragment_follow, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        followViewModel = ViewModelProvider(this, viewModelFactory).get(FollowViewModel::class.java).apply {
            binding.viewModel = this

            followAdapter = FollowAdapter(viewLifecycleOwner, this)

            eventUserPhotoActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(UserPhotoActivity.getIntent(requireContext(), userId))
            })

            eventRefreshData.observeEvent(viewLifecycleOwner, {
                initPagingFlow()
            })

            eventShowToast.observeEvent(viewLifecycleOwner, { stringRes ->
                Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
            })

            viewLifecycleOwner.lifecycleScope.launch {
                followAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val refreshState = loadStates.refresh
                        setLoading(refreshState is LoadState.Loading)

                        if (refreshState is LoadState.Error) {
                            if (refreshState.error is NotSignedInException) {
                                followAdapter.submitData(PagingData.empty())
                            } else {
                                Toast.makeText(context, R.string.network_fail, Toast.LENGTH_LONG).show()
                            }
                        }

                        if (refreshState is LoadState.NotLoading) {
                            binding.rvFollow.scrollToPosition(0)
                        }
                    }
            }

            initData()
        }
        binding.rvFollow.adapter = followAdapter
    }

    private var pagingJob: Job? = null

    private fun FollowViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            getFollowingListFlow().collectLatest { pagingData ->
                followAdapter.submitData(pagingData)
            }
        }
    }
}