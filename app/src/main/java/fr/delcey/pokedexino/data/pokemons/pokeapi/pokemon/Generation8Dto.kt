package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation8Dto(

    @field:SerializedName("icons")
    val icons: IconsDto? = null
)