package net.grandcentrix.blutufintegration.domain

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class DisconnectUseCase(private val bluetoothRepository: BluetoothRepository) {

    fun execute(identifier: String) {
        bluetoothRepository.disconnectDevice(identifier)
    }
}