package fr.delcey.pokedexino.domain.favorites

import fr.delcey.pokedexino.data.user.InterpolatedFavoritePokemonRepository
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.user.GetLoggedUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
                interpolatedFavoritePokemonRepository.interpolatedWithRealData(
                    realDataListFlow = userRepository.getFavoritePokemonIds(firebaseUser.uid)
                )
            }
        }.distinctUntilChanged()
}