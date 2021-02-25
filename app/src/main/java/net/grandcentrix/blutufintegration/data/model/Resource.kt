package net.grandcentrix.blutufintegration.data.model

import net.grandcentrix.blutuf.core.api.BlutufEvent


sealed class Resource<out T : Any?> {

    data class Success<out T : Any?>(val data: T) : Resource<T>()
    data class Scanning<out T : Any?>(val data: T? = null) : Resource<T>()
    data class Complete<out T : Any?>(val data: T? = null) : Resource<T>()
    data class Error(
        val error: BlutufEvent
    ) :
        Resource<Nothing>()
}