package net.grandcentrix.blutufintegration.domain

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class StopScanUseCase(val bluetoothRepository: BluetoothRepository) {

    fun execute() {
        bluetoothRepository.stopScan()
    }
}