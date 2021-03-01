package net.grandcentrix.blutufintegration.data.model

import net.grandcentrix.blutuf.core.api.BlutufEvent


sealed class ProcessState<out T : Any?> {

    data class Success<out T : Any?>(val data: T) : ProcessState<T>()
    data class Scanning<out T : Any?>(val data: T? = null) : ProcessState<T>()
    data class Complete<out T : Any?>(val data: T? = null) : ProcessState<T>()
    data class Error(
        val error: BlutufEvent
    ) :
        ProcessState<Nothing>()
}