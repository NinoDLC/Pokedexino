package fr.delcey.pokedexino.domain.pokemons

import fr.delcey.pokedexino.domain.pokemons.GetPagedRemotePokemonsQueryStateUseCase.FailureState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPagedPokemonsUseCase @Inject constructor(
    private val getPagedLocalPokemonsUseCase: GetPagedLocalPokemonsUseCase,
    private val getPagedRemotePokemonsQueryStateUseCase: GetPagedRemotePokemonsQueryStateUseCase,
) {

    fun get(): Flow<PokemonListDto> = combine(
        getPagedLocalPokemonsUseCase.get(),
        getPagedRemotePokemonsQueryStateUseCase.get()
    ) { localPokemons, remotePokemonsQueryState ->
        PokemonListDto(
            pokemons = localPokemons,
            hasMoreData = remotePokemonsQueryState.hasMoreData,
            failureState = remotePokemonsQueryState.failureState,
        )
    }

    suspend fun loadNextPage() {
        getPagedLocalPokemonsUseCase.loadNextPage()
        getPagedRemotePokemonsQueryStateUseCase.loadNextPage()
    }

    data class PokemonListDto(
        val pokemons: List<PokemonEntity>,
        val hasMoreData: Boolean,
        val failureState: FailureState?,
    )
}