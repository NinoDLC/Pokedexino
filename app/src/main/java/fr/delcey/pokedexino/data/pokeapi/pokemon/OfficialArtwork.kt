package fr.delcey.pokedexino.data.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class OfficialArtwork(

    @field:SerializedName("front_default")
    val frontDefault: String? = null
)