package com.example.alightmotiongenerator

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SendRequest(val email: String)
data class VerifyRequest(val email: String, val rawLink: String)

data class SendResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val orderCode: String? = null
)

data class VerifyResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val oobCode: String? = null,
    val idToken: String? = null,
    val userProfile: Any? = null
)

interface AlightApiService {
    @POST("/api/send")
    suspend fun sendMagicLink(@Body request: SendRequest): Response<SendResponse>

    @POST("/api/verify")
    suspend fun verifyAccount(@Body request: VerifyRequest): Response<VerifyResponse>
}