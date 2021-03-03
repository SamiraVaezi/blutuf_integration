package net.grandcentrix.blutufintegration.di

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository
import net.grandcentrix.blutufintegration.ui.detail.DetailViewModel
import net.grandcentrix.blutufintegration.ui.launch.LaunchViewModel
import net.grandcentrix.blutufintegration.ui.list.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {

    viewModel { LaunchViewModel(get()) }

    viewModel { ListViewModel(get()) }

    viewModel { (id: String) -> DetailViewModel(id, get()) }

    single {
        BluetoothRepository()
    }
}