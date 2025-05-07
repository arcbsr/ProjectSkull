package com.hope.network_libs.domain_omdb.di


import com.hope.network_libs.domain_omdb.repository.Repository
import com.hope.network_libs.BuildConfig
import com.hope.network_libs.domain_omdb.remote.ApiService
import com.hope.network_libs.domain_omdb.datsource.DataSource
import com.hope.network_libs.domain_omdb.repositoryimpl.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideApi(): ApiService = OkHttpClient().newBuilder()
//        .apply {
//            addInterceptor(AuthTokenInterceptor())
//        }
        .build().let {
            Retrofit.Builder()
                .client(it)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_URL).build().create(ApiService::class.java)
            //"https://www.omdbapi.com/"
        }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            // Add any additional configurations such as interceptors or network settings
            .build()
    }

    @Singleton
    @Provides
    fun provideRepositoryImpl(dataSource: DataSource): Repository = RepositoryImpl(dataSource)

//    @Singleton
//    @Provides
//    fun provideUseCase(repository: Repository): UseCase {
//        return UseCase(repository)
//    }
}