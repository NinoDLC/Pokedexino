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
import fr.delcey.pokedexino.domain.favorites.UpdateIsPokemonFavoriteUseCase
import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.domain.user.GetCurrentUserUseCase
import fr.delcey.pokedexino.ui.utils.EquatableCallback
import fr.delcey.pokedexino.ui.utils.capitalized
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val context: Application,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPagedPokemonsUseCase: GetPagedPokemonsUseCase,
    private val getFavoritePokemonIdsUseCase: GetFavoritePokemonIdsUseCase,
    private val updateIsPokemonFavoriteUseCase: UpdateIsPokemonFavoriteUseCase,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<PokemonsViewState> = liveData(coroutineDispatcherProvider.io) {
        combine(
            getCurrentUserUseCase(),
            getPagedPokemonsUseCase.get(),
            getFavoritePokemonIdsUseCase()
        ) { currentUser, pagedPokemons, favoritePokemonIds ->
            val items = pagedPokemons.map { pokemonEntity ->
                val isFavorite = favoritePokemonIds.any { it == pokemonEntity.id }

                PokemonsViewState.Item.Content(
                    pokemonId = pokemonEntity.id,
                    pokemonName = pokemonEntity.name.capitalized(),
                    pokemonImageUrl = pokemonEntity.imageUrl,
                    favoriteResourceDrawable = if (isFavorite) {
                        R.drawable.ic_star_24
                    } else {
                        R.drawable.ic_star_outline_24
                    },
                    isFavoriteEnabled = currentUser != null,
                    onCardClicked = EquatableCallback {
                        Toast.makeText(context, "OnCardClicked", Toast.LENGTH_SHORT)
                    },
                    onFavoriteButtonClicked = EquatableCallback {
                        viewModelScope.launch(coroutineDispatcherProvider.io) {
                            updateIsPokemonFavoriteUseCase(
                                pokemonEntity.id,
                                !isFavorite
                            )
                        }
                    },
                )
            }

            if (items.isNotEmpty()) {
                emit(
                    PokemonsViewState(
                        items + PokemonsViewState.Item.Loading(
                            onDisplayed = EquatableCallback {
                                viewModelScope.launch(coroutineDispatcherProvider.io) {
                                    getPagedPokemonsUseCase.loadNextPage()
                                }
                            }
                        )
                    )
                )
            }
        }.collect()
    }

    init {
        viewModelScope.launch(coroutineDispatcherProvider.io) {
            getPagedPokemonsUseCase.loadNextPage()
        }
    }
}