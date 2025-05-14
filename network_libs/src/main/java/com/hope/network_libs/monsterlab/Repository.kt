package com.hope.network_libs.monsterlab

import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.monsterlab.apiservice.MonsterApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


interface Repository {

    suspend fun getImageProcessingResponse(
        processId: String,
        authorization: String,
    ): Flow<ResponseWrapper<MonsterApiService.ImageProcessingResponse>>

    suspend fun generateImage(
        imagePart: MultipartBody.Part,
        authorization: String,
        prompt: RequestBody,
    ): Flow<ResponseWrapper<MonsterApiService.MonsEffectResponse>>
    suspend fun generateTxtToImage(
        authorization: String,
        prompt: RequestBody,
    ): Flow<ResponseWrapper<MonsterApiService.MonsEffectResponse>>
}