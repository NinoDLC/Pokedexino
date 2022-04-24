package fr.delcey.pokedexino.domain.image_generator

import javax.inject.Inject

class CreateImageFromInitialsUseCase @Inject constructor() {

    operator fun invoke(name: String): String {
        return "TODO" // TODO NINO PhotoUrl might be null : generate a local png
    }
}