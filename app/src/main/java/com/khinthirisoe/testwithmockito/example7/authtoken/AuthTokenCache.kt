package com.khinthirisoe.testwithmockito.example7.authtoken

interface AuthTokenCache {
    fun cacheAuthToken(authToken: String?)
    val authToken: String?
}