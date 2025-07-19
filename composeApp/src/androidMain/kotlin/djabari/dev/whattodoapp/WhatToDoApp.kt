package djabari.dev.whattodoapp

import android.app.Application
import djabari.dev.whattodoapp.di.appModule
import djabari.dev.whattodoapp.di.initSharedKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class WhatToDoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initSharedKoin {
            androidContext(this@WhatToDoApp)
            androidLogger()
            modules(appModule)
        }
    }
}