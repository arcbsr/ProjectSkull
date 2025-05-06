package com.hope.main_ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.hope.common.router.RoutePath
import com.hope.main_ui.R


@Route(path = RoutePath.Home.HOME)
class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_mainactivity)
    }
}