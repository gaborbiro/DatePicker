package com.gb.datepicker.util

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

class PersistDelegate(private val accessor: () -> KMutableProperty0<Int?>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = accessor().get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, i: Int?) = accessor().set(i)
}