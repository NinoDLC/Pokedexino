package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class GameIndicesItemDto(

    @field:SerializedName("game_index")
    val gameIndex: Long? = null,

    @field:SerializedName("version")
    val version: VersionDto? = null
)