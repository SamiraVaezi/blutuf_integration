package net.grandcentrix.blutufintegration.domain

import kotlinx.coroutines.flow.Flow
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.ProcessState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class ScanUseCase(private val bluetoothRepository: BluetoothRepository) {

    suspend fun execute(): Flow<ProcessState<List<DeviceUiState>>> {
        bluetoothRepository.scan()
        return bluetoothRepository.devicesStateFlow
    }
}