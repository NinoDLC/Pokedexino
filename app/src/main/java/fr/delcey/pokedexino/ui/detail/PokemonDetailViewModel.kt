package fr.delcey.pokedexino.ui.detail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.pokedexino.CoroutineDispatcherProvider
import fr.delcey.pokedexino.ui.utils.NavArgProducer
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val navArgProducer: NavArgProducer,
) : ViewModel() {
}