package net.grandcentrix.blutufintegration.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class DetailViewModel(identifier: String, private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    val uiModel = MutableLiveData<DeviceUiState>()

    val selectedDevice = bluetoothRepository.selectedDeviceStateFlow.asLiveData()

    init {
        uiModel.value = bluetoothRepository.getDevice(identifier)
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        bluetoothRepository.connectDevice(deviceUiState)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        bluetoothRepository.disconnectDevice(deviceUiState)
    }
}