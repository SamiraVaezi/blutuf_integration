package net.grandcentrix.blutufintegration.domain

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class ConnectUseCase(private val bluetoothRepository: BluetoothRepository) {

    fun execute(identifier: String) {
        bluetoothRepository.connectDevice(identifier)
    }
}