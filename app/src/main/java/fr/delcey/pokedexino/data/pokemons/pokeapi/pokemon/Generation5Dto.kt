package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation5Dto(

    @field:SerializedName("black-white")
    val blackWhite: BlackWhiteDto? = null
)