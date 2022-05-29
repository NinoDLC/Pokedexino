package fr.delcey.pokedexino.domain.pokemons

import android.util.Log
import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetPagedLocalPokemonsUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    companion object {
        private const val OFFSET = 20L
        private const val LIMIT = OFFSET
    }

    // replay = Int.MAX_VALUE in case pageMutableFlow is updated before it is collected
    private val pageMutableFlow = MutableSharedFlow<Int>(replay = Int.MAX_VALUE)

    fun get(): Flow<List<PokemonEntity>> = channelFlow {
        val pagedPokemonEntities = mutableMapOf<Int, List<PokemonEntity>>()

        pageMutableFlow.collect { page ->
            launch {
                pokemonRepository.getLocalPokemonsFlow(limit = LIMIT, offset = page * OFFSET).collect { pokemonEntities ->
                    pagedPokemonEntities[page] = pokemonEntities
                    trySend(pagedPokemonEntities.values.flatten())
                }
            }
        }
    }.conflate().flowOn(coroutineDispatcherProvider.io)

    fun loadNextPage() {
        pageMutableFlow.tryEmit((pageMutableFlow.replayCache.firstOrNull()?.plus(1) ?: 1).also {
            Log.d("Nino", "(UC-Local)loadNextPage() called with page = $it")
        })
    }
}