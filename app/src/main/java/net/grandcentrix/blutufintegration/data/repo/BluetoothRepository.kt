package net.grandcentrix.blutufintegration.data.repo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

object BluetoothRepository {

    fun deviceSupportsBluetooth(context: Context): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        return bluetoothManager?.adapter != null
    }

    fun enableBluetooth(enable: Boolean) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        when(enable) {
            true -> adapter.enable()
            false -> adapter.disable()
        }
    }
}