@file:Suppress("unused")

package fr.delcey.pokedexino.ui.utils

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible

fun TextView.setTextElseGone(text: CharSequence?) {
    this.text = text
    this.isVisible = text != null
}

fun TextView.setTextElseGone(@StringRes stringRes: Int?) {
    if (stringRes == null) {
        this.isVisible = false
    } else {
        this.isVisible = true
        setText(stringRes)
    }
}