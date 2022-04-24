package fr.delcey.pokedexino.data.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Emerald(

    @field:SerializedName("front_default")
    val frontDefault: String? = null,

    @field:SerializedName("front_shiny")
    val frontShiny: String? = null
)