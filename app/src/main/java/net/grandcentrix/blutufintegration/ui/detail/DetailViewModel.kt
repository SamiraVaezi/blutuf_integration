package net.grandcentrix.blutufintegration.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.domain.ConnectUseCase
import net.grandcentrix.blutufintegration.domain.DisconnectUseCase
import net.grandcentrix.blutufintegration.domain.GetDeviceUseCase

class DetailViewModel(
    identifier: String,
    getDeviceUseCase: GetDeviceUseCase,
    private val connectUseCase: ConnectUseCase,
    private val disconnectUseCase: DisconnectUseCase
) : ViewModel() {

    val uiModel = MutableLiveData<DeviceUiState>()

    val selectedDevice = getDeviceUseCase.selectedDeviceFlow.asLiveData()

    init {
        uiModel.value = getDeviceUseCase.execute(identifier)
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        connectUseCase.execute(deviceUiState.device.identifier)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        disconnectUseCase.execute(deviceUiState.device.identifier)
    }
}