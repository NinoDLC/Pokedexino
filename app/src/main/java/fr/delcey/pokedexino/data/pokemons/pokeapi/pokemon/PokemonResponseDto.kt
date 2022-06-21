package fr.delcey.pokedexino.data.pokemons.pokeapi.pokemon

import com.google.gson.annotations.SerializedName

data class PokemonResponseDto(

    @field:SerializedName("location_area_encounters")
    val locationAreaEncounters: String? = null,

    @field:SerializedName("types")
    val types: List<TypesItemDto>? = null,

    @field:SerializedName("base_experience")
    val baseExperience: Long? = null,

    @field:SerializedName("weight")
    val weight: Long? = null,

    @field:SerializedName("is_default")
    val isDefault: Boolean? = null,

    @field:SerializedName("sprites")
    val sprites: SpritesDto? = null,

    @field:SerializedName("abilities")
    val abilities: List<AbilitiesItemDto>? = null,

    @field:SerializedName("game_indices")
    val gameIndices: List<GameIndicesItemDto>? = null,

    @field:SerializedName("species")
    val species: SpeciesDto? = null,

    @field:SerializedName("stats")
    val stats: List<StatsItemDto>? = null,

    @field:SerializedName("moves")
    val moves: List<MovesItemDto>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("id")
    val id: Long? = null,

    @field:SerializedName("forms")
    val forms: List<FormsItemDto>? = null,

    @field:SerializedName("height")
    val height: Long? = null,

    @field:SerializedName("order")
    val order: Long? = null
)