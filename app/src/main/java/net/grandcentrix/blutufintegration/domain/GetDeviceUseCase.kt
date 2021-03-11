package net.grandcentrix.blutufintegration.domain

import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class GetDeviceUseCase(private val bluetoothRepository: BluetoothRepository) {

    fun execute(identifier: String): DeviceUiState? = bluetoothRepository.getDevice(identifier)
}