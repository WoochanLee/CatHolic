package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface PhotoAnalyzer {

    suspend fun detectCatFromLocalPhoto(uri: String): Resource<Boolean>
}