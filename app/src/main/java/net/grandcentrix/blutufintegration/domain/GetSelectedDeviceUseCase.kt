package net.grandcentrix.blutufintegration.domain

import kotlinx.coroutines.flow.Flow
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class GetSelectedDeviceUseCase(private val bluetoothRepository: BluetoothRepository) {

    fun execute(): Flow<DeviceUiState?> = bluetoothRepository.selectedDeviceStateFlow
}