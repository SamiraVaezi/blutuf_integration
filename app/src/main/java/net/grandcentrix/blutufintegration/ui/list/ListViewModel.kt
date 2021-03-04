package net.grandcentrix.blutufintegration.ui.list

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.BlutufEvent
import net.grandcentrix.blutuf.core.api.BlutufEventResult
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

private const val SCAN_TIMEOUT: Long = 5000

class ListViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    val uiModel = bluetoothRepository.devicesStateFlow.asLiveData()
    val selectedDevice = bluetoothRepository.selectedDeviceStateFlow.asLiveData()

    var bleStateFlow = MutableStateFlow(false)

    private val _scanState = MutableLiveData(false)
    val scanState : LiveData<Boolean> = _scanState

    init {
        Blutuf.bleManager.registerEventListener(this::onEvent)
    }

    override fun onCleared() {
        Blutuf.bleManager.unregisterEventListener(this::onEvent)
        bluetoothRepository.stopScan()
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
        viewModelScope.launch {
            _scanState.value = true
            bluetoothRepository.scan()
            delay(SCAN_TIMEOUT)
            stopScan()
        }
    }

    private fun stopScan() {
        bluetoothRepository.stopScan()
        _scanState.value = false
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        bluetoothRepository.connectDevice(deviceUiState)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        bluetoothRepository.disconnectDevice(deviceUiState)
    }
}