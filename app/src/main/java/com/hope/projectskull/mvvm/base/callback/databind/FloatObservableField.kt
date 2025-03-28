package com.hope.projectskull.mvvm.base.callback.databind

import androidx.databinding.ObservableField

class FloatObservableField(value: Float = 0f) : ObservableField<Float>(value) {

    override fun get(): Float {
        return super.get()!!
    }

}