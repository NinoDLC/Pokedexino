package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation4Dto(

    @field:SerializedName("platinum")
    val platinum: PlatinumDto? = null,

    @field:SerializedName("diamond-pearl")
    val diamondPearl: DiamondPearlDto? = null,

    @field:SerializedName("heartgold-soulsilver")
    val heartGoldSoulSilver: HeartGoldSoulSilverDto? = null
)