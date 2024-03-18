package com.example.wetalk.data.remote

import com.example.wetalk.data.model.postmodel.MediaValidatePost
import com.example.wetalk.data.model.responsemodel.ValidationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiValidation {
 @POST("ai/validation")
 suspend fun validationMedia(@Body mediaValidatePost: MediaValidatePost) : Response<ValidationResponse>
}