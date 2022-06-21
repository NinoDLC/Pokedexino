package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class OtherDto(

    @field:SerializedName("dream_world")
    val dreamWorld: DreamWorldDto? = null,

    @field:SerializedName("official-artwork")
    val officialArtwork: OfficialArtworkDto? = null,

    @field:SerializedName("home")
    val home: HomeDto? = null
)