package fr.delcey.pokedexino.data.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import fr.delcey.pokedexino.data.utils.ApiLevelHelper
import fr.delcey.pokedexino.domain.repository.ConnectivityRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ConnectivityRepositoryImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager?,
    private val apiLevelHelper: ApiLevelHelper,
) : ConnectivityRepository {

    override fun isInternetAvailableFlow(): Flow<Boolean> = callbackFlow {
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
            if (apiLevelHelper.isAndroidApiLevelAtLeast(Build.VERSION_CODES.N)) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                // TODO NINO TEST WITH OLD DEVICE
                connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
            }

            awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
        }
    }.distinctUntilChanged()
}