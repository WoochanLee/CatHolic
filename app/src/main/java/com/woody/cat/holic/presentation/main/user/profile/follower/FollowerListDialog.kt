package com.woody.cat.holic.presentation.main.user.profile.follower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogFollowerListBinding
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowerListDialog : DialogFragment() {

    private lateinit var binding: DialogFollowerListBinding
    private lateinit var followerListViewModel: FollowerListViewModel

    private var followerUserList = mutableListOf<String>()

    private lateinit var followerListAdapter: FollowerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (followerUserList.isEmpty()) {
            dismiss()
            return null
        }

        return DataBindingUtil.inflate<DialogFollowerListBinding>(inflater, R.layout.dialog_follower_list, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSlideAnimation

        followerListViewModel = ViewModelProvider(viewModelStore, FollowerListViewModelFactory(followerUserList)).get(FollowerListViewModel::class.java).apply {
            binding.followerListViewModel = this
            followerListAdapter = FollowerListAdapter(viewLifecycleOwner, this)

            viewLifecycleOwner.lifecycleScope.launch {
                getCommentFlow().collectLatest { pagingData ->
                    followerListAdapter.submitData(pagingData)
                }
            }

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })
        }
        binding.rvFollower.adapter = followerListAdapter
    }

    fun setLikeUserList(likeUserList: List<String>) {
        this.followerUserList.addAll(likeUserList)
    }

    class Builder {
        private val followerListDialog = FollowerListDialog()

        fun setFollowerUserList(followerUserList: List<String>): Builder {
            followerListDialog.setLikeUserList(followerUserList)
            return this
        }

        fun create(): FollowerListDialog {
            return followerListDialog
        }
    }
}