package com.hope.projectskull.mvvm.base.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hope.projectskull.mvvm.base.callback.livedata.event.EventLiveData

open class BaseAndroidViewModel(app: Application) : AndroidViewModel(app) {

    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    inner class UiLoadingChange {
        val showDialog by lazy { EventLiveData<String>() }
        val dismissDialog by lazy { EventLiveData<Boolean>() }
    }


}