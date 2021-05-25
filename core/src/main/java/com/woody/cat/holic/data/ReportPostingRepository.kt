package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface ReportPostingRepository {
    suspend fun reportPosting(userId: String, postingId: String): Resource<Unit>
}