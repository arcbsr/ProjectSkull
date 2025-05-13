package com.hope.common.log

import android.util.Log
import com.hope.common.BuildConfig

object Log {

    private const val DEFAULT_TAG = "HopeLog"

    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, buildMessage(message))
        }
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, buildMessage(message))
        }
    }

    fun w( tag: String = DEFAULT_TAG, message: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, buildMessage(message))
        }
    }

    fun e(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, buildMessage(message), throwable)
        }
    }

    private fun buildMessage(msg: String): String {
        val stackTrace = Thread.currentThread().stackTrace
        val stackIndex = 5 // [0] getStackTrace, [1] buildMessage, [2] d/i/w/e, [3] caller
        if (stackTrace.size <= stackIndex) return msg

        val element = stackTrace[stackIndex]
        val fileName = element.fileName
        val lineNumber = element.lineNumber
        val methodName = element.methodName

        return "($fileName:$lineNumber) #$methodName ➤ $msg"
    }
}