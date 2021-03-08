package net.grandcentrix.blutufintegration.domain

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class ScanUseCase(private val bluetoothRepository: BluetoothRepository) {

    val devicesFlow = bluetoothRepository.devicesStateFlow

    suspend fun execute() {
        bluetoothRepository.scan()
    }
}