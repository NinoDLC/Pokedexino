package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class GameIndicesItem(

    @field:SerializedName("game_index")
    val gameIndex: Int? = null,

    @field:SerializedName("version")
    val version: Version? = null
)