package com.hope.network_libs.monsterlab


import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.monsterlab.apiservice.MonsterApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class RepositoryImpl @Inject constructor(private val remoteDataSource: DataSource) : Repository {

    override suspend fun getImageProcessingResponse(
        processId: String,
        authorization: String
    ): Flow<ResponseWrapper<MonsterApiService.ImageProcessingResponse>> {
        return flow {
            emit(remoteDataSource.getImageProcessingResponse(processId, authorization))
        }
    }

    override suspend fun generateImage(
        imagePart: MultipartBody.Part,
        authorization: String,
        prompt: RequestBody
    ): Flow<ResponseWrapper<MonsterApiService.MonsEffectResponse>> {
        return flow {
            emit(remoteDataSource.generateImage(imagePart, authorization, prompt))
        }
    }

    override suspend fun generateTxtToImage(
        authorization: String,
        prompt: RequestBody
    ): Flow<ResponseWrapper<MonsterApiService.MonsEffectResponse>> {
        return flow {
            emit(remoteDataSource.generateTxtToImage(authorization, prompt))
        }
    }
}