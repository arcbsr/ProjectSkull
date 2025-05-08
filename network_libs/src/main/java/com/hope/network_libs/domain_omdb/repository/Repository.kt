package com.hope.network_libs.domain_omdb.repository

import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.models.models.MovieDetailsResponse
import com.hope.models.models.SearchResponseData
import kotlinx.coroutines.flow.Flow


interface Repository {

    suspend fun getAllMovieFromAPI(
        searchQuery: String,
        page: Int,
        year: String = "2000"
    ): Flow<ResponseWrapper<SearchResponseData>>

    suspend fun getMovieDetail(id: String): Flow<ResponseWrapper<MovieDetailsResponse>>
}