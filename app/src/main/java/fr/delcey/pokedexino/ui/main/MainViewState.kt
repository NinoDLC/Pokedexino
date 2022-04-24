package fr.delcey.pokedexino.ui.main

data class MainViewState(
    val animateHeaderChange: Boolean,
    val isLoginButtonVisible: Boolean,
    val isLogoutButtonVisible: Boolean,
    val isLoadingVisible: Boolean,
    val avatarUrl: String?,
    val userName: String?,
    val userEmail: String?,
)
