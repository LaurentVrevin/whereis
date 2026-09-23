package com.laurentvrevin.wheris.core.location.di

import com.google.android.gms.location.LocationServices
import com.laurentvrevin.wheris.core.location.FusedUserLocationRepository
import com.laurentvrevin.wheris.domain.repository.UserLocationRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val locationModule =
    module {
        single {
            LocationServices.getFusedLocationProviderClient(androidContext())
        }
        single<UserLocationRepository> {
            FusedUserLocationRepository(
                context = androidContext(),
                fusedLocationClient = get(),
            )
        }
    }
