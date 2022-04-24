package fr.delcey.pokedexino.data

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNodeResolver @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) {
    companion object {
        private const val COLLECTION_PATH_USERS = "users"
        private const val COLLECTION_PATH_LIKED_POKEMON = "liked_pokemons"
    }

    fun getUsersCollection() = firebaseFirestore.collection(COLLECTION_PATH_USERS)

    fun getUserDocument(userId: String) = firebaseFirestore.document("$COLLECTION_PATH_USERS/$userId")

    fun getLikedPokemonsCollection(userId: String) = firebaseFirestore.collection(
        "$COLLECTION_PATH_USERS/$userId/$COLLECTION_PATH_LIKED_POKEMON"
    )
}