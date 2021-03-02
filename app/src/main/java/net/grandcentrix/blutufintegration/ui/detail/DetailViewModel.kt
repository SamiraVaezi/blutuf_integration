package net.grandcentrix.blutufintegration.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class DetailViewModel(identifier: String, private val repository: BluetoothRepository) : ViewModel() {

    val uiModel = MutableLiveData<DeviceUiState>()

    val selectedDevice = repository.selectedDeviceStateFlow.asLiveData()

    init {
        uiModel.value = repository.getDevice(identifier)
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        repository.connectDevice(deviceUiState)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        repository.disconnectDevice(deviceUiState)
    }
}