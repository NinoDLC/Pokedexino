package fr.delcey.pokedexino.data.pokemons

import android.util.Log
import androidx.annotation.IntRange
import fr.delcey.pokedexino.data.pokemons.local.PokemonDao
import fr.delcey.pokedexino.data.pokemons.pokeapi.PokeApi
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity
import fr.delcey.pokedexino.domain.pokemons.PokemonRepository
import fr.delcey.pokedexino.domain.utils.ApiResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.security.InvalidParameterException
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val pokeApi: PokeApi,
) : PokemonRepository {
    override suspend fun getLocalPokemonById(pokemonId: Long): PokemonEntity? {
        TODO("Not yet implemented")
    }

    // Room OFFSET keyword implementation is even worse, it will re-emit values to the flow even if the underlying values didn't change.
    // If data is appended at the end of the table (for example id == 101+), all the flows "targeting" ids 1 to 100 will re-emit for nothing.
    override fun getLocalPokemonsFlow(limit: Long, offset: Long): Flow<List<PokemonEntity>> = pokemonDao.getPokemonsAsFlow(
        limit = limit,
        offset = offset
    ).distinctUntilChanged()

    override suspend fun upsertLocalPokemons(pokemons: List<PokemonEntity>) {
        Log.d("Nino", "upsertLocalPokemons() called with: pokemons = ${pokemons.size}, first id = ${pokemons.firstOrNull()?.id}")
        pokemonDao.updateAll(pokemons)
    }

    override suspend fun getRemotePagedPokemons(
        @IntRange(from = 0, to = Long.MAX_VALUE) offset: Long,
        @IntRange(from = 1, to = Long.MAX_VALUE) limit: Long,
    ): ApiResult<List<PokemonEntity>> = when {
        offset < 0 -> throw InvalidParameterException("Offset value is $offset")
        limit < 1 -> throw InvalidParameterException("Limit value is $offset")
        else -> try {
            val pokemons: List<PokemonEntity?> = coroutineScope {
                (offset..offset + limit).map { pokemonIdToQuery ->
                    async {
                        val pokemonResponse = pokeApi.getPokemonById(pokemonIdToQuery)

                        if (pokemonResponse?.id != null && pokemonResponse.name != null && pokemonResponse.sprites?.frontDefault != null) {
                            PokemonEntity(
                                id = pokemonResponse.id,
                                name = pokemonResponse.name,
                                imageUrl = pokemonResponse.sprites.frontDefault,
                            )
                        } else {
                            null
                        }
                    }
                }.awaitAll()
            }

            val nonNull = pokemons.filterNotNull()

            if (nonNull.size == pokemons.size) {
                ApiResult.Success(nonNull)
            } else {
                ApiResult.Failure.ApiException()
            }
        } catch (e: Exception) {
            ApiResult.Failure.ApiException()
        }
    }
}