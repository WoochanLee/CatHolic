package com.woody.cat.holic.presentation.main.posting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogPostingDetailBinding
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.viewmodel.PostingDetailViewModel
import com.woody.cat.holic.presentation.main.posting.viewmodel.PostingDetailViewModelFactory
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModel
import com.woody.cat.holic.presentation.main.viewmodel.MainViewModelFactory

class PostingDetailDialog : DialogFragment() {

    private lateinit var binding: DialogPostingDetailBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var postingDetailViewModel: PostingDetailViewModel

    private var postingItem: PostingItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (postingItem == null) {
            dismiss()
            return null
        }

        return DataBindingUtil.inflate<DialogPostingDetailBinding>(inflater, R.layout.dialog_posting_detail, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSlideAnimation

        val activity = activity ?: return
        val postingItem = this.postingItem ?: return

        mainViewModel = ViewModelProvider(activity, MainViewModelFactory()).get(MainViewModel::class.java).apply {
            binding.mainViewModel = this
        }

        postingDetailViewModel = ViewModelProvider(viewModelStore, PostingDetailViewModelFactory(postingItem)).get(PostingDetailViewModel::class.java).apply {
            binding.postingDetailViewModel = this

            eventShowCommentDialog.observeEvent(viewLifecycleOwner, {
                CommentDialog.Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(parentFragmentManager, CommentDialog::class.java.name)
            })
        }
    }

    fun setPostingItem(postingItem: PostingItem) {
        this.postingItem = postingItem
    }

    class Builder {
        private val postingDetailDialog = PostingDetailDialog()

        fun setPostingItem(postingItem: PostingItem): Builder {
            postingDetailDialog.setPostingItem(postingItem)
            return this
        }

        fun create(): PostingDetailDialog {
            return postingDetailDialog
        }
    }
}