package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class MovesItemDto(

    @field:SerializedName("version_group_details")
    val versionGroupDetails: List<VersionGroupDetailsItemDto>? = null,

    @field:SerializedName("move")
    val move: MoveDto? = null
)