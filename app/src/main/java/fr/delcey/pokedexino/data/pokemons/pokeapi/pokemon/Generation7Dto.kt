package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation7Dto(

    @field:SerializedName("icons")
    val icons: IconsDto? = null,

    @field:SerializedName("ultra-sun-ultra-moon")
    val ultraSunUltraMoon: UltraSunUltraMoonDto? = null
)