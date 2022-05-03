package fr.delcey.pokedexino.ui.pokemons

import androidx.annotation.DrawableRes
import fr.delcey.pokedexino.ui.utils.EquatableCallback

data class PokemonsViewState(
    val items: List<Item>,
) {
    sealed class Item(val type: Type) {
        enum class Type {
            LOADING,
            CONTENT,
        }

        data class Content(
            val pokemonId: Long,
            val pokemonName: String,
            val pokemonImageUrl: String,
            @DrawableRes
            val favoriteResourceDrawable: Int,
            val isFavoriteEnabled: Boolean,
            val onCardClicked: EquatableCallback,
            val onFavoriteButtonClicked: EquatableCallback,
        ) : Item(Type.CONTENT)

        data class Loading(
            val onDisplayed: EquatableCallback,
        ) : Item(Type.LOADING)
    }
}