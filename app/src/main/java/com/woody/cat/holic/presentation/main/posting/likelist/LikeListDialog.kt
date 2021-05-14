package com.woody.cat.holic.presentation.main.posting.likelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogLikeListBinding
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import dagger.android.support.DaggerDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class LikeListDialog : DaggerDialogFragment() {

    companion object {
        fun newInstance(fragmentManager: FragmentManager, postingItem: PostingItem) {
            Builder()
                .setLikeUserList(postingItem.likedUserIds)
                .create()
                .show(fragmentManager, LikeListDialog::class.java.name)
        }
    }

    private lateinit var binding: DialogLikeListBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var likeListViewModel: LikeListViewModel

    private var likeUserList = mutableListOf<String>()

    private lateinit var likeListAdapter: LikeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (likeUserList.isEmpty()) {
            dismiss()
            return null
        }

        return DataBindingUtil.inflate<DialogLikeListBinding>(inflater, R.layout.dialog_like_list, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSlideAnimation

        likeListViewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(LikeListViewModel::class.java).apply {
            binding.likeListViewModel = this

            this.likeUserList = this@LikeListDialog.likeUserList

            likeListAdapter = LikeListAdapter(viewLifecycleOwner, this)

            viewLifecycleOwner.lifecycleScope.launch {
                getLikeListFlow().collectLatest { pagingData ->
                    likeListAdapter.submitData(pagingData)
                }
            }

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })
        }
        binding.rvLike.adapter = likeListAdapter
    }

    fun setLikeUserList(likeUserList: List<String>) {
        this.likeUserList.addAll(likeUserList)
    }

    private class Builder {
        private val likeListDialog = LikeListDialog()

        fun setLikeUserList(likeUserList: List<String>): Builder {
            likeListDialog.setLikeUserList(likeUserList)
            return this
        }

        fun create(): LikeListDialog {
            return likeListDialog
        }
    }
}