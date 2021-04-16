package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.PostingRepository
import com.woody.cat.holic.data.common.PostingType

class ChangeToNextPostingOrder(private val postingRepository: PostingRepository) {

    operator fun invoke(postingType: PostingType) = postingRepository.changeToNextPostingOrder(postingType)
}