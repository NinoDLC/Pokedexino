package fr.delcey.pokedexino.domain.pokemons

import fr.delcey.pokedexino.CoroutineDispatcherProvider
import fr.delcey.pokedexino.data.pokeapi.PokeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPagedPokemonsUseCase @Inject constructor(
    private val pokeApi: PokeApi, // TODO NINO Repository plutôt ?
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    companion object {
        private const val LIMIT = 20
    }

    private val aggregatedPokemonsMutableFlow = MutableStateFlow<List<PokemonEntity>>(emptyList())

    private var offset = 0

    fun get(): Flow<List<PokemonEntity>> = aggregatedPokemonsMutableFlow.mapLatest { pokemons ->
        pokemons.sortedBy { it.id }
    }

    suspend fun loadNextPage() {
        withContext(coroutineDispatcherProvider.io) {
            val currentOffset = offset
            offset += LIMIT
            val pagedPokemonsResponse = pokeApi.getPagedPokemons(currentOffset, LIMIT)

            val pokemonNamesToQuery = pagedPokemonsResponse?.results?.mapNotNull { it.name }

            pokemonNamesToQuery?.forEach { pokemonName ->
                launch {
                    val pokemonResponse = pokeApi.getPokemonByIdOrName(pokemonName)

                    if (pokemonResponse?.id != null && pokemonResponse.name != null && pokemonResponse.sprites?.frontDefault != null) {
                        aggregatedPokemonsMutableFlow.update {
                            it + PokemonEntity(
                                id = pokemonResponse.id,
                                name = pokemonResponse.name,
                                imageUrl = pokemonResponse.sprites.frontDefault,
                            )
                        }
                    }
                }
            }
        }
    }
}