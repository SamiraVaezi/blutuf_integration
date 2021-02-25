package net.grandcentrix.blutufintegration.ui.list

import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.grandcentrix.blutuf.core.Blutuf
import net.grandcentrix.blutuf.core.api.BlutufEvent
import net.grandcentrix.blutuf.core.api.BlutufEventResult
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository


class ListViewModel : ViewModel() {

    val uiModel = MutableLiveData<Resource<List<DeviceUiState>>>()

    val selectedDevice: LiveData<DeviceUiState?> = BluetoothRepository.selectedDeviceStateFlow.asLiveData()

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

    @ExperimentalCoroutinesApi
    fun startScan() {
        uiModel.postValue(Resource.Scanning())
        viewModelScope.launch {
            BluetoothRepository
                .startScan()
                .onCompletion {
                    uiModel.postValue(Resource.Complete())
                }
                .catch {
                }
                .collect { list ->
                    uiModel.postValue(Resource.Success(list))
                }
        }
    }

    private fun stopScan() {
        BluetoothRepository.stopScan()
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        BluetoothRepository.connectDevice(deviceUiState)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        BluetoothRepository.disconnectDevice(deviceUiState)
    }
}