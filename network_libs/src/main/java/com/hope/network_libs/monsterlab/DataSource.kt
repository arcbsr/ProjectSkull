package com.hope.network_libs.monsterlab

import com.hope.network_libs.BaseDataSource
import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.domain_omdb.remote.IoDispatcher
import com.hope.network_libs.monsterlab.apiservice.MonsterApiService
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class DataSource @Inject constructor(
    private val apiService: MonsterApiService,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseDataSource() {

    suspend fun getImageProcessingResponse(
        processId: String,
        authorization: String
    ): ResponseWrapper<MonsterApiService.ImageProcessingResponse> {
        return safeApiCall(apiCall = {
            apiService.getImageProcessingStatus(
                processId = processId,
                authorization = authorization,
            )

        }, ioDispatcher)
    }

    suspend fun generateImage(
        imagePart: MultipartBody.Part,
        authorization: String,
        prompt: RequestBody,
    ): ResponseWrapper<MonsterApiService.MonsEffectResponse> {
        return safeApiCall(apiCall = {
            apiService.generateImage(
                image = imagePart,
                authorization = authorization,
                prompt = prompt,
            )

        }, ioDispatcher)
    }

}

