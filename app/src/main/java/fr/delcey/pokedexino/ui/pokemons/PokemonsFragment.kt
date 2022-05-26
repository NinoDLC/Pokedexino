package fr.delcey.pokedexino.ui.pokemons

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
        val layoutManager = LinearLayoutManager(requireContext())
        binding.pokemonsRecyclerView.layoutManager = layoutManager
        binding.pokemonsRecyclerView.adapter = adapter
        binding.pokemonsRecyclerView.itemAnimator = null
        binding.pokemonsRecyclerView.addOnScrollListener(
            InfiniteScrollListener(layoutManager) {
                viewModel.onLoadMore()
            }
        )

        viewModel.viewStateLiveData.observe(viewLifecycleOwner) { state ->
            binding.pokemonsLoadingView.isVisible = state.isLoadingVisible
            binding.pokemonsRecyclerView.isVisible = state.isRecyclerViewVisible
            adapter.submitList(state.items)
        }

        viewModel.viewActionEvents.observe(viewLifecycleOwner) { action ->
            when (action) {
                is PokemonsViewAction.Toast -> Toast.makeText(requireContext(), action.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}