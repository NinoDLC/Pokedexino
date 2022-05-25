package fr.delcey.pokedexino.ui.pokemons

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.delcey.pokedexino.databinding.PokemonsItemBinding
import fr.delcey.pokedexino.databinding.PokemonsItemLoadingBinding
import fr.delcey.pokedexino.ui.utils.exhaustive

class PokemonsAdapter : ListAdapter<PokemonsViewState.Item, RecyclerView.ViewHolder>(PokemonDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (PokemonsViewState.Item.Type.values()[viewType]) {
        PokemonsViewState.Item.Type.CONTENT -> PokemonViewContentHolder.newInstance(parent)
        PokemonsViewState.Item.Type.LOADING -> PokemonViewLoadingHolder.newInstance(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is PokemonsViewState.Item.Content -> (holder as PokemonViewContentHolder).bind(item)
            is PokemonsViewState.Item.Loading -> Unit // Just display the loading
        }.exhaustive
    }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    class PokemonViewContentHolder(private val binding: PokemonsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun newInstance(parent: ViewGroup) = PokemonViewContentHolder(
                PokemonsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(viewState: PokemonsViewState.Item.Content) {
            binding.pokemonsItemCardView.setOnClickListener {
                viewState.onCardClicked()
            }

            Glide.with(binding.pokemonsItemImageViewAvatar)
                .load(viewState.pokemonImageUrl)
                .fitCenter()
                .into(binding.pokemonsItemImageViewAvatar)

            binding.pokemonsItemTextViewName.text = viewState.pokemonName

            binding.pokemonsItemImageViewFavorite.setImageResource(viewState.favoriteResourceDrawable)
            binding.pokemonsItemImageViewFavorite.isEnabled = viewState.isFavoriteEnabled
            binding.pokemonsItemImageViewFavorite.setOnClickListener {
                viewState.onFavoriteButtonClicked()
            }
        }
    }

    object PokemonViewLoadingHolder {
        fun newInstance(parent: ViewGroup) = object : RecyclerView.ViewHolder(
            PokemonsItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
        ) {}
    }

    object PokemonDiffUtil : DiffUtil.ItemCallback<PokemonsViewState.Item>() {
        override fun areItemsTheSame(oldItem: PokemonsViewState.Item, newItem: PokemonsViewState.Item) =
            oldItem is PokemonsViewState.Item.Loading && newItem is PokemonsViewState.Item.Loading
                    || oldItem is PokemonsViewState.Item.Content && newItem is PokemonsViewState.Item.Content
                    && oldItem.pokemonId == newItem.pokemonId

        override fun areContentsTheSame(oldItem: PokemonsViewState.Item, newItem: PokemonsViewState.Item) =
            oldItem == newItem
    }
}

