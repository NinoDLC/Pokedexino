package fr.delcey.pokedexino.data.pokemons.pokeapi

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

    override suspend fun getPagedPokemons(offset: Int, limit: Int): List<PokemonEntity> {
        val pagedPokemonsResponse = pokeApi.getPagedPokemons(offset, limit)

        val pokemonNamesToQuery = pagedPokemonsResponse?.results?.mapNotNull { it.name }

        if (pokemonNamesToQuery.isNullOrEmpty()) {
            return emptyList()
        }

        return supervisorScope {
            pokemonNamesToQuery.map { pokemonName ->
                async {
                    val pokemonResponse = try {
                        pokeApi.getPokemonByIdOrName(pokemonName)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }

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
            }
                .awaitAll()
                .filterNotNull()
        }
    }
}