package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class OfficialArtworkDto(

    @field:SerializedName("front_default")
    val frontDefault: String? = null
)