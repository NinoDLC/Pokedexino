package fr.delcey.pokedexino.domain.pokemons

import android.util.Log
import fr.delcey.pokedexino.domain.GlobalCoroutineScope
import fr.delcey.pokedexino.domain.utils.ApiResult
import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetPagedPokemonsUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    @GlobalCoroutineScope
    private val globalCoroutineScope: CoroutineScope,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    companion object {
        private const val LIMIT = 20L

        private val RETRY_BASE_DELAY = 1.seconds
    }

    private val aggregatedRemotePokemonsMutableFlow = MutableStateFlow(
        PokemonListDto(
            pokemons = emptyList(),
            hasMoreData = true,
            failureState = null,
        )
    )
    private val maxLimitMutableFlow = MutableStateFlow(0L)

    private var retryCount = 0

    fun get(): Flow<PokemonListDto> = combine(
        aggregatedRemotePokemonsMutableFlow,
        maxLimitMutableFlow.flatMapLatest { maxLimit -> pokemonRepository.getLocalPokemonsFlow(limit = maxLimit, offset = 1) }
    ) { aggregatedRemotePokemons, localPokemons ->

        val allIds = aggregatedRemotePokemons.pokemons.map { it.id }
            .plus(localPokemons.map { it.id })
            .toSet()
            .sorted()

        Log.d("Nino", "get() called with: allIds = $allIds")

        aggregatedRemotePokemons.copy(
            pokemons = allIds.map { id ->
                aggregatedRemotePokemons.pokemons.firstOrNull { it.id == id } ?: localPokemons.first { it.id == id }
            }
        )
    }

    suspend fun loadNextPage() {
        Log.d("Nino", "loadNextPage() called, maxLimitMutableFlow.value = ${maxLimitMutableFlow.value}")
        val result = pokemonRepository.getRemotePagedPokemons(
            limit = maxLimitMutableFlow.updateAndGet { it + LIMIT },
            offset = 1
        )

        if (result !is ApiResult.Failure.IoException) {
            retryCount = 0
        }

        when (result) {
            is ApiResult.Success -> {
                globalCoroutineScope.launch(coroutineDispatcherProvider.io) {
                    pokemonRepository.upsertLocalPokemons(result.data)
                }

                aggregatedRemotePokemonsMutableFlow.tryEmit(
                    aggregatedRemotePokemonsMutableFlow.value.let {
                        it.copy(
                            pokemons = it.pokemons + result.data,
                            hasMoreData = true,
                            failureState = null,
                        )
                    }
                )
            }
            is ApiResult.Empty -> aggregatedRemotePokemonsMutableFlow.tryEmit(
                aggregatedRemotePokemonsMutableFlow.value.copy(
                    failureState = null,
                    hasMoreData = false,
                )
            )
            is ApiResult.Failure.IoException -> if (retryCount >= 5) {
                aggregatedRemotePokemonsMutableFlow.tryEmit(
                    aggregatedRemotePokemonsMutableFlow.value.copy(failureState = FailureState.TOO_MANY_ATTEMPTS)
                )
            } else {
                retryCount++

                delay(RETRY_BASE_DELAY * retryCount)

                loadNextPage()
            }
            is ApiResult.Failure.ApiException -> aggregatedRemotePokemonsMutableFlow.tryEmit(
                aggregatedRemotePokemonsMutableFlow.value.copy(failureState = FailureState.CRITICAL)
            )
        }
    }

    data class PokemonListDto(
        val pokemons: List<PokemonEntity>,
        val hasMoreData: Boolean,
        val failureState: FailureState?,
    )

    enum class FailureState {
        TOO_MANY_ATTEMPTS,
        CRITICAL,
    }
}