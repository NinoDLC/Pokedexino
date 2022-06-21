package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class MoveLearnMethodDto(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("url")
    val url: String? = null
)