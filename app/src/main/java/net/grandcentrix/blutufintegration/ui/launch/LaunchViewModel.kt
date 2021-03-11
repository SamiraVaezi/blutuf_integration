package net.grandcentrix.blutufintegration.ui.launch

import androidx.lifecycle.*
import net.grandcentrix.blutufintegration.domain.CheckPreConditionsUseCase

class LaunchViewModel(private val checkPreConditionsUseCase: CheckPreConditionsUseCase) :
    ViewModel() {

    val conditions = liveData {
        emit(checkPreConditionsUseCase.execute())
    }
}

