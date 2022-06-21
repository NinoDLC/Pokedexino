package fr.delcey.pokedexino.test_utils.defaults

import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.domain.pokemons.PokemonEntity
import fr.delcey.pokedexino.domain.user.entity.UserEntity

// region UserEntity
fun getUserEntity(index: Int = 0) = UserEntity(
    id = getUserId(index),
    name = getUserName(index),
    email = getUserEmail(index),
    photoUrl = getUserPhotoUrl(index),
)

fun getUserId(index: Int) = "USER_ID$index"
fun getUserName(index: Int) = "USER_NAME$index"
fun getUserEmail(index: Int) = "USER_EMAIL$index"
fun getUserPhotoUrl(index: Int) = "USER_PHOTO_URL$index"
// endregion UserEntity

// region PokemonEntity
fun getPokemonEntity(index: Int = 0) = PokemonEntity(
    id = getPokemonId(index),
    name = getPokemonName(index),
    imageUrl = getPokemonImageUrl(index),
)

fun getPokemonId(index: Int) = index.toLong()
fun getPokemonName(index: Int) = "POKEMON_NAME$index"
fun getPokemonImageUrl(index: Int) = "POKEMON_IMAGE_URL$index"
// endregion PokemonEntity

// region GetPagedPokemonsUseCase
const val DEFAULT_GET_PAGED_POKEMONS_USE_CASE_POKEMON_COUNT = 20

fun getGetPagedPokemonsUseCasePokemonListDto(
    pokemonCount: Int = DEFAULT_GET_PAGED_POKEMONS_USE_CASE_POKEMON_COUNT,
) = GetPagedPokemonsUseCase.PokemonListDto(
    pokemons = List(pokemonCount) { index -> getPokemonEntity(index) },
    hasMoreData = true,
    failureState = null,
)
// endregion GetPagedPokemonsUseCase
