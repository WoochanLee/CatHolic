package com.woody.cat.holic.presentation.main.posting.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.vanniktech.emoji.EmojiPopup
import com.vdurmont.emoji.EmojiParser
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogPostingCommentBinding
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.framework.paging.item.PostingItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

class CommentDialog : DialogFragment() {

    private lateinit var binding: DialogPostingCommentBinding
    private lateinit var commentViewModel: CommentViewModel

    private var postingItem: PostingItem? = null

    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (postingItem == null) {
            dismiss()
            return null
        }

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        return DataBindingUtil.inflate<DialogPostingCommentBinding>(inflater, R.layout.dialog_posting_comment, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSlideAnimation

        val postingItem = this.postingItem ?: return

        commentViewModel = ViewModelProvider(viewModelStore, CommentViewModelFactory(postingItem)).get(CommentViewModel::class.java).apply {
            binding.commentViewModel = this
            commentAdapter = CommentAdapter(viewLifecycleOwner, this)

            initPagingFlow()

            viewLifecycleOwner.lifecycleScope.launch {
                commentAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val loadState = loadStates.refresh
                        setLoading(loadState is LoadState.Loading)

                        if (loadState is LoadState.Error) {
                            //TODO: handle network error
                            if (loadState.error is NotSignedInException) {
                                commentAdapter.submitData(PagingData.empty())
                            }
                        }

                        if (loadState is LoadState.NotLoading) {
                            binding.rvComment.scrollToPosition(0)
                        }
                    }
            }

            eventRefreshData.observeEvent(viewLifecycleOwner, {
                initPagingFlow()
            })
        }

        initEmojiInput()
        binding.rvComment.adapter = commentAdapter
    }

    private fun initEmojiInput() {
        binding.eetPostingComment.apply {
            disableKeyboardInput(EmojiPopup.Builder.fromRootView(binding.root).build(binding.eetPostingComment))
            doOnTextChanged { text, _, _, _ ->
                val onlyEmojiStr = EmojiParser.extractEmojis(text.toString()).let { list ->
                    StringBuilder().apply { list.forEach { append(it) } }.toString()
                }
                commentViewModel.setWritingEmojiStr(onlyEmojiStr)
            }
        }
    }

    fun setPostingItem(postingItem: PostingItem) {
        this.postingItem = postingItem
    }

    private var pagingJob: Job? = null

    private fun CommentViewModel.initPagingFlow() {
        pagingJob?.cancel()
        pagingJob = viewLifecycleOwner.lifecycleScope.launch {
            getCommentFlow().collectLatest { pagingData ->
                commentAdapter.submitData(pagingData)
            }
        }
    }

    class Builder {
        private val postingCommentDialog = CommentDialog()

        fun setPostingItem(postingItem: PostingItem): Builder {
            postingCommentDialog.setPostingItem(postingItem)
            return this
        }

        fun create(): CommentDialog {
            return postingCommentDialog
        }
    }
}