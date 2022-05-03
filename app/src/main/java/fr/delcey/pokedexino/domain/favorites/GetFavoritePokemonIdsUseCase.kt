package fr.delcey.pokedexino.domain.favorites

import fr.delcey.pokedexino.data.user.InterpolatedFavoritePokemonRepository
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.user.GetLoggedUserUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetFavoritePokemonIdsUseCase @Inject constructor(
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val userRepository: UserRepository,
    private val interpolatedFavoritePokemonRepository: InterpolatedFavoritePokemonRepository,
) {
    operator fun invoke(): Flow<Collection<Long>> = getLoggedUserUseCase()
        .flatMapLatest { firebaseUser ->
            if (firebaseUser == null) {
                flowOf(emptyList())
            } else {
                combine(
                    userRepository.getFavoritePokemonIds(firebaseUser.uid),
                    interpolatedFavoritePokemonRepository.interpolatedFavoritePokemonIdsFlow
                ) { pokemonIds, interpolatedLikedPokemonIds ->
                    pokemonIds.filter {
                        // Keep null (not interpolated) or true (interpolated to favorite)
                        interpolatedLikedPokemonIds[it] != false
                    }.plus(
                        // Add interpolated (not present yet in backend)
                        interpolatedLikedPokemonIds.filter {
                            it.value
                        }.map {
                            it.key
                        }
                    ).toSet() // To avoid duplicates
                }
            }
        }.distinctUntilChanged()
}