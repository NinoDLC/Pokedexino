package fr.delcey.pokedexino.data.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class TypesItem(

    @field:SerializedName("slot")
    val slot: Int? = null,

    @field:SerializedName("type")
    val type: Type? = null
)