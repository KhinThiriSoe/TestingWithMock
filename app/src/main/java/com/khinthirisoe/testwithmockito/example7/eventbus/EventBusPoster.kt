package com.khinthirisoe.testwithmockito.example7.eventbus

interface EventBusPoster {
    fun postEvent(event: Any?)
}