package com.woody.cat.holic.presentation.main.posting.comment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.vanniktech.emoji.EmojiPopup
import com.vdurmont.emoji.EmojiParser
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogPostingCommentBinding
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.hideKeyboard
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.net.common.NotSignedInException
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import dagger.android.support.DaggerDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject


class CommentDialog : DaggerDialogFragment() {

    companion object {
        fun newInstance(fragmentManager: FragmentManager, postingItem: PostingItem) {
            if (fragmentManager.findFragmentByTag(CommentDialog::class.java.name) == null) {
                Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(fragmentManager, CommentDialog::class.java.name)
            }
        }
    }

    private lateinit var binding: DialogPostingCommentBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

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

        commentViewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(CommentViewModel::class.java).apply {
            this.postingItem = postingItem
            binding.commentViewModel = this
            commentAdapter = CommentAdapter(viewLifecycleOwner, this)

            initPagingFlow()

            viewLifecycleOwner.lifecycleScope.launch {
                commentAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.refresh }
                    .collectLatest { loadStates ->
                        val refreshState = loadStates.refresh
                        setLoading(refreshState is LoadState.Loading)

                        if (refreshState is LoadState.Error) {
                            //TODO: handle network error
                            if (refreshState.error is NotSignedInException) {
                                commentAdapter.submitData(PagingData.empty())
                            }
                        }

                        if (refreshState is LoadState.NotLoading) {
                            binding.rvComment.scrollToPosition(0)
                            setIsListEmpty(commentAdapter.itemCount == 0)
                        }
                    }
            }

            eventRefreshData.observeEvent(viewLifecycleOwner, {
                initPagingFlow()
            })

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })

            eventHideKeyboard.observeEvent(viewLifecycleOwner, {
                hideKeyboard(requireContext(), binding.eetPostingComment)
            })

            eventCopyEmojiComment.observeEvent(viewLifecycleOwner, { emojiComment ->
                context?.let { context ->
                    val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText(getString(R.string.emoji_comment), emojiComment))
                    Toast.makeText(context, R.string.emoji_comment_copied, Toast.LENGTH_SHORT).show()
                }
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

    private class Builder {
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