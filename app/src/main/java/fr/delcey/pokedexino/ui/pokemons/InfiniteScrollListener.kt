package fr.delcey.pokedexino.ui.pokemons

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(
    private val layoutManager: LinearLayoutManager,
    private val onLoadMore: () -> Unit,
) : RecyclerView.OnScrollListener() {

    companion object {
        // The minimum number of items remaining before we should loading more.
        private const val VISIBLE_THRESHOLD = 5
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy < 0) {
            return
        }

        val totalItemCount = layoutManager.itemCount
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        if (lastVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            onLoadMore()
        }
    }
}