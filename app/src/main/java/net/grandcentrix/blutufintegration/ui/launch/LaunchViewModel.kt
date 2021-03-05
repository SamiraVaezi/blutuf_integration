package net.grandcentrix.blutufintegration.ui.launch

import androidx.lifecycle.*
import net.grandcentrix.blutufintegration.data.model.ErrorCondition
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class LaunchViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    val conditions = MutableLiveData<List<ErrorCondition>>()

    init {
        checkPreconditions()
    }

    private fun checkPreconditions() {
        conditions.value = bluetoothRepository.checkPreconditions()
    }
}

