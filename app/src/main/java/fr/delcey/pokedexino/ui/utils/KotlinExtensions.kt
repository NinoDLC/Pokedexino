package fr.delcey.pokedexino.ui.utils

import java.util.*

@Suppress("unused") // Receiver reference forces when into expression form.
// See https://github.com/cashapp/exhaustive#library-trailing-property
inline val Any?.exhaustive
    get() = Unit

// Better to annoy thousand of people instead of making a breaking change during a major version change, right ?
fun String.capitalized() = replaceFirstChar {
    if (it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
    } else {
        it.toString()
    }
}