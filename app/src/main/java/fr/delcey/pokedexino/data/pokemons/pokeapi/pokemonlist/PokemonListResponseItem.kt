package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemonlist

import com.google.gson.annotations.SerializedName

data class PokemonListResponseItem(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("url")
    val url: String? = null
)