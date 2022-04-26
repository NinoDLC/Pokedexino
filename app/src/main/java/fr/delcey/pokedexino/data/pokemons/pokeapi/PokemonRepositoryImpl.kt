package fr.delcey.pokedexino.data.pokemons.pokeapi

import fr.delcey.pokedexino.domain.ApiResult
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity
import fr.delcey.pokedexino.domain.pokemons.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

// TODO NINO Binding et implem à finir, awaitAll à tester avec une exception
class PokemonRepositoryImpl @Inject constructor(
    private val pokeApi: PokeApi
) : PokemonRepository {
    override suspend fun getPokemonByIdOrName(pokemonId: String): PokemonEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun getPagedPokemons(offset: Int, limit: Int): ApiResult<List<PokemonEntity>> {
        val pagedPokemonsResponse = try {
            pokeApi.getPagedPokemons(offset, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            return ApiResult.Failure.IoException(e) // TODO NINO IO or SERVER ?
        }

        val pokemonNamesToQuery = pagedPokemonsResponse.results?.mapNotNull { it.name }

        if (pokemonNamesToQuery.isNullOrEmpty()) {
            return if (pagedPokemonsResponse.results?.isEmpty() == true) {
                ApiResult.Empty
            } else {
                ApiResult.Failure.ApiException(message = "List is not empty but all contained names are!")
            }
        }

        return try {
            val pokemons = supervisorScope {
                pokemonNamesToQuery.map { pokemonName ->
                    async {
                        val pokemonResponse = pokeApi.getPokemonByIdOrName(pokemonName)

                        if (pokemonResponse?.id != null && pokemonResponse.name != null && pokemonResponse.sprites?.frontDefault != null) {
                            PokemonEntity(
                                id = pokemonResponse.id.toString(),
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