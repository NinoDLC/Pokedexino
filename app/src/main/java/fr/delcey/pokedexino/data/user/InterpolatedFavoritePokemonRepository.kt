package fr.delcey.pokedexino.data.user

import fr.delcey.pokedexino.data.utils.InterpolationRepository
import fr.delcey.pokedexino.domain.GlobalCoroutineScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterpolatedFavoritePokemonRepository @Inject constructor(
    @GlobalCoroutineScope private val globalScope: CoroutineScope,
) : InterpolationRepository<Long>(globalScope = globalScope)