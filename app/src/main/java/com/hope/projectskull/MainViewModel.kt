package com.hope.projectskull

import com.hope.projectskull.mvvm.base.viewmodel.BaseViewModel

class MainViewModel : BaseViewModel() {
    init {

    }

    fun simulateLoadingProcess() {
        // Show dialog
        loadingChange.showDialog.postValue("Loading, please wait...")

        // After some time, dismiss dialog
        Thread.sleep(5000)  // Simulate delay (you'd replace this with real async code)
        loadingChange.dismissDialog.postValue(true)
    }
}
