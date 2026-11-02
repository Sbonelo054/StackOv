package com.apps.stackoverflow.di
import com.apps.stackoverflow.domain.contracts.repository.RecentSearchesRepository
import com.apps.stackoverflow.domain.contracts.repository.StackOverflowRepository
import com.apps.stackoverflow.domain.contracts.repository.StackOverflowRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module{

single<StackOverflowRepository> {
    StackOverflowRepositoryImpl(get())
}

//    single<RecentSearchesRepository>{
//
//    }
}