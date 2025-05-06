package com.hope.lib_mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.hope.lib_mvvm.callback.livedata.event.EventLiveData

open class BaseViewModel : ViewModel() {

    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }
    inner class UiLoadingChange {
        val showDialog by lazy { EventLiveData<String>() }
        val dismissDialog by lazy { EventLiveData<Boolean>() }
    }

}