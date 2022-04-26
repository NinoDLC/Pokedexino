package fr.delcey.pokedexino.data.pokemons.pokeapi

import fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon.PokemonResponse
import fr.delcey.pokedexino.data.pokemons.pokeapi.pokemonlist.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon/{pokemonId}/")
    suspend fun getPokemonByIdOrName(@Path("pokemonId") pokemonId: String): PokemonResponse?

    @GET("pokemon")
    suspend fun getPagedPokemons(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): PokemonListResponse?
}