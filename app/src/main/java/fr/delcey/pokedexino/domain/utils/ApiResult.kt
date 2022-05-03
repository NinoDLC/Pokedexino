package fr.delcey.pokedexino.domain.utils

sealed class ApiResult<out D : Any> {
    data class Success<out D : Any>(val data: D) : ApiResult<D>()
    object Empty : ApiResult<Nothing>()

    sealed class Failure : ApiResult<Nothing>() {
        abstract val exception: Exception?
        abstract val message: String?

        data class IoException(
            override val exception: Exception? = null,
            override val message: String? = null,
        ) : Failure()

        data class ApiException(
            override val exception: Exception? = null,
            override val message: String? = null,
        ) : Failure()
    }
}