package com.woody.cat.holic.usecase.review

import com.woody.cat.holic.data.SettingRepository

class SetHaveToShowInAppReview(private val settingRepository: SettingRepository) {

    operator fun invoke(haveToShowInAppReview: Boolean) = settingRepository.setHaveToShowInAppReview(haveToShowInAppReview)
}