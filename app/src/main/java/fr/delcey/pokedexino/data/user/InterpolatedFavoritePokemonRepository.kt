package fr.delcey.pokedexino.data.user

import android.util.Log
import fr.delcey.pokedexino.data.GlobalScopeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterpolatedFavoritePokemonRepository @Inject constructor(
    @GlobalScopeCoroutineScope private val globalScope: CoroutineScope,
) {

    private val interpolatedFavoritePokemonIdsMutableStateFlow = MutableSharedFlow<MutableMap<String, Pair<Int, Boolean>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply {
        tryEmit(mutableMapOf())
        onEach {
            Log.d("Nino", "mutableStateFlow value changed : $it")
        }
    }
    val interpolatedFavoritePokemonIdsFlow: Flow<Map<String, Boolean>> =
        interpolatedFavoritePokemonIdsMutableStateFlow.map { interpolatedMap ->
            interpolatedMap.mapValues {
                it.value.second
            }
        }

    private val interpolationId = AtomicInteger()

    fun put(pokemonId: String, isFavorite: Boolean) {
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