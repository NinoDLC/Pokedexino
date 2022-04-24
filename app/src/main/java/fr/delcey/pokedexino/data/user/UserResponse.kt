package fr.delcey.pokedexino.data.user

sealed class UserResponse {

    abstract val name: String?
    abstract val email: String?
    abstract val photoUrl: String?

    data class Up(
        override val name: String,
        override val email: String,
        override val photoUrl: String,
    ) : UserResponse()

    data class Down(
        val id: String? = null,
        override val name: String? = null,
        override val email: String? = null,
        override val photoUrl: String? = null,
    ) : UserResponse()
}