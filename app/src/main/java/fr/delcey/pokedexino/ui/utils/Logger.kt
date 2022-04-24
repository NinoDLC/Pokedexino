@file:Suppress("SpellCheckingInspection", "unused")

package fr.delcey.pokedexino.ui.utils

import android.os.Build
import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

private val LOG_TAG_MAX_LENGTH = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) 23 else 100

fun logv(msg: String) = log(Log.VERBOSE, null, msg, null)
fun logv(msg: String, throwable: Throwable) = log(Log.VERBOSE, null, msg, throwable)
fun logv(throwable: Throwable) = log(Log.VERBOSE, null, null, throwable)
fun logv(tag: String, msg: String) = log(Log.VERBOSE, tag, msg, null)
fun logv(tag: String, msg: String, throwable: Throwable) = log(Log.VERBOSE, tag, msg, throwable)

fun logd(msg: String) = log(Log.DEBUG, null, msg, null)
fun logd(msg: String, throwable: Throwable) = log(Log.DEBUG, null, msg, throwable)
fun logd(throwable: Throwable) = log(Log.DEBUG, null, null, throwable)
fun logd(tag: String, msg: String) = log(Log.DEBUG, tag, msg, null)
fun logd(tag: String, msg: String, throwable: Throwable) = log(Log.DEBUG, tag, msg, throwable)

fun logi(msg: String) = log(Log.INFO, null, msg, null)
fun logi(msg: String, throwable: Throwable) = log(Log.INFO, null, msg, throwable)
fun logi(throwable: Throwable) = log(Log.INFO, null, null, throwable)
fun logi(tag: String, msg: String) = log(Log.INFO, tag, msg, null)
fun logi(tag: String, msg: String, throwable: Throwable) = log(Log.INFO, tag, msg, throwable)

fun logw(msg: String) = log(Log.WARN, null, msg, null)
fun logw(msg: String, throwable: Throwable) = log(Log.WARN, null, msg, throwable)
fun logw(throwable: Throwable) = log(Log.WARN, null, null, throwable)
fun logw(tag: String, msg: String) = log(Log.WARN, tag, msg, null)
fun logw(tag: String, msg: String, throwable: Throwable) = log(Log.WARN, tag, msg, throwable)

fun loge(msg: String) = log(Log.ERROR, null, msg, null)
fun loge(msg: String, throwable: Throwable) = log(Log.ERROR, null, msg, throwable)
fun loge(throwable: Throwable) = log(Log.ERROR, null, null, throwable)
fun loge(tag: String, msg: String) = log(Log.ERROR, tag, msg, null)
fun loge(tag: String, msg: String, throwable: Throwable) = log(Log.ERROR, tag, msg, throwable)

private fun log(level: Int, tag: String?, msg: String?, tr: Throwable?) {
    val tagNonNull = tag ?: makeLogTag()
    when (level) {
        Log.VERBOSE -> Log.v(tagNonNull, msg, tr)
        Log.DEBUG -> Log.d(tagNonNull, msg, tr)
        Log.INFO -> Log.i(tagNonNull, msg, tr)
        Log.WARN -> Log.w(tagNonNull, msg, tr)
        Log.ERROR -> {
            Log.e(tagNonNull, msg.orEmpty(), tr)
            if (tr != null) {
                Firebase.crashlytics.recordException(tr)
            }
        }
    }
}

private fun makeLogTag() = Thread.currentThread().stackTrace
    .firstOrNull { it.fileName != "VMStack.java" && it.fileName != "Thread.java" && it.fileName != "Logger.kt" }
    ?.let { stack ->
        stack.className
            .split(".")
            .last()
            .split("\$Companion")
            .first()
            .split("$")
            .filter { it.toIntOrNull() == null }
            .joinToString(
                separator = "::",
                postfix = stack.methodName.takeIf { it != "invoke" }?.let { "::$it" }.orEmpty()
            )
    }
    ?.take(LOG_TAG_MAX_LENGTH)
    ?: "Nino"