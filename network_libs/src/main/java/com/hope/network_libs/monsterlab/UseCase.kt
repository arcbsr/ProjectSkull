package com.hope.network_libs.monsterlab

import com.hope.network_libs.datawrapper.ResponseWrapper
import com.hope.network_libs.monsterlab.apiservice.MonsterApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class UseCase @Inject constructor(private val repository: Repository) {


    suspend fun getImageProcessingResponse(
        processId: String,
        authorization: String
    ): Flow<ResponseWrapper<MonsterApiService.ImageProcessingResponse>> {
        return repository.getImageProcessingResponse(
            processId = processId,
            authorization = authorization
        ).map {
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

    suspend fun generateImage(
        imagePart: MultipartBody.Part,
        authorization: String,
        prompt: RequestBody,
    ): Flow<ResponseWrapper<MonsterApiService.MonsEffectResponse>> {
        return repository.generateImage(
            imagePart = imagePart,
            authorization = authorization,
            prompt = prompt,
        ).map {
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
    suspend fun generateTextImage(
        authorization: String,
        prompt: RequestBody,
    ): Flow<ResponseWrapper<MonsterApiService.MonsEffectResponse>> {
        return repository.generateTxtToImage(
            authorization = authorization,
            prompt = prompt,
        ).map {
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
