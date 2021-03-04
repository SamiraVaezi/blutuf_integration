package net.grandcentrix.blutufintegration.di

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository
import net.grandcentrix.blutufintegration.ui.detail.DetailViewModel
import net.grandcentrix.blutufintegration.ui.list.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {

    viewModel {
        ListViewModel(bluetoothRepository = get())
    }

    viewModel { (id: String) ->
        DetailViewModel(id, bluetoothRepository = get())
    }

    single {
        BluetoothRepository()
    }
}