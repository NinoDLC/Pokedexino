package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class GenerationV(

    @field:SerializedName("black-white")
    val blackWhite: BlackWhite? = null
)