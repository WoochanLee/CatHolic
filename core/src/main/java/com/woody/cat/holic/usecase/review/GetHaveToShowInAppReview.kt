package com.woody.cat.holic.usecase.review

import com.woody.cat.holic.data.SettingRepository

class GetHaveToShowInAppReview(private val settingRepository: SettingRepository) {

    operator fun invoke() = settingRepository.getHaveToShowInAppReview()
}