package com.hope.lib_mvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hope.lib_mvvm.callback.livedata.event.EventLiveData

open class BaseAndroidViewModel(app: Application) : AndroidViewModel(app) {

    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    inner class UiLoadingChange {
        val showDialog by lazy { EventLiveData<String>() }
        val dismissDialog by lazy { EventLiveData<Boolean>() }
    }


}