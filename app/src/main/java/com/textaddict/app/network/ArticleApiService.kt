package com.textaddict.app.network

import com.textaddict.app.network.service.HTTPS_API_TEXTADDICT_URL
import com.textaddict.app.network.service.UserService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type


object ArticleApiService {
    //creating a Network Interceptor to add api_key in all the request as authInterceptor
    private val interceptor = Interceptor { chain ->
        val url =
            chain.request().url().newBuilder().addQueryParameter("apiKey", "f1e5ca69296b4e70a3fb7fc722a63615").build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        chain.proceed(request)
    }
    // we are creating a networking client using OkHttp and add our authInterceptor.
    private val apiClient = OkHttpClient().newBuilder().addInterceptor(interceptor).build()

    private fun getRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging)

        return Retrofit.Builder()
            .baseUrl(HTTPS_API_TEXTADDICT_URL)
            .addConverterFactory(nullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    val userService: UserService = getRetrofit().create(UserService::class.java)
}

val nullOnEmptyConverterFactory = object : Converter.Factory() {
    fun converterFactory() = this
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>, retrofit: Retrofit) =
        object : Converter<ResponseBody?, Any?> {
            val nextResponseBodyConverter =
                retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)

            override fun convert(value: ResponseBody?) =
                if (value?.contentLength() != 0L) nextResponseBodyConverter.convert(value) else null
        }
}