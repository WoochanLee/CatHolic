package com.woody.cat.holic.presentation.main.posting.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogPostingDetailBinding
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import com.woody.cat.holic.framework.base.shareDynamicLink
import com.woody.cat.holic.framework.paging.item.PostingItem
import com.woody.cat.holic.presentation.main.MainViewModel
import com.woody.cat.holic.presentation.main.posting.PostingItemAdapter
import com.woody.cat.holic.presentation.main.posting.comment.CommentDialog
import com.woody.cat.holic.presentation.main.posting.likelist.LikeListDialog
import com.woody.cat.holic.presentation.main.user.profile.ProfileActivity
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class PostingDetailDialog @Inject constructor() : DaggerDialogFragment() {

    companion object {
        fun newInstance(context: Context?, fragmentManager: FragmentManager, postingItem: PostingItem) {

            if (postingItem.deleted) {
                Toast.makeText(context, R.string.the_post_has_been_deleted, Toast.LENGTH_LONG).show()
                return
            }

            if (fragmentManager.findFragmentByTag(PostingDetailDialog::class.java.name) == null) {
                Builder()
                    .setPostingItem(postingItem)
                    .create()
                    .show(fragmentManager, PostingDetailDialog::class.java.name)
            }
        }
    }

    private lateinit var binding: DialogPostingDetailBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mainViewModel: MainViewModel
    private lateinit var postingDetailViewModel: PostingDetailViewModel

    private var postingItem: PostingItem? = null

    private lateinit var postingItemAdapter: PostingItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen_dark)
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

        mainViewModel = ViewModelProvider(activity, viewModelFactory).get(MainViewModel::class.java).apply {
            binding.mainViewModel = this

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })
        }

        postingDetailViewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(PostingDetailViewModel::class.java).apply {
            binding.postingDetailViewModel = this
            setPostingItem(postingItem)

            addSourceToIsUserFollowed()

            eventShowCommentDialog.observeEvent(viewLifecycleOwner, {
                CommentDialog.newInstance(parentFragmentManager, postingItem)
            })

            eventSharePosting.observeEvent(viewLifecycleOwner, { dynamicLink ->
                context?.shareDynamicLink(R.string.I_really_want_to_show_you_this_cat, dynamicLink)
            })

            eventShowToast.observeEvent(viewLifecycleOwner, { stringRes ->
                context?.let { context ->
                    Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
                }
            })

            eventShowInAppReview.observeEvent(viewLifecycleOwner, {
                showInAppReview()
            })

            eventShowLikeListDialog.observeEvent(viewLifecycleOwner, { postingItem ->
                LikeListDialog.newInstance(parentFragmentManager, postingItem)
            })

            eventStartProfileActivity.observeEvent(viewLifecycleOwner, { userId ->
                startActivity(ProfileActivity.getIntent(requireContext(), userId))
            })
        }

        postingItemAdapter = PostingItemAdapter(
            lifecycleOwner = this,
            pageIndicatorView = binding.pivPostingDetail,
            scaleType = ImageView.ScaleType.FIT_CENTER,
            onClickPosting = { postingDetailViewModel.onClickPostingDetailImage() }
        ).also { postingItemViewPagerAdapter ->
            binding.vpPostingDetail.adapter = postingItemViewPagerAdapter
            binding.vpPostingDetail.registerOnPageChangeCallback(postingItemViewPagerAdapter.pageChangeListener)
            postingItemViewPagerAdapter.refreshItem(postingItem)
        }
    }

    fun setPostingItem(postingItem: PostingItem) {
        this.postingItem = postingItem
    }

    private fun showInAppReview() {
        activity?.let { activity ->
            val manager = ReviewManagerFactory.create(activity)

            manager.requestReviewFlow().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    manager.launchReviewFlow(activity, reviewInfo)
                } else {
                    FirebaseCrashlytics.getInstance().recordException(task.exception!!)
                }
            }
        }
    }

    private class Builder {
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