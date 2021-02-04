package net.grandcentrix.blutufintegration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.*
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository
import java.util.*
import kotlin.concurrent.schedule

private const val SCAN_TIMEOUT: Long = 30000

class MainViewModel : ViewModel() {

    val uiModel = MutableLiveData<Resource<List<DeviceUiState>>>()
    private var devices = mutableListOf<DeviceUiState>()

    var bleStateFlow = MutableStateFlow(false)

    init {
        Blutuf.bleManager.registerEventListener(this::onEvent)
    }

    override fun onCleared() {
        Blutuf.bleManager.unregisterEventListener(this::onEvent)
    }

    private fun onEvent(event: BlutufEvent, result: BlutufEventResult) {
        when (event) {
            BlutufEvent.BLUETOOTH_STATUS_CHANGE -> {
                bleStateFlow.value = result == BlutufEventResult.ENABLED
                if (bleStateFlow.value) {
                    startScan()
                } else {
                    stopScan()
                }
            }
            BlutufEvent.LOCATION_STATUS_CHANGE -> {
            }
        }
    }


    fun startScan() {
        uiModel.postValue(Resource.Scanning())
        devices.clear()
        viewModelScope.launch {
            Blutuf.bleManager.startScan(
                onDeviceAppeared = { device ->
                    if (!devices.any { it.device.identifier == device.identifier }) {
                        devices.add(DeviceUiState(device, State.DISCONNECTED, null))
                        uiModel.postValue(Resource.Success(devices))
                    }
                },
                onScanError = {
                    uiModel.postValue(Resource.Error(it))
                },
                onDeviceDisappeared = { device ->
                    devices.removeAll { it.device.identifier == device.identifier }
                },
                onDeviceUpdated = null // you can implement device updated callback here
            )
        }
        Timer("ScanTimer", false).schedule(SCAN_TIMEOUT) {
            uiModel.postValue(Resource.Complete())
            stopScan()
        }
    }

    private fun stopScan() {
        Blutuf.bleManager.stopScan()
    }

    fun bluetoothStateChange(enable: Boolean) {
        BluetoothRepository.enableBluetooth(enable)
    }

    fun connectDevice(identifier: String) {
        viewModelScope.launch {
            val selectedDevice = Blutuf.bleManager.getDevice(identifier)
            devices.find { it.device.identifier == identifier }?.let {
                it.state = State.CONNECTING
                uiModel.postValue(Resource.Success(devices))
            }
            selectedDevice.addConnectionCallback { connectionState ->
                devices.find { it.device.identifier == identifier }?.let {
                    it.state = when (connectionState) {
                        is ConnectionState.Ready -> {
                            State.CONNECTING
                        }
                        is ConnectionState.Connected -> {
                            State.CONNECTED
                        }
                        is ConnectionState.Disconnected -> {
                            State.DISCONNECTED
                        }
                        is ConnectionState.ConnectionError -> {
                            State.DISCONNECTED
                        }
                    }
                    uiModel.postValue(Resource.Success(devices))
                }
            }
            selectedDevice.connect()
        }
    }

    fun disconnectDevice(identifier: String) {
        val selectedDevice = Blutuf.bleManager.getDevice(identifier)
        selectedDevice.disconnect()
        devices.find { it.device.identifier == identifier }?.let {
            it.state = State.DISCONNECTED
            uiModel.postValue(Resource.Success(devices))
        }
    }

    fun createBond(identifier: String) {
        val selectedDevice = Blutuf.bleManager.getDevice(identifier)
        selectedDevice.createBond {
            devices.find { deviceUiState -> deviceUiState.device.identifier == identifier }?.let {
                it.bonding = selectedDevice.bondState
                uiModel.postValue(Resource.Success(devices))
            }
        }
    }
}