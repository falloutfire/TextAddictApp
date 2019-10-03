package com.textaddict.app.network.service

import com.textaddict.app.database.entity.UserLogin
import com.textaddict.app.database.entity.UserToken
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface UserService {

    /*@POST("signin")
    suspend fun loginUserAsync(@Body user: UserLogin): Deferred<Response<UserToken>>*/

    @POST("signin")
    suspend fun loginUserAsync(@Body user: UserLogin): Response<UserToken>

    @POST("register")
    suspend fun registerUserAsync(@Body user: UserLogin): Deferred<Response<UserToken>>

    @POST("user")
    suspend fun getUserAsync(@Body user: UserLogin): Deferred<Response<UserToken?>>
}

var HTTPS_API_TEXTADDICT_URL = "https://text-addict-server.herokuapp.com/"