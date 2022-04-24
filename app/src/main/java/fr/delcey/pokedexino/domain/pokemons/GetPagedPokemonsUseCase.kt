package fr.delcey.pokedexino.domain.pokemons

import fr.delcey.pokedexino.CoroutineDispatcherProvider
import fr.delcey.pokedexino.data.pokemons.pokeapi.PokeApi
import kotlinx.coroutines.flow.*
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
        pokemons.sortedBy { it.id.toInt() }
    }.debounce(200)

    suspend fun loadNextPage() {
        withContext(coroutineDispatcherProvider.io) {
            val currentOffset = offset
            offset += LIMIT

        }
    }
}