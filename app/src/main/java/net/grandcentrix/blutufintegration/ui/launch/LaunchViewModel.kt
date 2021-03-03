package net.grandcentrix.blutufintegration.ui.launch

import androidx.lifecycle.*
import net.grandcentrix.blutuf.core.api.MissingPrecondition
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

class LaunchViewModel(private val repository: BluetoothRepository) : ViewModel() {

    val conditions = MutableLiveData<List<MissingPrecondition>>()

    init {
        checkPreconditions()
    }

    private fun checkPreconditions() {
        conditions.value = repository.checkPreconditions()
    }
}

