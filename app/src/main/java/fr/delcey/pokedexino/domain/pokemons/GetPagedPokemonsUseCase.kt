package fr.delcey.pokedexino.domain.pokemons

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

    private val aggregatedPokemonsMutableFlow = MutableStateFlow<List<PokemonEntity>>(emptyList())

    private val offset = AtomicInteger()

    fun get(): Flow<List<PokemonEntity>> = aggregatedPokemonsMutableFlow

    suspend fun loadNextPage() {
        aggregatedPokemonsMutableFlow.tryEmit(
            aggregatedPokemonsMutableFlow.value + pokemonRepository.getPagedPokemons(
                offset = offset.getAndAdd(LIMIT),
                limit = LIMIT
            )
        )
    }
}