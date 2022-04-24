package fr.delcey.pokedexino.ui.main

import android.content.Intent

sealed class MainViewAction {
    data class Toast(val message: String) : MainViewAction()
    data class NavigateForResult(val intent: Intent, val requestCode: Int) : MainViewAction()
}
