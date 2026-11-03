package com.apps.stackoverflow.di

import com.apps.stackoverflow.data.remote.repository.KtorQuestionsService
import com.apps.stackoverflow.data.remote.QuestionsService
import org.koin.dsl.module

val networkModule = module {

    single {
        HttpClientFactory.create()
    }

    single<QuestionsService> {
        KtorQuestionsService(
            client = get()
        )
    }
}