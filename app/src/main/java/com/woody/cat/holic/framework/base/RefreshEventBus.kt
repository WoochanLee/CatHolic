package com.woody.cat.holic.framework.base

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class RefreshEventBus {
    private val events = MutableSharedFlow<GlobalRefreshEvent>()

    fun produceEvent(event: GlobalRefreshEvent) {
        GlobalScope.launch {
            events.emit(event)
        }
    }

    suspend fun subscribeEvent(globalRefreshEvent: GlobalRefreshEvent, onEvent: () -> Unit) {
        events.filter { it == globalRefreshEvent }.collect { onEvent() }
    }
}

enum class GlobalRefreshEvent {
    LikedPostingChangeEvent,
    UploadPostingEvent
}