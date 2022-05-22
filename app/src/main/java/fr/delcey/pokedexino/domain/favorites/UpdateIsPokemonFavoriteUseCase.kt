package fr.delcey.pokedexino.domain.favorites

import fr.delcey.pokedexino.data.user.InterpolatedFavoritePokemonRepository
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.user.GetLoggedUserUseCase
import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import fr.delcey.pokedexino.ui.utils.loge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class UpdateIsPokemonFavoriteUseCase @Inject constructor(
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val userRepository: UserRepository,
    private val interpolatedFavoritePokemonRepository: InterpolatedFavoritePokemonRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    private val updateQuantityJobs = mutableMapOf<Long, Job>()

    suspend operator fun invoke(pokemonId: Long, isFavorite: Boolean) = try {
        withContext(coroutineDispatcherProvider.io) {
            val interpolationId = if (isFavorite) {
                interpolatedFavoritePokemonRepository.add(pokemonId)
            } else {
                interpolatedFavoritePokemonRepository.remove(pokemonId)
            }

            withTimeoutOrNull(3_000) {
                val updateQuantityJob = launch {
                    try {
                        val userId = getLoggedUserUseCase.invoke().map { it?.uid }.filterNotNull().first()

                        delay(1_000) // Avoid extra write cost if user is spamming !

                        userRepository.setPokemonFavorite(userId, pokemonId, isFavorite)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        interpolatedFavoritePokemonRepository.invalidate(interpolationId)
                    }
                }

                val previous = updateQuantityJobs.put(pokemonId, updateQuantityJob)
                previous?.cancel()
            }
        }
    } catch (e: Exception) {
        loge(e)
    }
}