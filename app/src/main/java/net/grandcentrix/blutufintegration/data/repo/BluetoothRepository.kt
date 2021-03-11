package net.grandcentrix.blutufintegration.data.repo

import android.bluetooth.BluetoothAdapter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.*
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.ErrorCondition
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

    fun connectDevice(identifier: String) {
        devices[identifier]?.let {
            selectedDeviceStateFlow.value = it
            val selectedDevice = Blutuf.bleManager.getDevice(identifier)
            selectedDevice.addConnectionCallback { connectionState ->
                when (connectionState) {
                    is ConnectionState.Connected -> {
                        it.state = State.CONNECTED
                        selectedDeviceStateFlow.value = it
                    }
                    is ConnectionState.Ready -> {
                        connectionState.device.services()?.forEach { service ->
                        }
                    }
                    is ConnectionState.Disconnected -> {
                        it.state = State.DISCONNECTED
                        selectedDeviceStateFlow.value = it
                    }
                    else -> {
                    }
                }
            }
            selectedDevice.connect()
        }
    }

    fun disconnectDevice(identifier: String) {
        devices[identifier]?.let {
            val selectedDevice = Blutuf.bleManager.getDevice(identifier)
            selectedDevice.disconnect()
        }
    }

    fun checkPreconditions(): List<ErrorCondition> {
        return Blutuf.bleManager.checkPreconditions()
            .map {
                when (it) {
                    is FineLocationPermissionMissingError -> ErrorCondition.PermissionNotGuaranteedError
                    is BluetoothDisabledError -> ErrorCondition.DisabledBluetoothError
                    is GpsDisabledError -> ErrorCondition.GpsDisabledError
                    else -> ErrorCondition.UnknownError
                }
            }
    }

    fun enableBluetooth(enable: Boolean) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        when (enable) {
            true -> adapter.enable()
            false -> adapter.disable()
        }
    }
}