package fr.delcey.pokedexino.data.user

import com.google.firebase.firestore.ktx.toObject
import fr.delcey.pokedexino.data.FirebaseNodeResolver
import fr.delcey.pokedexino.domain.user.entity.UserEntity
import fr.delcey.pokedexino.ui.utils.loge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseNodeResolver: FirebaseNodeResolver,
) {

    companion object {
        private val EMPTY_DOCUMENT = hashMapOf<Any, Any>()
    }

    fun getUser(userId: String): Flow<UserResponse.Down> = callbackFlow {
        val listener = firebaseNodeResolver.getUserDocument(userId)
            .addSnapshotListener { value, error ->
                val result = try {
                    value?.toObject<UserResponse.Down>()?.copy(id = value.id)
                } catch (e: Exception) {
                    loge(e)
                    null
                }

                if (error != null) {
                    loge(error)
                }

                if (result != null) {
                    trySendBlocking(result)
                }
            }
        awaitClose { listener.remove() }
    }.conflate()

    suspend fun insertUser(userEntity: UserEntity) {
        firebaseNodeResolver.getUsersCollection()
            .document(userEntity.id)
            .set(
                UserResponse.Up(
                    name = userEntity.name,
                    email = userEntity.email,
                    photoUrl = userEntity.photoUrl
                )
            )
            .await()
    }

    fun getFavoritePokemonIds(userId: String): Flow<List<Long>> = callbackFlow {
        val listener = firebaseNodeResolver.getFavoritePokemonsCollection(userId)
            .addSnapshotListener { value, error ->
                val favoritePokemonIds = value?.documents?.map { it.id.toLong() }

                if (error != null) {
                    loge(error)
                }

                trySendBlocking(favoritePokemonIds ?: emptyList())
            }
        awaitClose { listener.remove() }
    }.conflate()

    suspend fun setPokemonFavorite(userId: String, pokemonId: Long, isFavorite: Boolean) {
        if (isFavorite) {
            firebaseNodeResolver.getFavoritePokemonsCollection(userId)
                .document(pokemonId.toString())
                .set(EMPTY_DOCUMENT)
                .await()
        } else {
            firebaseNodeResolver.getFavoritePokemonsCollection(userId)
                .document(pokemonId.toString())
                .delete()
                .await()
        }
    }
}