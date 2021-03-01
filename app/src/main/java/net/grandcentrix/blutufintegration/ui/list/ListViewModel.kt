package net.grandcentrix.blutufintegration.ui.list

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.BlutufEvent
import net.grandcentrix.blutuf.core.api.BlutufEventResult
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

private const val SCAN_TIMEOUT: Long = 5000

class ListViewModel : ViewModel() {

    val uiModel = BluetoothRepository.devicesStateFlow.asLiveData()

    val selectedDevice: LiveData<DeviceUiState?> =
        BluetoothRepository.selectedDeviceStateFlow.asLiveData()

    var bleStateFlow = MutableStateFlow(false)

    val scanState = MutableLiveData(false)

    init {
        Blutuf.bleManager.registerEventListener(this::onEvent)
    }

    override fun onCleared() {
        Blutuf.bleManager.unregisterEventListener(this::onEvent)
        BluetoothRepository.stopScan()
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
            scanState.value = true
            BluetoothRepository.scan()
            Handler(Looper.getMainLooper()).postDelayed({
                stopScan()
            }, SCAN_TIMEOUT)
        }
    }

    private fun stopScan() {
        BluetoothRepository.stopScan()
        scanState.value = false
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        BluetoothRepository.connectDevice(deviceUiState)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        BluetoothRepository.disconnectDevice(deviceUiState)
    }
}