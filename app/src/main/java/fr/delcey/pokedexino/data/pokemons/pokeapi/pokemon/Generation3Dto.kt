package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Generation3Dto(

    @field:SerializedName("firered-leafgreen")
    val fireRedLeafGreen: FireRedLeafGreenDto? = null,

    @field:SerializedName("ruby-sapphire")
    val rubySapphire: RubySapphireDto? = null,

    @field:SerializedName("emerald")
    val emerald: EmeraldDto? = null
)