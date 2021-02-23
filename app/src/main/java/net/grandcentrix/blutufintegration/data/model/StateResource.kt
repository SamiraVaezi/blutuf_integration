package net.grandcentrix.blutufintegration.data.model

sealed class StateResource<out T : Any?> {
    data class Connecting<out T : Any?>(val data: T? = null) : StateResource<T>()
    data class Connected<out T : Any?>(val data: T? = null) : StateResource<T>()
    data class Disconnected<out T : Any?>(val data: T? = null) : StateResource<T>()
    data class Error(
        val error: Throwable
    ) :
        StateResource<Nothing>()
}