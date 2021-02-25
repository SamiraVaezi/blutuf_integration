package net.grandcentrix.blutufintegration.data.model

import android.content.Context
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.R

data class DeviceUiState(
    val device: Device,
    var state: State
) {

    fun getStateTitle(context: Context) = when (state) {
        State.CONNECTED -> context.getString(
            R.string.connected_to, device.name ?: device.identifier
        )
        State.CONNECTING -> context.getString(
            R.string.connecting_to, device.name ?: device.identifier
        )
        else -> context.getString(R.string.disconnected)
    }
}

enum class State(val title: String, val backColor: Int, val textColor: Int) {
    CONNECTED(title = "Connect", R.color.light_gray, R.color.colorPrimaryDark),
    DISCONNECTED(title = "Disconnected", R.color.colorPrimary, R.color.white),
    CONNECTING(title = "Connecting", R.color.light_gray, R.color.colorPrimaryDark)
}