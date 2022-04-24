package fr.delcey.pokedexino.ui.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.databinding.PokemonDetailFragmentBinding
import fr.delcey.pokedexino.ui.utils.viewBinding

@AndroidEntryPoint
class PokemonDetailFragment : Fragment(R.layout.pokemon_detail_fragment) {

    private val binding by viewBinding { PokemonDetailFragmentBinding.bind(it) }
    private val viewModel by viewModels<PokemonDetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}