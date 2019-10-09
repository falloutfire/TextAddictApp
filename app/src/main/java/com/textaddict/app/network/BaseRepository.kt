package com.textaddict.app.network

import android.util.Log
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException


open class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, error: String): T? {
        val result = safeApiResponse(call, error)
        var output: T? = null
        when (result) {
            is Output.Success ->
                output = result.output
            is Output.Error -> Log.e("Error", "The $error and the ${result.exception}")
        }
        return output
    }

    suspend fun <T : Any> safeApiCallTest(call: suspend () -> Call<T>, error: String): T? {
        val result = safeApiCallToServer(call, error)
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
                Output.Error(IOException("OOps .. Something went wrong due to  $error"), "")
        } catch (e: Exception) {
            Output.Error(Exception("OOps .. Something went wrong due to  $e"), "")
        }
    }

    suspend fun <T : Any> safeApiCallToServer(
        call: suspend () -> Call<T>,
        error: String
    ): Output<T> {
        var output: Output<T>? = null
        call.invoke().enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if ((response.body() is Result)) {
                        if ((response.body() as Result).status == "error") {
                            output =
                                Output.Error(
                                    IOException("OOps .. Something went wrong due to  $error"),
                                    ""
                                )
                        } else {
                            output = Output.Success(response.body()!!)
                        }
                    } else {
                        output = Output.Success(response.body()!!)
                    }
                } else {
                    Log.e("call", response.code().toString())
                    output =
                        Output.Error(IOException("OOps .. Something went wrong due to  $error"), "")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    // "Connection Timeout";
                    output = Output.Error(t, "")
                    //Output.Error(SocketTimeoutException("OOps .. Something went wrong due to  $error, connection timeout"))
                } else if (t is IOException) {
                    // "Timeout";
                    output = Output.Error(t, "")
                    //Output.Error(SocketTimeoutException("OOps .. Something went wrong due to  $error, timeout"))
                } else {
                    //Call was cancelled by user
                    if (call.isCanceled) {
                        Log.e("Call", "Call was cancelled forcefully")
                        output = Output.Error(t as Exception, "")
                    } else {
                        //Generic error handling
                        Log.e("Call", "Network Error :: " + t.localizedMessage)
                        output = Output.Error(t as Exception, "")
                    }
                }
            }
        })

        return output!!
    }

    suspend fun <T : Any> safeApiResponse(
        call: suspend () -> Response<T>,
        error: String
    ): Output<T> {
        return try {
            val response = call.invoke()
            when {
                response.isSuccessful -> Output.Success(response.body()!!)
                response.code() >= 500 -> Output.Error(
                    IOException("OOps .. Something went wrong due to  $error"),
                    "Something wrong. Please, try again later."
                )
                else -> {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Output.Error(
                        IOException("OOps .. Something went wrong due to  $error"),
                        jObjError.getString("message")
                    )
                }
            }
        } catch (e: SocketTimeoutException) {
            Output.Error(
                SocketTimeoutException("OOps .. Something went wrong due to  $error"),
                "Connection Timeout"
            )
        } catch (e: IOException) {
            Output.Error(IOException("OOps .. Something went wrong due to  $error"), "Timeout")
        } catch (e: Exception) {
            Output.Error(
                Exception("OOps .. Something went wrong due to  $error"),
                "Something wrong. Please, try again later."
            )
        }
    }

    private suspend fun <T : Any> apiOutput(
        call: suspend () -> Response<Result>,
        error: String
    ): Output<Result> {
        val response = call.invoke()
        return if (response.isSuccessful) {
            Log.e("Success", response.code().toString())
            if ((response.body() as Result).status != "error") {
                Output.Success(response.body()!!)
            } else {
                Output.Error(IOException("OOps .. Something went wrong due to  $error"), "")
            }
        } else {
            Output.Error(IOException("OOps .. Something went wrong due to  $error"), "")
        }
    }

    class Result() {
        var status: String = ""
        var message: String = ""

        constructor(status: String, message: String) : this() {
            this.status = status
            this.message = message
        }
    }

    val ERROR = "error"
}