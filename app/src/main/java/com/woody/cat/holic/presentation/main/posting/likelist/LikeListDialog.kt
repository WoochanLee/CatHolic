package com.woody.cat.holic.presentation.main.posting.likelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogLikeListBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LikeListDialog : DialogFragment() {

    private lateinit var binding: DialogLikeListBinding
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

        likeListViewModel = ViewModelProvider(viewModelStore, LikeListViewModelFactory(likeUserList)).get(LikeListViewModel::class.java).apply {
            binding.likeListViewModel = this
            likeListAdapter = LikeListAdapter(viewLifecycleOwner, this)

            viewLifecycleOwner.lifecycleScope.launch {
                getCommentFlow().collectLatest { pagingData ->
                    likeListAdapter.submitData(pagingData)
                }
            }
        }
        binding.rvLike.adapter = likeListAdapter
    }

    fun setLikeUserList(likeUserList: List<String>) {
        this.likeUserList.addAll(likeUserList)
    }

    class Builder {
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