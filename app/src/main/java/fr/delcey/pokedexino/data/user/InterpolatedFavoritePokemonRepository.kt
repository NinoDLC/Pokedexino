package fr.delcey.pokedexino.data.user

import fr.delcey.pokedexino.domain.GlobalCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterpolatedFavoritePokemonRepository @Inject constructor(
    @GlobalCoroutineScope private val globalScope: CoroutineScope,
) {

    // PokemonId to (InterpolationId,isPokemonFavorite)
    private val interpolatedFavoritePokemonIdsMutableStateFlow = MutableSharedFlow<MutableMap<Long, Pair<Long, Boolean>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply {
        tryEmit(mutableMapOf())
    }
    val interpolatedFavoritePokemonIdsFlow: Flow<Map<Long, Boolean>> =
        interpolatedFavoritePokemonIdsMutableStateFlow.map { interpolatedMap ->
            interpolatedMap.mapValues {
                it.value.second
            }
        }

    private val interpolationId = AtomicLong()

    fun put(pokemonId: Long, isFavorite: Boolean) {
        // Global scope use because if the scope is killed during the delay, the value will always be interpolated...
        globalScope.launch {
            val currentInterpolationId = interpolationId.getAndIncrement()

            interpolatedFavoritePokemonIdsMutableStateFlow.tryEmit(
                interpolatedFavoritePokemonIdsMutableStateFlow.replayCache.first().apply {
                    put(pokemonId, Pair(currentInterpolationId, isFavorite))
                }
            )

            delay(2_000)

            val map = interpolatedFavoritePokemonIdsMutableStateFlow.replayCache.first()

            if (map[pokemonId]?.first == currentInterpolationId) {
                interpolatedFavoritePokemonIdsMutableStateFlow.tryEmit(
                    map.apply {
                        remove(pokemonId)
                    }
                )
            }
        }
    }
}