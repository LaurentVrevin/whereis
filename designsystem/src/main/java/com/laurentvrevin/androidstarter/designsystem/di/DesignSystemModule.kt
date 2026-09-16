package com.laurentvrevin.androidstarter.designsystem.di

import org.koin.dsl.module

val designSystemModule =
    module {
        // Currently no global UI singletons needed.
        // Feedback (Snackbar) is managed locally in ViewModels/Screens.
    }
