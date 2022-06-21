package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class AbilitiesItemDto(

    @field:SerializedName("is_hidden")
    val isHidden: Boolean? = null,

    @field:SerializedName("ability")
    val ability: AbilityDto? = null,

    @field:SerializedName("slot")
    val slot: Long? = null
)