package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class TypesItem(

    @field:SerializedName("slot")
    val slot: Long? = null,

    @field:SerializedName("type")
    val type: Type? = null
)