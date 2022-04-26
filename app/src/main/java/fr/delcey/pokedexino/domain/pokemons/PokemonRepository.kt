package fr.delcey.pokedexino.domain.pokemons

import fr.delcey.pokedexino.domain.ApiResult

interface PokemonRepository {
    suspend fun getPokemonByIdOrName(pokemonId: String): PokemonEntity?

    suspend fun getPagedPokemons(offset: Int, limit: Int): ApiResult<List<PokemonEntity>>
}