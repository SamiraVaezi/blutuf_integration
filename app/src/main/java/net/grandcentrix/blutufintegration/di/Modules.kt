package net.grandcentrix.blutufintegration.di

import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository
import net.grandcentrix.blutufintegration.ui.detail.DetailViewModel
import net.grandcentrix.blutufintegration.ui.list.ListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val LIST_VIEW_MODEL = "list"
const val DETAIL_VIEW_MODEL: String = "detail"
const val REPOSITORY = "repository"

val applicationModule = module {

    viewModel(named(LIST_VIEW_MODEL)) {
        ListViewModel(get(named(REPOSITORY)))
    }

    viewModel(named(DETAIL_VIEW_MODEL)) { (id: String) ->
        DetailViewModel(id, get(named(REPOSITORY)))
    }

    single(named(REPOSITORY)) {
        BluetoothRepository()
    }
}