package com.hope.lib_mvvm

import com.hope.lib_mvvm.viewmodel.BaseViewModel

class MainViewModel : BaseViewModel() {
    init {

    }

    fun simulateLoadingProcess() {
        // Show dialog
        loadingChange.showDialog.postValue("Loading, please wait...")

        // After some time, dismiss dialog
        Thread.sleep(2000)  // Simulate delay (you'd replace this with real async code)
        loadingChange.dismissDialog.postValue(true)
    }
}
