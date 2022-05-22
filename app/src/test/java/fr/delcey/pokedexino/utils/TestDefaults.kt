package fr.delcey.pokedexino.utils

import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity
import fr.delcey.pokedexino.domain.user.entity.UserEntity

@Suppress("MemberVisibilityCanBePrivate")
object TestDefaults {

    // region UserEntity
    const val USER_ID = "USER_ID"
    const val USER_NAME = "USER_NAME"
    const val USER_EMAIL = "USER_EMAIL"
    const val USER_PHOTO_URL = "USER_PHOTO_URL"

    fun getUserEntity(
        id: String = USER_ID,
        name: String = USER_NAME,
        email: String = USER_EMAIL,
        photoUrl: String = USER_PHOTO_URL,
    ) = UserEntity(
        id = id,
        name = name,
        email = email,
        photoUrl = photoUrl,
    )
    // endregion UserEntity

    // region PokemonEntity
    const val POKEMON_ID = 48L
    const val POKEMON_NAME = "POKEMON_NAME"
    const val POKEMON_IMAGE_URL = "POKEMON_IMAGE_URL"

    fun getPokemonEntity(
        id: Long = POKEMON_ID,
        name: String = POKEMON_NAME,
        imageUrl: String = POKEMON_IMAGE_URL,
    ) = PokemonEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
    )

    fun getPokemonEntity(index: Int) = PokemonEntity(
        id = index.toLong(),
        name = "$POKEMON_NAME$index",
        imageUrl = "$POKEMON_IMAGE_URL$index",
    )
    // endregion PokemonEntity

    // region GetPagedPokemonsUseCase
    const val DEFAULT_GET_PAGED_POKEMONS_USE_CASE_POKEMON_COUNT = 20

    fun getGetPagedPokemonsUseCasePokemonListDto(
        pokemonCount: Int = DEFAULT_GET_PAGED_POKEMONS_USE_CASE_POKEMON_COUNT,
    ) = GetPagedPokemonsUseCase.PokemonListDto(
        pokemons = List(pokemonCount) { getPokemonEntity(it) },
        hasMoreData = false,
        failureState = null,
    )
    // endregion GetPagedPokemonsUseCase

}
