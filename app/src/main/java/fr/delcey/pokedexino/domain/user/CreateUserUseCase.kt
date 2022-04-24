package fr.delcey.pokedexino.domain.user

import com.google.firebase.auth.FirebaseUser
import fr.delcey.pokedexino.data.user.UserRepository
import fr.delcey.pokedexino.domain.image_generator.CreateImageFromInitialsUseCase
import fr.delcey.pokedexino.domain.user.entity.UserEntity
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val createImageFromInitialsUseCase: CreateImageFromInitialsUseCase,
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(user: FirebaseUser): Boolean {
        val name = user.displayName ?: return false

        userRepository.insertUser(
            UserEntity(
                id = user.uid,
                name = name,
                email = user.email ?: return false,
                photoUrl = user.photoUrl?.toString() ?: createImageFromInitialsUseCase(name),
            )
        )

        return true
    }
}