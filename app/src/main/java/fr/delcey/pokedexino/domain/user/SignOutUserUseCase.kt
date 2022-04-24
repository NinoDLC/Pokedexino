package fr.delcey.pokedexino.domain.user

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) {
    operator fun invoke() {
        firebaseAuth.signOut()
    }
}