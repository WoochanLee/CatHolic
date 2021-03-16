package com.woody.cat.holic.presentation.main.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.woody.cat.holic.R
import com.woody.cat.holic.databinding.FragmentGalleryBinding
import com.woody.cat.holic.presentation.main.MainViewModel

class GalleryFragment : Fragment() {

    lateinit var viewModel: GalleryViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentGalleryBinding

    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(this, viewModel)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel =
            ViewModelProvider(this, GalleryViewModelFactory()).get(GalleryViewModel::class.java)
                .apply {
                    binding.viewModel = this
                    photosLiveData.observe(this@GalleryFragment, { list ->
                        photoAdapter.refreshData(list.map { it })
                    })
                    initPhotos()
                }

        activity?.let {
            activityViewModel =
                ViewModelProvider(it, GalleryViewModelFactory()).get(MainViewModel::class.java)
            binding.activityViewModel = activityViewModel
        }

        binding.rvMainGallery.adapter = photoAdapter
    }
}