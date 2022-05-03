package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class AbilitiesItem(

    @field:SerializedName("is_hidden")
    val isHidden: Boolean? = null,

    @field:SerializedName("ability")
    val ability: Ability? = null,

    @field:SerializedName("slot")
    val slot: Long? = null
)