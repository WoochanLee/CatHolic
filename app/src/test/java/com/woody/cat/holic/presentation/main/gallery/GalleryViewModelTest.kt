package com.woody.cat.holic.presentation.main.gallery

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jraska.livedata.test
import com.woody.cat.holic.data.common.PostingType
import com.woody.cat.holic.framework.base.RefreshEventBus
import com.woody.cat.holic.usecase.posting.ChangeToNextPostingOrder
import com.woody.cat.holic.usecase.posting.GetGalleryPostings
import com.woody.cat.holic.usecase.user.GetCurrentUserId
import com.woody.cat.holic.usecase.user.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.*
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GalleryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var galleryViewModel: GalleryViewModel

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private val mockRefreshEventBus = mock(RefreshEventBus::class.java)
    private val changeToNextPostingOrder = mock(ChangeToNextPostingOrder::class.java)
    private val getCurrentUserId = mock(GetCurrentUserId::class.java)
    private val getGalleryPostings = mock(GetGalleryPostings::class.java)
    private val getUserProfile = mock(GetUserProfile::class.java)

    @Before
    fun before() {
        reset(
            mockRefreshEventBus,
            changeToNextPostingOrder,
            getCurrentUserId,
            getGalleryPostings,
            getUserProfile
        )

        Dispatchers.setMain(mainThreadSurrogate)
        galleryViewModel = GalleryViewModel(
            mockRefreshEventBus,
            changeToNextPostingOrder,
            getCurrentUserId,
            getGalleryPostings,
            getUserProfile
        )
    }

    @After
    fun after() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun getGalleryPostingFlow() {
        galleryViewModel.getGalleryPostingFlow()
    }

    @Test
    fun testSetLoading() {
        Dispatchers.resetMain()
        galleryViewModel.setLoading(true)

        assert(galleryViewModel.isLoading.value == true)
    }

    @Test
    fun testInitData() {
        galleryViewModel.initData()

        galleryViewModel.eventRefreshData.test()
            .awaitValue()
            .assertHasValue()
    }

    @Test
    fun testChangeToNextPostingOrder() {
        galleryViewModel.changeToNextPostingOrder()

        verify(changeToNextPostingOrder, times(1)).invoke(PostingType.GALLERY)
    }
}