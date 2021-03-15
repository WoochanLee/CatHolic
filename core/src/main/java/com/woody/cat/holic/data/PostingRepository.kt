package com.woody.cat.holic.data

import com.woody.cat.holic.domain.Posting

interface PostingRepository {

    suspend fun addPostings(postings: List<Posting>)
}