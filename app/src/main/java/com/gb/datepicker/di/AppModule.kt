package com.gb.datepicker.di

import android.app.Application
import android.content.Context
import com.gb.prefsutil.PrefsUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideAppPrefsUtil(appContext: Context): PrefsUtil = PrefsUtil(appContext, "user_prefs")
}