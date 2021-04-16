package com.woody.cat.holic.data.common

enum class PostingOrder {
    CREATED,
    LIKED,
    RANDOM; //TODO: need more logic. (randomly combine order, value, etc..)

    fun getNextPostingOrder(): PostingOrder {
        return when (this) {
            LIKED -> CREATED
            CREATED -> RANDOM
            RANDOM -> LIKED
        }
    }
}