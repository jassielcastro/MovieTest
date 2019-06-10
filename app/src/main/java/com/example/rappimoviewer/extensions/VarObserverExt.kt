package com.example.rappimoviewer.extensions

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class VarWithObservableSetter<T, out TDidSet : (() -> Unit)?>
internal constructor(
    val initialValue: T,
    private val didSet: TDidSet
) : ObservableProperty<T>(initialValue) {
    override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean {
        return true
    }

    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) {
        didSet?.invoke()
    }
}

fun <T> value(initialValue: T) =
    VarWithObservableSetter(initialValue, null)

fun <T, R : (() -> Unit)?>
        VarWithObservableSetter<T, R>.didSet(action: () -> Unit) =
    VarWithObservableSetter(initialValue, action)