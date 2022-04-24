package fr.delcey.pokedexino.domain.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.delcey.pokedexino.ui.utils.logi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * Returns a [Flow] of [FirebaseUser]?, representing if the user is currently logged to FirebaseAuth, or null if not connected
 */
class GetLoggedUserUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {

    operator fun invoke(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                logi("FirebaseUser is null")
            }
            trySend(it.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }
}