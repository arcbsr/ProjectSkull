package com.hope.network_libs.monsterlab.di


import com.hope.network_libs.monsterlab.DataSource
import com.hope.network_libs.monsterlab.Repository
import com.hope.network_libs.monsterlab.RepositoryImpl
import com.hope.network_libs.monsterlab.apiservice.MonsterApiService
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
    fun provideApi(): MonsterApiService = OkHttpClient().newBuilder()
//        .apply {
//            addInterceptor(AuthTokenInterceptor())
//        }
        .build().let {
            Retrofit.Builder()
                .client(it)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.monsterapi.ai/v1/").build().create(MonsterApiService::class.java)
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