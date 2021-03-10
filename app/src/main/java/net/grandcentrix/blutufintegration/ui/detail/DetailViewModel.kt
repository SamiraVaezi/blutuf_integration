package net.grandcentrix.blutufintegration.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.collect
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.domain.ConnectUseCase
import net.grandcentrix.blutufintegration.domain.DisconnectUseCase
import net.grandcentrix.blutufintegration.domain.GetDeviceUseCase
import net.grandcentrix.blutufintegration.domain.GetSelectedDeviceUseCase

class DetailViewModel(
    identifier: String,
    getDeviceUseCase: GetDeviceUseCase,
    getSelectedDeviceUseCase: GetSelectedDeviceUseCase,
    private val connectUseCase: ConnectUseCase,
    private val disconnectUseCase: DisconnectUseCase
) : ViewModel() {

    val uiModel = liveData {
        emit(getDeviceUseCase.execute(identifier))
    }

    val selectedDevice = liveData {
        getSelectedDeviceUseCase.execute().collect {
            it?.let { selectedDevice -> emit(selectedDevice) }
        }
    }

    fun onConnectClicked(deviceUiState: DeviceUiState) {
        connectUseCase.execute(deviceUiState.device.identifier)
    }

    fun onDisconnectClicked(deviceUiState: DeviceUiState) {
        disconnectUseCase.execute(deviceUiState.device.identifier)
    }
}