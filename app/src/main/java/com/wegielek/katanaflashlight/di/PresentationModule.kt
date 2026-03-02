package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.presentation.viewmodels.KatanaViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel {
            KatanaViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
    }
