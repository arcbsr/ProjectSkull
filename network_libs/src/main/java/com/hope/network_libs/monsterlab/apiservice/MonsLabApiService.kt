package com.hope.network_libs.monsterlab.apiservice

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

public interface MonsterApiService {
    data class MonsEffectResponse(
        val message: String,
        val process_id: String,
        val status_url: String,
        val callback_url: String?,
        val webhook_url: String?,
        val output: List<String> // Make sure this matches the actual API response
    )

    data class ImageProcessingResponse(
        val process_id: String,
        val status: String,
        val result: Result,
        val credit_used: Int,
        val overage: Int
    )

    data class Result(
        val output: List<String>
    )

    @Multipart
    @POST("generate/img2img")
    suspend fun generateImage(
        @Header("accept") accept: String = "application/json",
        @Header("authorization") authorization: String,
        @Part("prompt") prompt: RequestBody,
        @Part image: MultipartBody.Part
    ): MonsEffectResponse

    @Multipart
    @POST("generate/pix2pix")
    suspend fun generateImagepix2pix(
        @Header("accept") accept: String = "application/json",
        @Header("authorization") authorization: String,
        @Part("prompt") prompt: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<MonsEffectResponse>

    @Multipart
    @POST("generate/photo-maker")
    suspend fun generateImagePhotoMaker(
        @Header("accept") accept: String = "application/json",
        @Header("authorization") authorization: String,
        @Part("prompt") prompt: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<MonsEffectResponse>

    @GET("status/{processId}")
    suspend fun getImageProcessingStatus(
        @Path("processId") processId: String,
        @Header("accept") accept: String = "application/json",
        @Header("authorization") authorization: String
    ): ImageProcessingResponse

}
