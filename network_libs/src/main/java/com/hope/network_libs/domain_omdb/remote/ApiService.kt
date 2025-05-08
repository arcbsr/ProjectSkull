package com.hope.network_libs.domain_omdb.remote

import com.hope.models.models.MovieDetailsResponse
import com.hope.models.models.SearchResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") searchQuery: String,
        @Query("apikey") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("y") year: String = "2000",
        @Query("type") type: String = "movie",
    ): SearchResponseData

    @GET("/")
    suspend fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") id: String
    ): MovieDetailsResponse
}