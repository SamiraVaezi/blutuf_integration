package net.grandcentrix.blutufintegration

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.*
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.data.model.StateResource
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository
import java.util.*
import kotlin.concurrent.schedule


private const val SCAN_TIMEOUT: Long = 30000

class MainViewModel : ViewModel() {

    val uiModel = MutableLiveData<Resource<List<DeviceUiState>>>()
    private val devices = mutableListOf<DeviceUiState>()

    var bleStateFlow = MutableStateFlow(false)

    val selectedDeviceState: MutableStateFlow<StateResource<Any>> =
        MutableStateFlow(StateResource.Disconnected())

    var selectedDevice: SelectedDevice = Blutuf.bleManager.getDevice("")

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
                    if (!devices.any {
                            it.device.advertisementData.deviceName == device.advertisementData.deviceName
                        }) {
                        devices.add(DeviceUiState(device, State.DISCONNECTED, null, arrayListOf()))
                        uiModel.postValue(Resource.Success(devices))
                    }
                },
                onScanError = {
                    uiModel.postValue(Resource.Error(it))
                },
                onDeviceDisappeared = { device ->
                    devices.removeAll { it.device.identifier == device.identifier }
                },
                onDeviceUpdated = {
                }
            )
        }
        Timer("ScanTimer", false).schedule(SCAN_TIMEOUT) {
            uiModel.postValue(Resource.Complete())
            stopScan()
        }
    }

    @ExperimentalCoroutinesApi
    fun startScanTest(): Flow<List<DeviceUiState>> = channelFlow {
        Blutuf.bleManager.startScan(
            onDeviceAppeared = { device ->
                if (!devices.any {
                        it.device.advertisementData.deviceName == device.advertisementData.deviceName
                    }) {
                    devices.add(DeviceUiState(device, State.DISCONNECTED, null, arrayListOf()))
                    if (!isClosedForSend) {
                        offer(devices)
                    }
                }
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
        awaitClose { cancel() }
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
            val deviceUiState = devices.find { it.device.identifier == identifier }
            deviceUiState?.let {
                it.state = State.CONNECTING
                uiModel.postValue(Resource.Success(devices))
            }
            selectedDevice.addConnectionCallback { connectionState ->
                devices.find { it.device.identifier == identifier }?.let {
                    Log.e("sami", "${it.device.name} -> " + it.state)
                    when (connectionState) {
                        is ConnectionState.Connected -> {
                            it.state = State.CONNECTED
                            uiModel.postValue(Resource.Success(devices))
                        }
                        is ConnectionState.Ready -> {
                            connectionState.device.services()?.forEach { service ->

                                deviceUiState?.services?.add(service)
                                uiModel.postValue(Resource.Success(devices))
                                Log.e("sami", "service -> " + service.identifier)

                                /*service.characteristics.forEach {
                                    characteristicAdapter.add(it)
                                }*/
                            }
                        }
                        is ConnectionState.Disconnected -> {
                            it.state = State.DISCONNECTED
                            uiModel.postValue(Resource.Success(devices))
                        }
                        else -> {
                            return@let
                        }
                    }
                }
            }
            selectedDevice.connect()
        }
    }

    fun connectDevicetest(identifier: String) {
        val selectedDevice = Blutuf.bleManager.getDevice(identifier)
        selectedDeviceState.value = StateResource.Connecting()

        val connectionCallback: ConnectionCallback = { connectionState ->
            when (connectionState) {
                is ConnectionState.Connected -> {
                    selectedDeviceState.value = StateResource.Connected()
                }
                is ConnectionState.Ready -> {
                }
                is ConnectionState.Disconnected -> {
                    selectedDeviceState.value = StateResource.Disconnected()
                }
                is ConnectionState.ConnectionError -> {
                    selectedDeviceState.value = StateResource.Error(connectionState.error)
                }
            }
        }
        selectedDevice.addConnectionCallback(connectionCallback)
        selectedDevice.connect()
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
        devices.find {
            it.device.identifier == identifier
        }?.let {
            it.bonding = Bonding.State.BONDING
            uiModel.postValue(Resource.Success(devices))
        }
        val selectedDevice = Blutuf.bleManager.getDevice(identifier)
        selectedDevice.createBond {
            devices.find { deviceUiState -> deviceUiState.device.identifier == identifier }?.let {
                it.bonding = selectedDevice.bondState
                uiModel.postValue(Resource.Success(devices))
            }
        }
    }
}