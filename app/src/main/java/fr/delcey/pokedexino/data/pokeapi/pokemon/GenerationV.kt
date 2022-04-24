package fr.delcey.pokedexino.data.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class GenerationV(

    @field:SerializedName("black-white")
    val blackWhite: BlackWhite? = null
)