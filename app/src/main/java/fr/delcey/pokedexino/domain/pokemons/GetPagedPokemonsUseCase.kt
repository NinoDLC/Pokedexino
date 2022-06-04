package fr.delcey.pokedexino.domain.pokemons

import android.util.Log
import fr.delcey.pokedexino.domain.utils.ApiResult
import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class GetPagedPokemonsUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    companion object {
        private const val OFFSET = 20L
        private const val LIMIT = OFFSET

        private val RETRY_BASE_DELAY = 1.seconds
        private val RETRY_MAX_DELAY = 30.seconds
    }

    // replay = Int.MAX_VALUE in case pageMutableFlow is updated before it is collected
    private val pageMutableFlow = MutableSharedFlow<Long>(replay = Int.MAX_VALUE).apply {
        tryEmit(0)
    }

    private val remotePokemonsQueryStateMutableFlow = MutableStateFlow(
        RemotePokemonsQueryState(
            hasMoreData = true,
            failureState = null
        )
    )
    private var retryCount = 0

    fun get(): Flow<PokemonListDto> = combine(
        getLocalFlow(),
        remotePokemonsQueryStateMutableFlow,
    ) { localPokemons: TreeMap<Long, List<PokemonEntity>>, remotePokemonsQueryState: RemotePokemonsQueryState ->
        PokemonListDto(
            pokemons = localPokemons.values.flatten().also {
                Log.d("Nino", "get() called")
            },
            hasMoreData = remotePokemonsQueryState.hasMoreData,
            failureState = remotePokemonsQueryState.failureState,
        )
    }

    /**
     * Suspends until a response from the remote API is received (successful or not)
     */
    suspend fun loadNextPage() {
        val page = pageMutableFlow.replayCache.last() + 1
        pageMutableFlow.tryEmit(page)

        loadRemotePage(page = page)
    }

    private fun getLocalFlow(): Flow<TreeMap<Long, List<PokemonEntity>>> = channelFlow {
        val pagedPokemonEntities = TreeMap<Long, List<PokemonEntity>>()

        pageMutableFlow.collect { page ->
            launch {
                pokemonRepository.getLocalPokemonsFlow(
                    limit = LIMIT,
                    offset = getOffset(page)
                ).collect { pokemonEntities ->
                    Log.d("Nino", "getLocalFlow() called for page = $page, entities = ${pokemonEntities.size}")
                    pagedPokemonEntities[page] = pokemonEntities
                    trySend(pagedPokemonEntities)
                }
            }
        }
    }.conflate().flowOn(coroutineDispatcherProvider.io)

    private suspend fun loadRemotePage(page: Long) {
        val result = pokemonRepository.getRemotePagedPokemons(
            limit = LIMIT,
            offset = getOffset(page)
        )

        if (result !is ApiResult.Failure.IoException) {
            retryCount = 0
        }

        when (result) {
            is ApiResult.Success -> {
                pokemonRepository.upsertLocalPokemons(result.data)

                remotePokemonsQueryStateMutableFlow.value = RemotePokemonsQueryState(
                    hasMoreData = true,
                    failureState = null
                )
            }
            is ApiResult.Empty -> remotePokemonsQueryStateMutableFlow.value = RemotePokemonsQueryState(
                hasMoreData = false,
                failureState = null
            )
            is ApiResult.Failure.IoException -> {
                if (retryCount >= 5) {
                    remotePokemonsQueryStateMutableFlow.value = RemotePokemonsQueryState(
                        hasMoreData = true,
                        failureState = FailureState.TOO_MANY_ATTEMPTS
                    )
                }
                retryCount++

                delay((RETRY_BASE_DELAY * retryCount).coerceAtMost(RETRY_MAX_DELAY))

                loadRemotePage(page)
            }
            is ApiResult.Failure.ApiException -> remotePokemonsQueryStateMutableFlow.value = RemotePokemonsQueryState(
                hasMoreData = true,
                failureState = FailureState.CRITICAL
            )
        }
    }

    private fun getOffset(page: Long) = page * OFFSET + 1

    data class PokemonListDto(
        val pokemons: List<PokemonEntity>,
        val hasMoreData: Boolean,
        val failureState: FailureState?,
    )

    data class RemotePokemonsQueryState(
        val hasMoreData: Boolean,
        val failureState: FailureState?,
    )

    enum class FailureState {
        TOO_MANY_ATTEMPTS,
        CRITICAL,
    }
}