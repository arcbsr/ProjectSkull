package com.hope.network_libs.domain_omdb.repositoryimpl


import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.model_omdb.models.MovieDetailsResponse
import com.hope.model_omdb.models.SearchResponseData
import com.hope.network_libs.domain_omdb.repository.Repository
import com.hope.network_libs.domain_omdb.datsource.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class RepositoryImpl @Inject constructor(private val remoteDataSource: DataSource) : Repository {
    override suspend fun getAllMovieFromAPI(
        searchQuery: String,
        page: Int,
        year: String
    ): Flow<ResponseWrapper<SearchResponseData>> {
        return flow {
            emit(remoteDataSource.getMoviesFromAPI(searchQuery, page, year = year))
        }
    }

    override suspend fun getMovieDetail(id: String): Flow<ResponseWrapper<MovieDetailsResponse>> {
        return flow {
            emit(remoteDataSource.getMovieDetail(id))
        }
    }
}