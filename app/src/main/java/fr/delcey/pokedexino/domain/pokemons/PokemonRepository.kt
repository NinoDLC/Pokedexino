package fr.delcey.pokedexino.domain.pokemons

import fr.delcey.pokedexino.domain.utils.ApiResult
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getLocalPokemonById(pokemonId: Long): PokemonEntity?

    fun getLocalPokemonsFlow(limit: Long, offset: Long): Flow<List<PokemonEntity>>

    suspend fun upsertLocalPokemons(pokemons: List<PokemonEntity>)

    suspend fun getRemotePagedPokemons(offset: Long, limit: Long): ApiResult<List<PokemonEntity>>
}