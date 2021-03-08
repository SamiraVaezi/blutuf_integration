package net.grandcentrix.blutufintegration.di

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository
import net.grandcentrix.blutufintegration.domain.*
import net.grandcentrix.blutufintegration.ui.detail.DetailViewModel
import net.grandcentrix.blutufintegration.ui.launch.LaunchViewModel
import net.grandcentrix.blutufintegration.ui.list.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {

    viewModel {
        LaunchViewModel(checkPreConditionsUseCase = get())
    }

    viewModel {
        ListViewModel(
            scanUseCase = get(),
            stopScanUseCase = get(),
            connectUseCase = get(),
            disconnectUseCase = get(),
            getDeviceUseCase = get()
        )
    }

    viewModel { (id: String) ->
        DetailViewModel(
            id,
            getDeviceUseCase = get(),
            connectUseCase = get(),
            disconnectUseCase = get()
        )
    }

    factory { ScanUseCase(bluetoothRepository = get()) }

    factory { StopScanUseCase(bluetoothRepository = get()) }

    factory { ConnectUseCase(bluetoothRepository = get()) }

    factory { DisconnectUseCase(bluetoothRepository = get()) }

    factory { GetDeviceUseCase(bluetoothRepository = get()) }

    factory { CheckPreConditionsUseCase(bluetoothRepository = get()) }

    single {
        BluetoothRepository()
    }
}