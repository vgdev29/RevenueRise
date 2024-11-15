package com.apc.revenuerise.providers

import com.apc.revenuerise.api.HomeApi
import com.apc.revenuerise.dispatchers.DispatcherTypes
import com.apc.revenuerise.repository.home.HomeDefRepo
import com.apc.revenuerise.repository.home.HomeMainRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvider {


    @Singleton
    @Provides
    fun provideHomeRepository(api: HomeApi, dispatcherProvider: DispatcherTypes): HomeMainRepo =
        HomeDefRepo(api, dispatcherProvider)
}