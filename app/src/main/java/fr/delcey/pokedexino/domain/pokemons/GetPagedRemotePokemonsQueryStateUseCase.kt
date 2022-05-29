package fr.delcey.pokedexino.domain.pokemons

import android.util.Log
import fr.delcey.pokedexino.domain.utils.ApiResult
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject

class GetPagedRemotePokemonsQueryStateUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) {

    companion object {
        private const val OFFSET = 20L
        private const val LIMIT = OFFSET

        private val RETRY_BASE_DELAY = 1.seconds
        private val RETRY_MAX_DELAY = 30.seconds
    }

    private val pageMutableFlow = MutableStateFlow(0L)
    private val remotePokemonsQueryStateMutableFlow = MutableStateFlow(RemotePokemonsQueryState(hasMoreData = true, failureState = null))

    private var retryCount = 0

    fun get(): Flow<RemotePokemonsQueryState> = remotePokemonsQueryStateMutableFlow.asStateFlow()

    suspend fun loadNextPage() {
        loadPage(page = pageMutableFlow.updateAndGet { it + 1 })
    }

    private suspend fun loadPage(page: Long) {
        Log.d("Nino", "(UC-Remote)loadPage() called with page = $page")

        val result = pokemonRepository.getRemotePagedPokemons(
            limit = LIMIT,
            offset = page * OFFSET
        )

        if (result !is ApiResult.Failure.IoException) {
            retryCount = 0
        }

        when (result) {
            is ApiResult.Success -> {
                pokemonRepository.upsertLocalPokemons(result.data)

                remotePokemonsQueryStateMutableFlow.update { previousState ->
                    previousState.copy(
                        hasMoreData = true,
                        failureState = null
                    )
                }
            }
            is ApiResult.Empty -> remotePokemonsQueryStateMutableFlow.update { previousState ->
                previousState.copy(
                    hasMoreData = false,
                    failureState = null
                )
            }
            is ApiResult.Failure.IoException -> {
                if (retryCount >= 5) {
                    remotePokemonsQueryStateMutableFlow.update { previousState ->
                        previousState.copy(failureState = FailureState.TOO_MANY_ATTEMPTS)
                    }
                }
                retryCount++

                delay((RETRY_BASE_DELAY * retryCount).coerceAtMost(RETRY_MAX_DELAY))

                loadPage(page)
            }
            is ApiResult.Failure.ApiException -> remotePokemonsQueryStateMutableFlow.update { previousState ->
                previousState.copy(failureState = FailureState.CRITICAL)
            }
        }
    }

    data class RemotePokemonsQueryState(
        val hasMoreData: Boolean,
        val failureState: FailureState?,
    )

    enum class FailureState {
        TOO_MANY_ATTEMPTS,
        CRITICAL,
    }
}