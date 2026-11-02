package com.apps.stackoverflow.di

import com.apps.stackoverflow.data.remote.KtorStackExchangeApi
import com.apps.stackoverflow.data.remote.StackExchangeApi
import org.koin.dsl.module

val networkModule = module {

    single {
        HttpClientFactory.create()
    }

    single<StackExchangeApi> {
        KtorStackExchangeApi(
            client = get()
        )
    }
}