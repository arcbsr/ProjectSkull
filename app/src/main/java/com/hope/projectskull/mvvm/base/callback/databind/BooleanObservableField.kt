package com.hope.projectskull.mvvm.base.callback.databind

import androidx.databinding.ObservableField

class BooleanObservableField(value: Boolean = false) : ObservableField<Boolean>(value) {
    override fun get(): Boolean {
        return super.get()!!
    }

}