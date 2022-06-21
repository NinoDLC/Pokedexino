package fr.delcey.pokedexino.data.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ConnectivityRepositoryImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager?,
) {

    fun isInternetAvailableFlow(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        if (connectivityManager == null) {
            trySend(false)
            close()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                // TODO NINO TEST
                connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
            }

            awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
        }
    }
}