package com.example.vrgroup_rest_demo

import com.example.vrgroup_rest_demo.model.AnswerDto
import com.example.vrgroup_rest_demo.model.GameDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServerService {

    @GET("v1/games/{name}")
    fun getGameDto(@Path("name") name : String): Call<GameDto>

    @POST("v1/games/{name}")
    fun submitAnswer(@Path("name") name : String, @Body answerResponse : AnswerDto): Call<Void>
}