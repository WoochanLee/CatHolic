package com.woody.cat.holic.usecase.share

import com.woody.cat.holic.data.DynamicLinkManager

class GetDynamicLink(private val dynamicLinkManager: DynamicLinkManager) {

    suspend operator fun invoke(deepLink: String, description: String? = null, imageUrl: String? = null) = dynamicLinkManager.getDynamicLink(deepLink, description, imageUrl)
}