package fr.delcey.pokedexino.domain.repository

import kotlinx.coroutines.flow.Flow

interface ConnectivityRepository {
    fun isInternetAvailableFlow(): Flow<Boolean>
}