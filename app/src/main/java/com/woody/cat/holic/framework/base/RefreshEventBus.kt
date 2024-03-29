package com.woody.cat.holic.framework.base

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class RefreshEventBus {
    private val events = MutableSharedFlow<GlobalRefreshEvent>()

    fun emitEvent(event: GlobalRefreshEvent) {
        GlobalScope.launch {
            events.emit(event)
        }
    }

    suspend fun subscribeEvent(vararg globalRefreshEvents: GlobalRefreshEvent, onEvent: () -> Unit) {
        events.filter { globalRefreshEvents.contains(it) }.collect { onEvent() }
    }
}

enum class GlobalRefreshEvent {
    POSTING_LIKED_CHANGE_EVENT,
    UPLOAD_POSTING_EVENT,
    DELETE_POSTING_EVENT,
    FOLLOW_USER_EVENT,
    UPDATE_USER_PROFILE_EVENT,
    SIGN_IN_STATUS_CHANGE_EVENT
}