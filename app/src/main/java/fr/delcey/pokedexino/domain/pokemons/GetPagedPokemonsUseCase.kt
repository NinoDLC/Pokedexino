package fr.delcey.pokedexino.domain.pokemons

import fr.delcey.pokedexino.domain.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class GetPagedPokemonsUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
) {

    companion object {
        private const val LIMIT = 20
    }

    private val aggregatedPokemonsMutableFlow = MutableStateFlow(
        PokemonListDto(
            pokemons = emptyList(),
            hasMoreData = true,
            failureState = null,
        )
    )

    private val offset = AtomicInteger()

    fun get(): Flow<PokemonListDto> = aggregatedPokemonsMutableFlow

    suspend fun loadNextPage() {
        val result = pokemonRepository.getPagedPokemons(
            offset = offset.getAndAdd(LIMIT),
            limit = LIMIT
        )

        val currentFlowValue = aggregatedPokemonsMutableFlow.value

        aggregatedPokemonsMutableFlow.tryEmit(
            when (result) {
                is ApiResult.Success -> currentFlowValue.copy(
                    pokemons = currentFlowValue.pokemons + result.data,
                    hasMoreData = true,
                    failureState = null,
                )
                is ApiResult.Empty -> currentFlowValue.copy(
                    failureState = null,
                    hasMoreData = false,
                )
                is ApiResult.Failure -> currentFlowValue.copy( // TODO NINO RETRY ?
                    failureState = FailureState.RETRYING,
                )
            }
        )
    }

    data class PokemonListDto(
        val pokemons: List<PokemonEntity>,
        val hasMoreData: Boolean,
        val failureState: FailureState?,
    )

    enum class FailureState {
        RETRYING,
        TOO_MANY_ATTEMPTS
    }
}