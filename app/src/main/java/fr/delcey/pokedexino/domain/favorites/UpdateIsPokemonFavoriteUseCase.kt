package fr.delcey.pokedexino.domain.favorites

import fr.delcey.pokedexino.data.user.InterpolatedFavoritePokemonRepository
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.user.GetLoggedUserUseCase
import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import fr.delcey.pokedexino.ui.utils.loge
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdateIsPokemonFavoriteUseCase @Inject constructor(
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val userRepository: UserRepository,
    private val interpolatedFavoritePokemonRepository: InterpolatedFavoritePokemonRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    private val updateQuantityJobs = mutableMapOf<Long, Job>()

    suspend operator fun invoke(pokemonId: Long, isFavorite: Boolean): Boolean = try {
        withContext(coroutineDispatcherProvider.io) {
            interpolatedFavoritePokemonRepository.put(pokemonId, isFavorite)

            withTimeoutOrNull(3_000) {
                val updateQuantityJob = launch {
                    val userId = getLoggedUserUseCase.invoke().map { it?.uid }.filterNotNull().first()

                    delay(1_000) // Avoid extra write cost !
                    userRepository.setPokemonFavorite(userId, pokemonId, isFavorite)
                }

                val previous = updateQuantityJobs.put(pokemonId, updateQuantityJob)
                previous?.cancel()

                true
            } ?: false
        }
    } catch (e: Exception) {
        loge(e)
        false
    }
}