package com.apps.stackoverflow.di

import com.apps.stackoverflow.presentation.QuestionDetailsViewModel
import com.apps.stackoverflow.presentation.QuestionSearchViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val viewModelModule = module{
    viewModel {
        QuestionSearchViewModel(get(), get()
        )
    }

    viewModel {
        QuestionDetailsViewModel(get(), get(), get())
    }
}