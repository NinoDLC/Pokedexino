package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class EmeraldDto(

    @field:SerializedName("front_default")
    val frontDefault: String? = null,

    @field:SerializedName("front_shiny")
    val frontShiny: String? = null
)