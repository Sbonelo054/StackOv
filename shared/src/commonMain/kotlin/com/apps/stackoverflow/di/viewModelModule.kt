package com.apps.stackoverflow.di

import com.apps.stackoverflow.presentation.DetailViewModel
import com.apps.stackoverflow.presentation.SearchViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val viewModelModule = module{
    viewModel {
        SearchViewModel(get(), get()
        )
    }

    viewModel {
        DetailViewModel(get(), get(), get())
    }
}