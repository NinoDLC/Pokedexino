package fr.delcey.pokedexino.ui.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@SuppressLint("CheckResult")
fun ImageView.loadImageUrl(
    imageUrl: String?,
    @DrawableRes
    fallbackResId: Int? = null,
    fade: Boolean = false,
    circleCrop: Boolean = false,
) {
    if (imageUrl == null) {
        this.isVisible = false
    } else {
        this.isVisible = true
        Glide
            .with(this)
            .load(imageUrl)
            .apply {
                if (fallbackResId != null) {
                    fallback(fallbackResId)
                }
                if (fade) {
                    transition(DrawableTransitionOptions.withCrossFade())
                }
                if (circleCrop) {
                    circleCrop()
                }
            }
            .into(this)
    }
}