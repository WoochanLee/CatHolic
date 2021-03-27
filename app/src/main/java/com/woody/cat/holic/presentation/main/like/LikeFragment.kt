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
import androidx.paging.map
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentLikeBinding
import com.woody.cat.holic.presentation.main.PostingAdapter
import com.woody.cat.holic.presentation.main.like.viewmodel.LikeViewModel
import com.woody.cat.holic.presentation.main.like.viewmodel.LikeViewModelFactory
import com.woody.cat.holic.presentation.main.mapToPostingItem
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModel
import com.woody.cat.holic.presentation.main.viewmodel.SignViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            mainViewModel.setCurrentVisiblePostingOrder(likeViewModel.getCurrentPostingOrder())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return

        mainViewModel = ViewModelProvider(activity, MainViewModelFactory()).get(MainViewModel::class.java).apply {
            binding.mainViewModel = this

            eventChangeLikePostingOrder.observe(viewLifecycleOwner, {
                likeViewModel.changeToNextPostingOrder()
                setCurrentVisiblePostingOrder(likeViewModel.getCurrentPostingOrder().getNextPostingOrder())
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

        likeViewModel = ViewModelProvider(activity, LikeViewModelFactory()).get(LikeViewModel::class.java).apply {
            binding.likeViewModel = this

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

        binding.rvMainGallery.adapter = postingAdapter
    }
}