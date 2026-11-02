package com.apps.stackoverflow.di

import com.apps.stackoverflow.connectivity.ConnectivityNetworkMonitor
import com.apps.stackoverflow.data.remote.NetworkMonitor
import com.apps.stackoverflow.platform.AndroidUrlLauncher
import com.apps.stackoverflow.utils.UrlLauncher
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule  = module{
    single<NetworkMonitor> {
        ConnectivityNetworkMonitor(
            context = androidContext()
        )
    }

    single<UrlLauncher> {
        AndroidUrlLauncher(get())
    }
}