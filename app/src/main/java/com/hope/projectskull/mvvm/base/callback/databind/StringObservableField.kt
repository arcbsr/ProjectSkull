package com.hope.projectskull.mvvm.base.callback.databind

import androidx.databinding.ObservableField

open class StringObservableField(value: String = "") : ObservableField<String>(value) {

    override fun get(): String {
        return super.get()!!
    }

}