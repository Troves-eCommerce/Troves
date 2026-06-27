package com.troves.di

import com.example.presintation.di.presentationModule
import org.koin.core.module.Module

actual fun platformModule(): Module = presentationModule
