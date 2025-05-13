package com.hope.network_libs.domain_omdb.datsource

import com.hope.models.models.MovieDetailsResponse
import com.hope.models.models.SearchResponseData
import com.hope.network_libs.BaseDataSource
import com.hope.network_libs.BuildConfig
import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.domain_omdb.remote.ApiService
import com.hope.network_libs.domain_omdb.remote.IoDispatcher
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

}