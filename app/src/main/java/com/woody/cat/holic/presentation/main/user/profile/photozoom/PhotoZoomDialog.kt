package com.woody.cat.holic.presentation.main.user.profile.photozoom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.DialogPhotoZoomBinding
import com.woody.cat.holic.framework.base.ViewModelFactory
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

class PhotoZoomDialog : DaggerDialogFragment() {

    companion object {

        private const val KEY_IMAGE_URL = "KEY_IMAGE_URL"

        fun newInstance(fragmentManager: FragmentManager, imageUrl: String) {
            if (fragmentManager.findFragmentByTag(PhotoZoomDialog::class.java.name) == null) {
                PhotoZoomDialog().apply {
                    arguments = Bundle().apply {
                        putString(KEY_IMAGE_URL, imageUrl)
                    }
                    show(fragmentManager, PhotoZoomDialog::class.java.name)
                }
            }
        }
    }

    private lateinit var binding: DialogPhotoZoomBinding

    private lateinit var photoZoomViewModel: PhotoZoomViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen_dark)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return DataBindingUtil.inflate<DialogPhotoZoomBinding>(inflater, R.layout.dialog_photo_zoom, container, false).apply {
            binding = this
            lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.BottomSlideAnimation

        photoZoomViewModel = ViewModelProvider(this, viewModelFactory).get(PhotoZoomViewModel::class.java).apply {
            binding.viewModel = this
            arguments?.getString(KEY_IMAGE_URL)?.let { imageUrl ->
                initImageUrl(imageUrl)
            }
        }
    }
}