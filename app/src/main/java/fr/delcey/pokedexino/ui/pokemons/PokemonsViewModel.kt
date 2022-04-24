package fr.delcey.pokedexino.ui.pokemons

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.pokedexino.CoroutineDispatcherProvider
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.domain.favorites.GetFavoritePokemonIdsUseCase
import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.ui.utils.EquatableCallback
import fr.delcey.pokedexino.ui.utils.NavArgProducer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val context: Application,
    private val getPagedPokemonsUseCase: GetPagedPokemonsUseCase,
    private val getFavoritePokemonIdsUseCase: GetFavoritePokemonIdsUseCase,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<PokemonsViewState> = liveData(coroutineDispatcherProvider.io) {
        combine(
            getPagedPokemonsUseCase.get(),
            getFavoritePokemonIdsUseCase()
        ) { pagedPokemons, favoritePokemonIds ->
            emit(
                PokemonsViewState(
                    pagedPokemons.map { pokemonEntity ->
                        PokemonsViewState.Item.Content(
                            pokemonId = pokemonEntity.id.toString(),
                            pokemonName = pokemonEntity.name,
                            pokemonImageUrl = pokemonEntity.imageUrl,
                            starResourceDrawable = if (favoritePokemonIds.any { it == pokemonEntity.id.toString() }) {
                                R.drawable.ic_star_24
                            } else {
                                R.drawable.ic_star_outline_24
                            },
                            onCardClicked = EquatableCallback {
                                Toast.makeText(context, "OnCardClicked", Toast.LENGTH_SHORT)
                            },
                            onFavoriteButtonClicked = EquatableCallback {
                                Toast.makeText(context, "OnFavoriteButtonClicked", Toast.LENGTH_SHORT)
                            },
                        )
                    } + PokemonsViewState.Item.Loading(
                        onDisplayed = EquatableCallback {
                            viewModelScope.launch(coroutineDispatcherProvider.io) {
                                getPagedPokemonsUseCase.loadNextPage()
                            }
                        }
                    )
                )
            )
        }.collect()
    }
}