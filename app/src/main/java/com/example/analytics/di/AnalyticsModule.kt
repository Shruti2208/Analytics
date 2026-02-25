package com.example.analytics.di

import com.example.analytics.data.repository.AnalyticsRepository
import com.example.analytics.data.repository.AnalyticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS THIS FILE?
//
// This is a Hilt "Module" — it is an instruction manual that tells Hilt:
// "when someone asks for X, give them Y".
//
// In our case: when the ViewModel asks for an AnalyticsRepository,
// Hilt should give it an AnalyticsRepositoryImpl.
//
// WHY DO WE NEED THIS?
// Our ViewModel depends on the *interface* AnalyticsRepository, not the
// concrete class AnalyticsRepositoryImpl. This is intentional — the ViewModel
// doesn't care HOW data is fetched (network, database, hardcoded). It just
// calls fetchAnalytics() and trusts the repository to return data.
//
// But Hilt needs to know which concrete class to actually create when the
// interface is requested. That is exactly what this module declares.
// ─────────────────────────────────────────────────────────────────────────────

// @Module — marks this class as a Hilt module (an instruction manual for DI)
@Module

// @InstallIn(SingletonComponent::class)
// Tells Hilt the "scope" of this module — i.e. how long these objects live.
// SingletonComponent = the entire lifetime of the app (created once, destroyed
// when the app process ends). This means the repository is created once and
// shared everywhere — no duplicate instances.
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    // @Binds — tells Hilt: "when AnalyticsRepository is requested, provide
    // AnalyticsRepositoryImpl". Hilt reads the parameter type (what it creates)
    // and the return type (what it satisfies). Abstract function = no body
    // needed, Hilt generates the wiring automatically.
    //
    // @Singleton — ensures only ONE instance of AnalyticsRepositoryImpl is
    // ever created for the whole app. Every class that asks for
    // AnalyticsRepository gets the same object.
    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository
}
