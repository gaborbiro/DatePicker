package com.gb.datepicker.di

import com.gb.datepicker.ui.PasswordActivity
import com.gb.datepicker.ui.DatePickerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivityInjector(): DatePickerActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeHSBCActivityInjector(): PasswordActivity
}
