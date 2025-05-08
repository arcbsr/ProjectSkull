package com.hope.network_libs.domain_omdb.usecase

import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.models.models.MovieDetailsResponse
import com.hope.models.models.SearchResponseData
import com.hope.network_libs.domain_omdb.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UseCase @Inject constructor(private val repository: Repository) {


    suspend fun getMovies(
        searchQuery: String,
        page: Int,
        year: String = "2000"
    ): Flow<ResponseWrapper<SearchResponseData>> {
        return repository.getAllMovieFromAPI(searchQuery, page, year).map {
            when (it) {
                is ResponseWrapper.GenericError -> {
                    ResponseWrapper.GenericError(it.code, it.error)
                }

                ResponseWrapper.NetworkError -> {
                    ResponseWrapper.NetworkError
                }

                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
                }
            }
        }
    }

    suspend fun getMovieDetail(
        id: String
    ): Flow<ResponseWrapper<MovieDetailsResponse>> {
        return repository.getMovieDetail(id = id).map {
            when (it) {
                is ResponseWrapper.GenericError -> {
                    ResponseWrapper.GenericError(it.code, it.error)
                }

                ResponseWrapper.NetworkError -> {
                    ResponseWrapper.NetworkError
                }

                is ResponseWrapper.Success -> {
                    ResponseWrapper.Success(it.value)
                }
            }
        }
    }
}