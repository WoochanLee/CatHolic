package com.woody.cat.holic.usecase.posting

import com.woody.cat.holic.data.ReportPostingRepository

class ReportPosting(private val reportPostingRepository: ReportPostingRepository) {

    suspend operator fun invoke(userId: String, postingId: String) = reportPostingRepository.reportPosting(userId, postingId)
}