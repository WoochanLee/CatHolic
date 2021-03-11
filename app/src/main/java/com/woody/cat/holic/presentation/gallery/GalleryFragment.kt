package com.woody.cat.holic.presentation.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentGalleryBinding
import com.woody.cat.holic.framework.base.BaseViewModelFactory

class GalleryFragment : Fragment() {

    lateinit var viewModel: GalleryViewModel
    lateinit var binding: FragmentGalleryBinding

    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<FragmentGalleryBinding>(
            inflater,
            R.layout.fragment_gallery,
            container,
            false
        ).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMainGallery.adapter = photoAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, BaseViewModelFactory).get(GalleryViewModel::class.java).apply {
            photosLiveData.observe(this@GalleryFragment, { list ->
                photoAdapter.refreshData(list.map { it })
            })
            getPhotos()
        }
    }
}