package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemonlist

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(

    @field:SerializedName("next")
    val next: String? = null,

    @field:SerializedName("previous")
    val previous: String? = null,

    @field:SerializedName("count")
    val count: Long? = null,

    @field:SerializedName("results")
    val results: List<PokemonListResponseItem>? = null
)