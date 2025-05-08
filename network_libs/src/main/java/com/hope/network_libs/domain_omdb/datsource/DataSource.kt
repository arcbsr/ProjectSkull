package com.hope.network_libs.domain_omdb.datsource

import com.hope.models.models.MovieDetailsResponse
import com.hope.models.models.SearchResponseData
import com.hope.network_libs.BaseDataSource
import com.hope.network_libs.BuildConfig
import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.domain_omdb.di.IoDispatcher
import com.hope.network_libs.domain_omdb.remote.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DataSource @Inject constructor(
    private val apiService: ApiService, @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseDataSource() {

    suspend fun getMoviesFromAPI(
        searchQuery: String,
        page: Int,
        year: String = "2000"
    ): ResponseWrapper<SearchResponseData> {
        return safeApiCall(apiCall = {
            apiService.searchMovies(
                searchQuery = searchQuery,
                page = page,
                year = year,
                apiKey = BuildConfig.API_KEY
            )

        }, ioDispatcher)
    }

    suspend fun getMovieDetail(id: String): ResponseWrapper<MovieDetailsResponse> {
        return safeApiCall(apiCall = {
            apiService.getMovieDetails(id = id, apiKey = "")

        }, ioDispatcher)
    }

//    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): ResponseWrapper<T> {
//        return withContext(ioDispatcher) {
//            try {
//                val data = apiCall.invoke()
//                ResponseWrapper.Success(data)
//            } catch (throwable: Throwable) {
//                when (throwable) {
//                    is NoConnectivityException -> ResponseWrapper.NetworkError
//                    is IOException -> ResponseWrapper.NetworkError
//                    is HttpException -> {
//                        val code = throwable.code()
//                        val msg = throwable.message()
//                        val errorMsg = if (msg.isNullOrEmpty()) {
//                            throwable.response()?.errorBody()?.string()
//                        } else {
//                            msg
//                        }
//                        ResponseWrapper.GenericError(code, errorMsg)
//                    }
//
//                    else -> {
//                        ResponseWrapper.GenericError(0, throwable.message)
//                    }
//                }
//            }
//        }
//    }

}