package net.grandcentrix.blutufintegration.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class DetailViewModel(identifier: String) : ViewModel() {

    val uiModel = MutableLiveData<DeviceUiState>()

    val selectedDevice = BluetoothRepository.selectedDeviceStateFlow.asLiveData()

    init {
        uiModel.value = BluetoothRepository.getDevice(identifier)
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        BluetoothRepository.connectDevice(deviceUiState)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        BluetoothRepository.disconnectDevice(deviceUiState)
    }
}