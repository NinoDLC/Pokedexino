package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class StatsItemDto(

    @field:SerializedName("stat")
    val stat: StatDto? = null,

    @field:SerializedName("base_stat")
    val baseStat: Long? = null,

    @field:SerializedName("effort")
    val effort: Long? = null
)