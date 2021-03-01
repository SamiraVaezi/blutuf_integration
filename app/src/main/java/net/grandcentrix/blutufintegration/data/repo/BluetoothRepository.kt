package net.grandcentrix.blutufintegration.data.repo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.ConnectionState
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.ProcessState
import net.grandcentrix.blutufintegration.data.model.State

private const val SCAN_TIMEOUT: Long = 5000

object BluetoothRepository {

    var isScanning = false
    private val devices = mutableMapOf<String, DeviceUiState>()

    val selectedDeviceStateFlow = MutableStateFlow<DeviceUiState?>(null)
    val devicesStateFlow = MutableSharedFlow<ProcessState<List<DeviceUiState>>>()

    @ExperimentalCoroutinesApi
    suspend fun scan() {
        devices.clear()
        if (!isScanning) {
            isScanning = true

            coroutineScope {
                Blutuf.bleManager.startScan(
                    onDeviceAppeared = { device ->
                        devices[device.identifier] = device.toDeviceState()
                        launch {
                            devicesStateFlow.emit(ProcessState.Success(devices.values.toList()))
                        }
                    },
                    onScanError = {
                        devices.clear()
                        //todo
//                    devicesStateFlow.emit(ProcessState.Error())
                    },
                    onDeviceDisappeared = { device ->
                        devices.remove(device.identifier)
                        launch {
                            devicesStateFlow.emit(ProcessState.Success(devices.values.toList()))
                        }
                    },
                    onDeviceUpdated = { device ->
                        /*devices.put(device.identifier, device.toDeviceState())
                        offer(ProcessState.Success(devices.values.toList()))*/
                    }
                )
            }
        }
    }

    fun stopScan() {
        isScanning = false
        Blutuf.bleManager.stopScan()
    }

    private fun Device.toDeviceState() = DeviceUiState(this, State.DISCONNECTED)

    fun getDevice(identifier: String) = devices[identifier]

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
                    connectionState.device.services()?.forEach { service ->
                    }
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
        try {
            val selectedDevice = Blutuf.bleManager.getDevice(deviceUiState.device.identifier)
            selectedDevice.disconnect()
        } catch (e: Exception) {
            Log.e("sami", e.message.toString())
        }
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