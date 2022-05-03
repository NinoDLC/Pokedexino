package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class PokemonResponse(

    @field:SerializedName("location_area_encounters")
    val locationAreaEncounters: String? = null,

    @field:SerializedName("types")
    val types: List<TypesItem>? = null,

    @field:SerializedName("base_experience")
    val baseExperience: Long? = null,

    @field:SerializedName("weight")
    val weight: Long? = null,

    @field:SerializedName("is_default")
    val isDefault: Boolean? = null,

    @field:SerializedName("sprites")
    val sprites: Sprites? = null,

    @field:SerializedName("abilities")
    val abilities: List<AbilitiesItem>? = null,

    @field:SerializedName("game_indices")
    val gameIndices: List<GameIndicesItem>? = null,

    @field:SerializedName("species")
    val species: Species? = null,

    @field:SerializedName("stats")
    val stats: List<StatsItem>? = null,

    @field:SerializedName("moves")
    val moves: List<MovesItem>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("forms")
    val forms: List<FormsItem>? = null,

    @field:SerializedName("height")
    val height: Long? = null,

    @field:SerializedName("order")
    val order: Long? = null
)