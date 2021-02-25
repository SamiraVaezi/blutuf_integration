package net.grandcentrix.blutufintegration.data.repo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.ConnectionState
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutuf.core.api.SelectedDevice
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.model.State

private const val SCAN_TIMEOUT: Long = 10000

object BluetoothRepository {

    var isScanning = false
    private val devices = mutableListOf<DeviceUiState>()

    val selectedDeviceStateFlow = MutableStateFlow<DeviceUiState?>(null)

    @ExperimentalCoroutinesApi
    fun startScan(): Flow<List<DeviceUiState>> = channelFlow {
        devices.clear()
        if (!isScanning) {
            isScanning = true
            Blutuf.bleManager.startScan(
                onDeviceAppeared = { device ->
                    devices.takeUnless {

                    }
                    devices.add(device.toDeviceState())
                    offer(devices)
                },
                onScanError = {
                    close()
                },
                onDeviceDisappeared = {
                },
                onDeviceUpdated = {
                }
            )
            Handler(Looper.getMainLooper()).postDelayed({ close() }, SCAN_TIMEOUT)
            awaitClose {
                stopScan()
                cancel()
            }
        }
    }

    fun stopScan() {
        isScanning = false
        Blutuf.bleManager.stopScan()
    }

    private fun Device.toDeviceState() = DeviceUiState(this, State.DISCONNECTED)

    fun getDevice(identifier: String) =
        devices.find { deviceUiState -> deviceUiState.device.identifier == identifier }


    fun connectDevice(deviceUiState: DeviceUiState) {
        deviceUiState.state = State.CONNECTING
        selectedDeviceStateFlow.value = deviceUiState
        val selectedDevice = Blutuf.bleManager.getDevice(deviceUiState.device.identifier)
        selectedDevice.addConnectionCallback { connectionState ->
            when (connectionState) {
                is ConnectionState.Connected -> {
                    deviceUiState.state = State.CONNECTED
                    selectedDeviceStateFlow.value = deviceUiState
                }
                is ConnectionState.Ready -> {
                }
                is ConnectionState.Disconnected -> {
                    deviceUiState.state = State.DISCONNECTED
                    selectedDeviceStateFlow.value = deviceUiState
                }
                else -> {
                }
            }
        }
        selectedDevice.connect()
    }

    fun disconnectDevice(deviceUiState: DeviceUiState) {
        val selectedDevice = Blutuf.bleManager.getDevice(deviceUiState.device.identifier)
        selectedDevice.disconnect()
    }

    fun deviceSupportsBluetooth(context: Context): Boolean {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        return bluetoothManager?.adapter != null
    }

    fun enableBluetooth(enable: Boolean) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        when (enable) {
            true -> adapter.enable()
            false -> adapter.disable()
        }
    }
}