package fr.delcey.pokedexino.data.utils

import android.os.Build
import javax.inject.Singleton

@Singleton
class ApiLevelHelper {

    fun isAndroidApiLevelAtLeast(versionCode: Int): Boolean = Build.VERSION.SDK_INT >= versionCode

}
