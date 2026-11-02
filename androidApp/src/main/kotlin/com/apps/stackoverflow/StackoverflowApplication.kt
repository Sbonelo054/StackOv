package com.apps.stackoverflow

import android.app.Application
import com.apps.stackoverflow.di.androidModule
import com.apps.stackoverflow.di.networkModule
import com.apps.stackoverflow.di.repositoryModule
import com.apps.stackoverflow.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class StackoverflowApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StackoverflowApplication)
            androidLogger()
            modules(networkModule, repositoryModule, androidModule, viewModelModule)
        }
    }
}