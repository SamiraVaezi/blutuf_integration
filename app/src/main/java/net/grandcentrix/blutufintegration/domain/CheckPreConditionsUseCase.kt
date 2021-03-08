package net.grandcentrix.blutufintegration.domain

import net.grandcentrix.blutufintegration.data.model.ErrorCondition
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class CheckPreConditionsUseCase(private val bluetoothRepository: BluetoothRepository) {

    fun execute(): List<ErrorCondition> = bluetoothRepository.checkPreconditions()
}