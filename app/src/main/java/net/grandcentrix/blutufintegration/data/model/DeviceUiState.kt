package net.grandcentrix.blutufintegration.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import net.grandcentrix.blutuf.core.api.Bonding
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.R

@Parcelize
data class DeviceUiState(
    val device: @RawValue Device,
    var state: @RawValue State,
    var bonding: @RawValue Bonding.State?
) : Parcelable {

}

enum class State(val title: String, val backColor: Int, val textColor: Int) {
    CONNECTED(title = "Connect", R.color.light_gray, R.color.colorPrimaryDark),
    DISCONNECTED(title = "Disconnected", R.color.colorPrimary, R.color.white),
    CONNECTING(title = "Connecting", R.color.light_gray, R.color.colorPrimaryDark)
}