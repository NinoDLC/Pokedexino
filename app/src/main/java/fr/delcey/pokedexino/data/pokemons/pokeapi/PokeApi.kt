package fr.delcey.pokedexino.data.pokemons.pokeapi

import fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon.PokemonResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("pokemon/{pokemonId}/")
    suspend fun getPokemonById(@Path("pokemonId") pokemonId: Long): PokemonResponseDto?
}