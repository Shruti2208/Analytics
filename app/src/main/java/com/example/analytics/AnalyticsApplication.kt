package com.example.analytics

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS THIS FILE?
//
// Every Android app has one Application class — it is the very first thing that
// runs when your app starts, before any screen is shown. Think of it as the
// "root" of your entire app.
//
// By default Android creates a plain Application for you. Here we are telling
// Android to use OUR custom one so we can set up Hilt.
// ─────────────────────────────────────────────────────────────────────────────

// WHAT IS HILT?
//
// Hilt is a Dependency Injection (DI) framework. DI sounds scary but the idea
// is simple:
//
//   Normally if class A needs class B, class A creates class B itself:
//       class ViewModel { val repo = AnalyticsRepositoryImpl() }
//
//   With DI, class A just *asks* for class B and something else provides it:
//       class ViewModel(val repo: AnalyticsRepository)  ← Hilt gives it
//
// Benefits:
//   - You can swap implementations easily (e.g. real data vs fake data for testing)
//   - Classes are not tightly coupled to each other
//   - Hilt manages lifecycles — creates objects once and reuses them (Singleton)
//
// Hilt needs to be "installed" into the Application class so it can start
// managing all these objects the moment the app launches.

// @HiltAndroidApp
// This annotation triggers Hilt's code generation. At build time, Hilt reads
// this annotation and generates all the "plumbing" code that wires your classes
// together. Without this, Hilt simply won't work anywhere in the app.
@HiltAndroidApp
class AnalyticsApplication : Application()
