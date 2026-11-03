package com.apps.stackoverflow.di
import com.apps.stackoverflow.domain.contracts.StackOverflowRepository
import com.apps.stackoverflow.data.remote.repository.StackOverflowRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module{

single<StackOverflowRepository> {
    StackOverflowRepositoryImpl(get())
}

//    single<RecentSearchesRepository>{
//
//    }
}