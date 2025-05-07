package com.hope.projectskull

import android.app.Application
import com.hope.main_ui.MyApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : MyApplication() {
}
