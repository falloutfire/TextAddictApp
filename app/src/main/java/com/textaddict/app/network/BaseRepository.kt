package com.textaddict.app.network

import android.util.Log
import retrofit2.Response
import java.io.IOException

open class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T?>, error: String): T? {
        val result = newsApiOutput(call, error)
        var output: T? = null
        when (result) {
            is Output.Success ->
                output = result.output
            is Output.Error -> Log.e("Error", "The $error and the ${result.exception}")
        }
        return output

    }

    private suspend fun <T : Any> newsApiOutput(call: suspend () -> Response<T?>, error: String): Output<T> {
        val response = call.invoke()
        return if (response.isSuccessful) {
            Log.e("Success", response.code().toString())
            Output.Success(response.body()!!)
        } else
            Output.Error(IOException("OOps .. Something went wrong due to  $error"))
    }

    suspend fun safeApiResponse(call: suspend () -> okhttp3.Response, error: String): Output<okhttp3.Response> {
        val response = call.invoke()
        return if (response.isSuccessful) {
            Log.e("Success", response.code().toString())
            Output.Success(response)
        } else {
            Output.Error(IOException("OOps .. Something went wrong due to  $error"))
        }
    }
}