package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class VersionGroupDetailsItemDto(

    @field:SerializedName("level_learned_at")
    val levelLearnedAt: Long? = null,

    @field:SerializedName("version_group")
    val versionGroup: VersionGroupDto? = null,

    @field:SerializedName("move_learn_method")
    val moveLearnMethod: MoveLearnMethodDto? = null
)