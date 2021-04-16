package com.woody.cat.holic.presentation.main.user.profile.following

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogFollowingListBinding
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowingListDialog : DialogFragment() {

    private lateinit var binding: DialogFollowingListBinding
    private lateinit var followingListViewModel: FollowingListViewModel

    private var followingUserList = mutableListOf<String>()

    private lateinit var followingListAdapter: FollowingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (followingUserList.isEmpty()) {
            dismiss()
            return null
        }

        return DataBindingUtil.inflate<DialogFollowingListBinding>(inflater, R.layout.dialog_following_list, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSlideAnimation

        followingListViewModel = ViewModelProvider(viewModelStore, FollowingListViewModelFactory(followingUserList)).get(FollowingListViewModel::class.java).apply {
            binding.followingListViewModel = this
            followingListAdapter = FollowingListAdapter(viewLifecycleOwner, this)

            viewLifecycleOwner.lifecycleScope.launch {
                getCommentFlow().collectLatest { pagingData ->
                    followingListAdapter.submitData(pagingData)
                }
            }

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })
        }
        binding.rvFollowing.adapter = followingListAdapter
    }

    fun setLikeUserList(likeUserList: List<String>) {
        this.followingUserList.addAll(likeUserList)
    }

    class Builder {
        private val followingListDialog = FollowingListDialog()

        fun setFollowingUserList(followingUserList: List<String>): Builder {
            followingListDialog.setLikeUserList(followingUserList)
            return this
        }

        fun create(): FollowingListDialog {
            return followingListDialog
        }
    }
}