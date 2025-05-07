package com.hope.network_libs

import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.exception.NoConnectivityException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

open class BaseDataSource {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T,
        ioDispatcher: CoroutineDispatcher
    ): ResponseWrapper<T> {
        return withContext(ioDispatcher) {
            try {
                val data = apiCall.invoke()
                ResponseWrapper.Success(data)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is NoConnectivityException -> ResponseWrapper.NetworkError
                    is IOException -> ResponseWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val msg = throwable.message()
                        val errorMsg = if (msg.isNullOrEmpty()) {
                            throwable.response()?.errorBody()?.string()
                        } else {
                            msg
                        }
                        ResponseWrapper.GenericError(code, errorMsg)
                    }

                    else -> {
                        ResponseWrapper.GenericError(0, throwable.message)
                    }
                }
            }
        }
    }
}