package fr.delcey.pokedexino.ui.pokemons

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.databinding.PokemonsFragmentBinding
import fr.delcey.pokedexino.ui.utils.viewBinding

@AndroidEntryPoint
class PokemonsFragment : Fragment(R.layout.pokemons_fragment) {

    private val binding by viewBinding { PokemonsFragmentBinding.bind(it) }
    private val viewModel by viewModels<PokemonsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PokemonsAdapter()
        binding.pokemonsRecyclerView.adapter = adapter
        binding.pokemonsRecyclerView.itemAnimator = null

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.items)
        }
    }
}