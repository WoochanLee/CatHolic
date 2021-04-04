package com.woody.cat.holic.presentation.main

import com.woody.cat.holic.presentation.main.gallery.GalleryFragment
import com.woody.cat.holic.presentation.main.like.LikeFragment
import com.woody.cat.holic.presentation.main.user.UserFragment

enum class MainTab(val position: Int) {
    TAB_GALLERY(0),
    TAB_LIKE(1),
    TAB_USER(2);

    companion object {
        fun tabFromPosition(position: Int): MainTab {
            val map = values().associateBy(MainTab::position)
            return map[position] ?: throw IllegalArgumentException()
        }

        fun positionFormClass(className: String?): Int {
            return when(className) {
                GalleryFragment::class.java.name -> 0
                LikeFragment::class.java.name -> 1
                UserFragment::class.java.name -> 2
                else -> 0
            }
        }
    }
}