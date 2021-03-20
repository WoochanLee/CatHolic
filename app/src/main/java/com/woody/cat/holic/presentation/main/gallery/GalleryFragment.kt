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
import com.woody.cat.holic.framework.base.CatHolicLogger
import com.woody.cat.holic.presentation.main.MainViewModel
import com.woody.cat.holic.presentation.main.MainViewModelFactory

class GalleryFragment : Fragment() {

    lateinit var activityViewModel: MainViewModel
    //lateinit var viewModel: GalleryViewModel

    lateinit var binding: FragmentGalleryBinding

    private val galleryAdapter: GalleryAdapter by lazy {
        GalleryAdapter(this, activityViewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DataBindingUtil.inflate<FragmentGalleryBinding>(inflater, R.layout.fragment_gallery, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            activityViewModel = ViewModelProvider(it, MainViewModelFactory()).get(MainViewModel::class.java).apply {
                binding.activityViewModel = this

                postingsLiveData.observe(this@GalleryFragment, { list ->
                    galleryAdapter.refreshData(list)
                })

                currentPostingOrder.observe(this@GalleryFragment, {
                    initPostings()
                })
            }
        }

        /*viewModel = ViewModelProvider(this, GalleryViewModelFactory()).get(GalleryViewModel::class.java)
            .apply {
                binding.viewModel = this
                postingsLiveData.observe(this@GalleryFragment, { list ->
                    galleryAdapter.refreshData(list.map { it })
                })
                viewModel.initPostings()
            }*/

        binding.rvMainGallery.adapter = galleryAdapter
    }
}