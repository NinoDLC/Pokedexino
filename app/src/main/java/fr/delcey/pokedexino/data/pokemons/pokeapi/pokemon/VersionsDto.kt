package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class VersionsDto(

    @field:SerializedName("generation-i")
    val generation1: Generation1Dto? = null,

    @field:SerializedName("generation-ii")
    val generation2: Generation2Dto? = null,

    @field:SerializedName("generation-iii")
    val generation3: Generation3Dto? = null,

    @field:SerializedName("generation-iv")
    val generation4: Generation4Dto? = null,

    @field:SerializedName("generation-v")
    val generation5: Generation5Dto? = null,

    @field:SerializedName("generation-vi")
    val generation6: Generation6Dto? = null,

    @field:SerializedName("generation-vii")
    val generation7: Generation7Dto? = null,

    @field:SerializedName("generation-viii")
    val generation8: Generation8Dto? = null,
)