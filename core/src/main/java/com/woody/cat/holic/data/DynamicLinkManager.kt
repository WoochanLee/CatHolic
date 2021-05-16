package com.woody.cat.holic.data

import com.woody.cat.holic.data.common.Resource

interface DynamicLinkManager {

    suspend fun getDynamicLink(deepLink: String, description: String?, imageUrl: String?): Resource<String>
}