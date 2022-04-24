package fr.delcey.pokedexino.domain.favorites

import fr.delcey.pokedexino.data.user.InterpolatedLikedPokemonRepository
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.user.GetLoggedUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetFavoritePokemonIdsUseCase @Inject constructor(
    private val getLoggedUserUseCase: GetLoggedUserUseCase,
    private val userRepository: UserRepository,
    private val interpolatedLikedPokemonRepository: InterpolatedLikedPokemonRepository,
) {
    operator fun invoke(): Flow<List<String>> = getLoggedUserUseCase()
        .flatMapLatest { firebaseUser ->
            if (firebaseUser == null) {
                flowOf(emptyList())
            } else {
                combine(
                    userRepository.getFavoritePokemonIds(firebaseUser.uid),
                    interpolatedLikedPokemonRepository.interpolatedLikedPokemonIdsFlow
                ) { pokemonIds, interpolatedLikedPokemonIds ->
                    pokemonIds.filter { // TODO NINO
                        interpolatedLikedPokemonIds[it] == false
                    }
                }
            }
        }
}