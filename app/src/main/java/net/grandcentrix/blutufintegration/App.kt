package net.grandcentrix.blutufintegration

import android.app.Application
import android.content.Context
import net.grandcentrix.blutufintegration.di.applicationModule
import org.koin.core.Koin
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        startKoin {
            modules(listOf(applicationModule))
        }
    }
}