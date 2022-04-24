package fr.delcey.pokedexino.data.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class Sprites(

    @field:SerializedName("back_shiny_female")
    val backShinyFemale: Any? = null,

    @field:SerializedName("back_female")
    val backFemale: Any? = null,

    @field:SerializedName("other")
    val other: Other? = null,

    @field:SerializedName("back_default")
    val backDefault: String? = null,

    @field:SerializedName("front_shiny_female")
    val frontShinyFemale: Any? = null,

    @field:SerializedName("front_default")
    val frontDefault: String? = null,

    @field:SerializedName("versions")
    val versions: Versions? = null,

    @field:SerializedName("front_female")
    val frontFemale: Any? = null,

    @field:SerializedName("back_shiny")
    val backShiny: String? = null,

    @field:SerializedName("front_shiny")
    val frontShiny: String? = null
)