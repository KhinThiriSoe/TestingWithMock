package com.khinthirisoe.testwithmockito.example7.networking

interface LoginHttpEndpointSync {
    /**
     * Log in using provided credentials
     * @return the aggregated result of login operation
     * @throws NetworkErrorException if login attempt failed due to network error
     */
    @Throws(NetworkErrorException::class)
    fun loginSync(username: String?, password: String?): EndpointResult?
    enum class EndpointResultStatus {
        SUCCESS, AUTH_ERROR, SERVER_ERROR, GENERAL_ERROR
    }

    class EndpointResult(val status: EndpointResultStatus, val authToken: String)
}