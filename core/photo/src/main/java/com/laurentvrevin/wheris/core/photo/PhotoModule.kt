package com.laurentvrevin.wheris.core.photo

import com.laurentvrevin.wheris.domain.PhotoStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val photoModule =
    module {
        single { AndroidPhotoStorage(androidContext()) }
        single<PhotoStorage> { get<AndroidPhotoStorage>() }
        single(named("photoOperations")) { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) }
    }
