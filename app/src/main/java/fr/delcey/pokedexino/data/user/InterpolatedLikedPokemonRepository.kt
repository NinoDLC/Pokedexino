package fr.delcey.pokedexino.data.user

import fr.delcey.pokedexino.data.GlobalScopeCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterpolatedLikedPokemonRepository @Inject constructor(
    @GlobalScopeCoroutineScope private val globalScope: CoroutineScope,
) {

    private val interpolatedLikedPokemonIdsMutableStateFlow = MutableStateFlow(
        mapOf<String, Pair<Int, Boolean>>()
    )
    val interpolatedLikedPokemonIdsFlow: Flow<Map<String, Boolean>> =
        interpolatedLikedPokemonIdsMutableStateFlow.map { interpolatedQuantityMap ->
            interpolatedQuantityMap.mapValues {
                it.value.second
            }
        }

    private val interpolationId = AtomicInteger()

    fun put(pokemonId: String, isLiked: Boolean) {
        // Global scope use because if the scope is killed during the delay, the value will always be interpolated...
        globalScope.launch {
            val currentInterpolationId = interpolationId.getAndIncrement()

            interpolatedLikedPokemonIdsMutableStateFlow.update {
                it + Pair(pokemonId, Pair(currentInterpolationId, isLiked))
            }

            delay(2_000)

            interpolatedLikedPokemonIdsMutableStateFlow.update {
                if (it[pokemonId]?.first == currentInterpolationId) {
                    it - pokemonId
                } else {
                    it
                }
            }
        }
    }
}