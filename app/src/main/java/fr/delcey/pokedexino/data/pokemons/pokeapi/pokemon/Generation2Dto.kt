package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation2Dto(

    @field:SerializedName("gold")
    val gold: GoldDto? = null,

    @field:SerializedName("crystal")
    val crystal: CrystalDto? = null,

    @field:SerializedName("silver")
    val silver: SilverDto? = null
)