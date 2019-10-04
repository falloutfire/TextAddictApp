package com.textaddict.app.network

import android.util.Log
import retrofit2.Response
import java.io.IOException

open class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, error: String): T? {
        val result = loginApiOutput(call, error)
        var output: T? = null
        when (result) {
            is Output.Success ->
                output = result.output
            is Output.Error -> Log.e("Error", "The $error and the ${result.exception}")
        }
        return output

    }

    private suspend fun <T : Any> loginApiOutput(
        call: suspend () -> Response<T>,
        error: String
    ): Output<T> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                Log.e("Success", response.code().toString())
                Output.Success(response.body()!!)
            } else
                Output.Error(IOException("OOps .. Something went wrong due to  $error"))
        } catch (e: Exception) {
            Output.Error(Exception("OOps .. Something went wrong due to  $e"))
        }
    }

    suspend fun <T : Any> safeApiResponse(
        call: suspend () -> Response<T>,
        error: String
    ): Response<T> {
        val response = call.invoke()
        return if (response.isSuccessful) {
            Log.e("Success", response.code().toString())
            response
        } else {
            Output.Error(IOException("OOps .. Something went wrong due to  $error"))
            response
        }
    }

    private suspend fun <T : Any> apiOutput(
        call: suspend () -> Response<Result<T>>,
        error: String
    ): Output<Result<T>> {
        val response = call.invoke()
        return if (response.isSuccessful) {
            Log.e("Success", response.code().toString())
            if ((response.body() as Result).status != "error") {
                Output.Success(response.body()!!)
            } else {
                Output.Error(IOException("OOps .. Something went wrong due to  $error"))
            }
        } else {
            Output.Error(IOException("OOps .. Something went wrong due to  $error"))
        }
    }

    data class Result<T : Any>(var status: String, var message: T)

    val ERROR = "error"
}