package fr.delcey.pokedexino.ui.pokemons

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.domain.favorites.GetFavoritePokemonIdsUseCase
import fr.delcey.pokedexino.domain.favorites.UpdateIsPokemonFavoriteUseCase
import fr.delcey.pokedexino.domain.pokemons.GetPagedPokemonsUseCase
import fr.delcey.pokedexino.domain.pokemons.GetPagedRemotePokemonsQueryStateUseCase
import fr.delcey.pokedexino.domain.user.GetCurrentUserUseCase
import fr.delcey.pokedexino.domain.utils.CoroutineDispatcherProvider
import fr.delcey.pokedexino.ui.utils.EquatableCallback
import fr.delcey.pokedexino.ui.utils.SingleLiveEvent
import fr.delcey.pokedexino.ui.utils.capitalized
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class PokemonsViewModel @Inject constructor(
    private val context: Application,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getPagedPokemonsUseCase: GetPagedPokemonsUseCase,
    private val getFavoritePokemonIdsUseCase: GetFavoritePokemonIdsUseCase,
    private val updateIsPokemonFavoriteUseCase: UpdateIsPokemonFavoriteUseCase,
    val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    val viewStateLiveData: LiveData<PokemonsViewState> = liveData(coroutineDispatcherProvider.io) {

        emit(
            PokemonsViewState(
                items = emptyList(),
                isRecyclerViewVisible = false,
                isLoadingVisible = true
            )
        )

        combine(
            getCurrentUserUseCase(),
            getPagedPokemonsUseCase.get(),
            getFavoritePokemonIdsUseCase()
        ) { currentUser, pagedPokemons, favoritePokemonIds ->
            val items = pagedPokemons.pokemons.map { pokemonEntity ->
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
                                pokemonId = pokemonEntity.id,
                                isFavorite = !isFavorite
                            )
                        }
                    },
                )
            }

            if (items.isNotEmpty()) {
                emit(
                    PokemonsViewState(
                        items = if (pagedPokemons.hasMoreData) {
                            items + PokemonsViewState.Item.Loading
                        } else {
                            items
                        },
                        isRecyclerViewVisible = true,
                        isLoadingVisible = false,
                    )
                )
            }

            if (pagedPokemons.failureState != null) {
                withContext(coroutineDispatcherProvider.main) {
                    viewActionEvents.value = PokemonsViewAction.Toast(
                        message = context.getString(
                            when (pagedPokemons.failureState) {
                                GetPagedRemotePokemonsQueryStateUseCase.FailureState.TOO_MANY_ATTEMPTS -> R.string.pokemons_query_error_io
                                GetPagedRemotePokemonsQueryStateUseCase.FailureState.CRITICAL -> R.string.pokemons_query_error_critical
                            }
                        )
                    )
                }
            }
        }.collect()
    }

    val viewActionEvents = SingleLiveEvent<PokemonsViewAction>()

    private val isLoadingNextPage = AtomicBoolean(false)

    init {
        loadNextPage()
    }

    fun onLoadMore() {
        loadNextPage()
    }

    private fun loadNextPage() {
        if (isLoadingNextPage.compareAndSet(false, true)) {
            viewModelScope.launch(coroutineDispatcherProvider.io) {
                getPagedPokemonsUseCase.loadNextPage()
                isLoadingNextPage.set(false)
            }
        }
    }
}