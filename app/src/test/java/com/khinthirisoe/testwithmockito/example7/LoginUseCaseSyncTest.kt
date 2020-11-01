import com.khinthirisoe.testwithmockito.example7.LoginUseCaseSync
import com.khinthirisoe.testwithmockito.example7.authtoken.AuthTokenCache
import com.khinthirisoe.testwithmockito.example7.eventbus.EventBusPoster
import com.khinthirisoe.testwithmockito.example7.eventbus.LoggedInEvent
import com.khinthirisoe.testwithmockito.example7.networking.LoginHttpEndpointSync
import com.khinthirisoe.testwithmockito.example7.networking.NetworkErrorException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginUseCaseSyncTest {

    companion object {
        val USERNAME = "username"
        val PASSWORD = "password"
        val AUTH_TOKEN = "authToken"
    }

    lateinit var SUT : LoginUseCaseSync
    @Mock lateinit var loginHttpEndpointSyncMock: LoginHttpEndpointSync
    @Mock lateinit var authTokenCacheMock: AuthTokenCache
    @Mock lateinit var postEventBusPosterMock: EventBusPoster

    @Before
    @Throws(Exception::class)
    fun setup() {
        SUT = LoginUseCaseSync(
            loginHttpEndpointSyncMock,
            authTokenCacheMock,
            postEventBusPosterMock
        )
        success()
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_usernameAndPasswordPassedToEndpoint() {
        val ac: ArgumentCaptor<String> = ArgumentCaptor.forClass(String::class.java)
        SUT.loginSync(USERNAME, PASSWORD)
        verify(loginHttpEndpointSyncMock, times(1)).loginSync(ac.capture(), ac.capture())
        val captures = ac.allValues
        assertThat(captures[0], `is`(USERNAME))
        assertThat(captures[1], `is`(PASSWORD))
    }

    // AAA pattern for unit test
    @Test
    @Throws(Exception::class)
    fun loginSync_success_authTokenCached() {
        // Arrange
        val ac = ArgumentCaptor.forClass(String::class.java)
        // Art
        SUT.loginSync(USERNAME, PASSWORD)
        // Assert
        verify(authTokenCacheMock).cacheAuthToken(ac.capture())
        assertThat(ac.value, `is`(AUTH_TOKEN))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_generalError_authTokenNotCached() {
        generalError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(authTokenCacheMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_authError_authTokenNotCached() {
        authError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(authTokenCacheMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_serverError_authTokenNotCached() {
        serverError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(authTokenCacheMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_loggedInEventPosted() {
        val ac = ArgumentCaptor.forClass(String::class.java)
        SUT.loginSync(USERNAME, PASSWORD)
        verify(postEventBusPosterMock).postEvent(ac.capture())
        assertThat(ac.value, `is`(instanceOf(LoggedInEvent::class.java)))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_generalError_noInteractionWithLoggedInEventPosted() {
        generalError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(postEventBusPosterMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_authError_noInteractionWithLoggedInEventPosted() {
        authError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(postEventBusPosterMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_serverError_noInteractionWithLoggedInEventPosted() {
        serverError()
        SUT.loginSync(USERNAME, PASSWORD)
        verifyNoMoreInteractions(postEventBusPosterMock)
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_success_successReturned() {
        val userResult = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(userResult, `is`(LoginUseCaseSync.UseCaseResult.SUCCESS))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_generalError_failureReturned() {
        generalError()
        val userResult = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(userResult, `is`(LoginUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_authError_failureReturned() {
        authError()
        val userResult = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(userResult, `is`(LoginUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_serverError_failureReturned() {
        serverError()
        val userResult = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(userResult, `is`(LoginUseCaseSync.UseCaseResult.FAILURE))
    }

    @Test
    @Throws(Exception::class)
    fun loginSync_networkError_networkErrorReturned() {
        networkError()
        val userResult = SUT.loginSync(USERNAME, PASSWORD)
        assertThat(userResult, `is`(LoginUseCaseSync.UseCaseResult.NETWORK_ERROR))
    }

    @Throws(Exception::class)
    private fun networkError() {
        doThrow(NetworkErrorException())
        .`when`(
            loginHttpEndpointSyncMock).loginSync(
            any(String::class.java),
            any(String::class.java)
        )
    }

    @Throws(NetworkErrorException::class)
    private fun success() {
        `when`(
            loginHttpEndpointSyncMock.loginSync(any(String::class.java), any(String::class.java))
        )
            .thenReturn(
                LoginHttpEndpointSync.EndpointResult(
                    LoginHttpEndpointSync.EndpointResultStatus.SUCCESS,
                    AUTH_TOKEN
                )
            )

    }

    @Throws(Exception::class)
    private fun generalError() {
        `when`(
            loginHttpEndpointSyncMock.loginSync(any(String::class.java), any(String::class.java))
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                ""
            )
        )

    }

    @Throws(Exception::class)
    private fun authError() {
        `when`(
            loginHttpEndpointSyncMock.loginSync(any(String::class.java), any(String::class.java))
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                ""
            )
        )

    }

    @Throws(Exception::class)
    private fun serverError() {
        `when`(
            loginHttpEndpointSyncMock.loginSync(any(String::class.java), any(String::class.java))
        ).thenReturn(
            LoginHttpEndpointSync.EndpointResult(
                LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                ""
            )
        )

    }

}