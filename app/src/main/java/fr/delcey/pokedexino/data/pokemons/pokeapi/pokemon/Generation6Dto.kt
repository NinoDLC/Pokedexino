package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation6Dto(

    @field:SerializedName("omegaruby-alphasapphire")
    val omegaRubyAlphaSapphire: OmegaRubyAlphaSapphireDto? = null,

    @field:SerializedName("x-y")
    val xY: XYDto? = null
)