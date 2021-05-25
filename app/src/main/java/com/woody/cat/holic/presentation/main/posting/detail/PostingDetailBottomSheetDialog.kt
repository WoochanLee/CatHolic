package com.woody.cat.holic.presentation.main.posting.detail

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogPostingDetailBottomSheetBinding
import com.woody.cat.holic.framework.base.ViewModelFactory
import com.woody.cat.holic.framework.base.observeEvent
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PostingDetailBottomSheetDialog @Inject constructor() : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(fragmentManager: FragmentManager) {
            if (fragmentManager.findFragmentByTag(PostingDetailBottomSheetDialog::class.java.name) == null) {
                PostingDetailBottomSheetDialog().show(fragmentManager, PostingDetailBottomSheetDialog::class.java.name)
            }
        }
    }

    private lateinit var binding: DialogPostingDetailBottomSheetBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var postingDetailViewModel: PostingDetailViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        AndroidSupportInjection.inject(this)
        setStyle(STYLE_NO_TITLE, R.style.RoundBottomSheetDialog)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<DialogPostingDetailBottomSheetBinding>(
            inflater,
            R.layout.dialog_posting_detail_bottom_sheet,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragment?.let { parentFragment ->
            postingDetailViewModel = ViewModelProvider(parentFragment, viewModelFactory).get(PostingDetailViewModel::class.java).apply {
                binding.postingDetailViewModel = this
                postingItem.value?.let { postingItem ->
                    binding.postingItem = postingItem
                } ?: dismiss()


                eventDismissBottomSheet.observeEvent(viewLifecycleOwner, {
                    dismiss()
                })
            }
        } ?: dismiss()
    }
}