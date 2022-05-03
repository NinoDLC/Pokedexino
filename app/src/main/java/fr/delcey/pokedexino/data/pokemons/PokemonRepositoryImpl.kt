package fr.delcey.pokedexino.data.pokemons

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
import java.security.InvalidParameterException
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonDao: PokemonDao,
    private val pokeApi: PokeApi,
) : PokemonRepository {
    override suspend fun getLocalPokemonById(pokemonId: Long): PokemonEntity? {
        TODO("Not yet implemented")
    }

    override fun getLocalPokemonsFlow(limit: Long, offset: Long): Flow<List<PokemonEntity>> = pokemonDao.getPokemonsAsFlow(
        limit = limit,
        offset = offset
    )

    override suspend fun upsertLocalPokemons(pokemons: List<PokemonEntity>) {
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