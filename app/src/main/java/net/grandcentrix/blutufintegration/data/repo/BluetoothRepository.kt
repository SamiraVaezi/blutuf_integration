package net.grandcentrix.blutufintegration.data.repo

import android.bluetooth.BluetoothAdapter
import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.ConnectionState
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutuf.core.api.MissingPrecondition
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.ProcessState
import net.grandcentrix.blutufintegration.data.model.State

class BluetoothRepository {

    private val devices = mutableMapOf<String, DeviceUiState>()

    val selectedDeviceStateFlow = MutableStateFlow<DeviceUiState?>(null)
    val devicesStateFlow = MutableSharedFlow<ProcessState<List<DeviceUiState>>>()

    private var scanCounter = 0

    suspend fun scan() {
        scanCounter++
        if (scanCounter == 1) {
            devices.clear()
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
        scanCounter--
        if (scanCounter == 0) {
            Blutuf.bleManager.stopScan()
        }
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

    fun checkPreconditions(): List<MissingPrecondition> {
        return Blutuf.bleManager.checkPreconditions()
    }

    fun enableBluetooth(enable: Boolean) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        when (enable) {
            true -> adapter.enable()
            false -> adapter.disable()
        }
    }
}