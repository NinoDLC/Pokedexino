package fr.delcey.pokedexino.data.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class GenerationIi(

    @field:SerializedName("gold")
    val gold: Gold? = null,

    @field:SerializedName("crystal")
    val crystal: Crystal? = null,

    @field:SerializedName("silver")
    val silver: Silver? = null
)