package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation1Dto(

    @field:SerializedName("yellow")
    val yellow: YellowDto? = null,

    @field:SerializedName("red-blue")
    val redBlue: RedBlueDto? = null
)