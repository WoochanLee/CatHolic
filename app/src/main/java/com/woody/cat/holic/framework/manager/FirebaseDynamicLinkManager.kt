package com.woody.cat.holic.framework.manager

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.woody.cat.holic.BuildConfig
import com.woody.cat.holic.data.DynamicLinkManager
import com.woody.cat.holic.data.common.Resource
import kotlinx.coroutines.tasks.await

class FirebaseDynamicLinkManager : DynamicLinkManager {

    companion object {
        private const val DOMAIN_URI_PREFIX = "https://woody.page.link"
        const val SHARE_PHOTO_PREFIX = "https://www.woodycatholic.com/main"
        const val WOODY_CAT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/catholic-c8378.appspot.com/o/common%2Fcongshu_cat.png?alt=media&token=f3d18b57-558a-46a8-a703-aa500a06bb4b"
    }

    override suspend fun getDynamicLink(deepLink: String, description: String?, imageUrl: String?): Resource<String> {
        return try {
            val result = Firebase.dynamicLinks.shortLinkAsync {
                link = Uri.parse(deepLink)
                domainUriPrefix = DOMAIN_URI_PREFIX

                androidParameters(BuildConfig.APPLICATION_ID) {}
                socialMetaTagParameters {
                    title = "CAT HOLIC"
                    description?.let {
                        setDescription(description)
                    }
                    imageUrl?.let {
                        setImageUrl(Uri.parse(imageUrl))
                    }
                }
            }.await()
            Resource.Success(result.shortLink.toString())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}