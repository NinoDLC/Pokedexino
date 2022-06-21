package fr.delcey.pokedexino.data.connectivity

import android.net.ConnectivityManager
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ConnectivityRepositoryImplTest {

    private val connectivityManager = mockk<ConnectivityManager>()

    private val connectivityRepositoryImpl = ConnectivityRepositoryImpl(connectivityManager)

    @Test
    fun `nominal case - API 24 (N)`() = runTest {
        // Given
        val networkCallbackSlot = slot<ConnectivityManager.NetworkCallback>()
        justRun { connectivityManager.registerNetworkCallback(any(), capture(networkCallbackSlot)) }
        justRun { connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>()) }

        // When
        val result = connectivityRepositoryImpl.isInternetAvailableFlow().onStart {
            launch {
                delay(1)
                networkCallbackSlot.captured.onAvailable(mockk())
            }
        }.first()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `error case - if there's no ConnectivityManager, there's no internet`() = runTest {
        // Given
        val connectivityRepositoryImpl = ConnectivityRepositoryImpl(connectivityManager = null)

        // When
        val result = connectivityRepositoryImpl.isInternetAvailableFlow().first()

        // Then
        assertThat(result).isFalse()
    }
}