package com.woody.cat.holic.presentation.upload.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogUploadImageBinding

class UploadImageDialog : DialogFragment() {

    companion object {
        const val TAG = "UploadImageDialog"
        const val KEY_FILE_LIST = "key_file_list"
    }

    lateinit var viewModel: UploadImageViewModel
    lateinit var binding: DialogUploadImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_upload_image,
            container,
            false
        )

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            UploadImageViewModelFactory()
        ).get(UploadImageViewModel::class.java).apply {
            binding.viewModel = this
            eventUploadComplete.observe(viewLifecycleOwner, {
                dismiss()
            })
            arguments?.getStringArrayList(KEY_FILE_LIST)?.let {
                uploadPhotos(it)
            }
        }
    }
}