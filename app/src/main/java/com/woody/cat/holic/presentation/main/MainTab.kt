package com.woody.cat.holic.presentation.main

enum class MainTab(val position: Int) {
    TAB_GALLERY(0),
    TAB_LIKE(1),
    TAB_USER(2);

    companion object {
        fun tabFromPosition(position: Int): MainTab {
            val map = values().associateBy(MainTab::position)
            return map[position] ?: throw IllegalArgumentException()
        }
    }
}