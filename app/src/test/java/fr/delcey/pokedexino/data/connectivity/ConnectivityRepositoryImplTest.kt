package fr.delcey.pokedexino.data.connectivity

import android.net.ConnectivityManager
import android.os.Build
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import fr.delcey.pokedexino.data.utils.ApiLevelHelper
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ConnectivityRepositoryImplTest {

    private val connectivityManager = mockk<ConnectivityManager>()
    private val apiLevelHelper = mockk<ApiLevelHelper>()

    private val connectivityRepository = ConnectivityRepositoryImpl(
        connectivityManager,
        apiLevelHelper
    )

    private val networkCallbackSlot = slot<ConnectivityManager.NetworkCallback>()

    @Before
    fun setUp() {
        justRun { connectivityManager.registerDefaultNetworkCallback(capture(networkCallbackSlot)) }
        justRun { connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>()) }
        every { apiLevelHelper.isAndroidApiLevelAtLeast(Build.VERSION_CODES.N) } returns true
    }

    @Test
    fun `nominal case - API 24 (N) and more`() = runTest {
        // When
        connectivityRepository.isInternetAvailableFlow().test {
            networkCallbackSlot.captured.onAvailable(mockk())

            // Then
            assertThat(awaitItem()).isTrue()
            cancel()
        }
    }

    @Test
    fun `nominal case - API 24 (N) and more - multiple emit`() = runTest {
        // When
        connectivityRepository.isInternetAvailableFlow().test {
            networkCallbackSlot.captured.onAvailable(mockk())
            networkCallbackSlot.captured.onAvailable(mockk())
            networkCallbackSlot.captured.onUnavailable()
            networkCallbackSlot.captured.onLost(mockk())
            networkCallbackSlot.captured.onAvailable(mockk())

            // Then
            assertThat(awaitItem()).isTrue()
            assertThat(awaitItem()).isFalse()
            assertThat(awaitItem()).isTrue()
            cancel()
        }
    }

    // This is just an example on how to test callbackFlows without Turbine,
    // I let this one "as it" for education purposes
    @Test
    fun `fallback case - API 21 (L) to API 23 (N)`() = runTest {
        // Given
        justRun { connectivityManager.registerNetworkCallback(any(), capture(networkCallbackSlot)) }

        every { apiLevelHelper.isAndroidApiLevelAtLeast(Build.VERSION_CODES.N) } returns false

        // When
        launch {
            val result = connectivityRepository.isInternetAvailableFlow().first()

            // Then
            assertThat(result).isTrue()
        }

        runCurrent()
        networkCallbackSlot.captured.onAvailable(mockk())
    }

    @Test
    fun `error case - if there's no ConnectivityManager, there's no internet`() = runTest {
        // Given
        val connectivityRepository = ConnectivityRepositoryImpl(connectivityManager = null, apiLevelHelper)

        // When
        val result = connectivityRepository.isInternetAvailableFlow().first()

        // Then
        assertThat(result).isFalse()
    }
}