package com.sikderithub.keyboard.internet

import android.util.Base64
import android.util.Log

import com.google.gson.GsonBuilder
import com.sikderithub.keyboard.BuildConfig
import com.sikderithub.keyboard.Models.Config
import com.sikderithub.keyboard.Models.GenericResponse
import com.sikderithub.keyboard.Models.Gk
import com.sikderithub.keyboard.Models.Update
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MyApi {
    @FormUrlEncoded
    @POST("api/get_result_by_id.php")
    fun getLatestQuestion(
           @Field("id") id:String
    ): Call<GenericResponse<List<Gk>>>
    @GET("api/config.all_config_data.php")
    fun getConfig(
            @Query("version_code") version_code:Int
    ) : Call<GenericResponse<Config>>

    @GET("api/update.last_update_data.php")
    fun getLatestUpdate(

    ) : Call<GenericResponse<Update>>




    companion object {

        @Volatile
        private var myApiInstance: MyApi? = null
        private val LOCK = Any()

        operator fun invoke() = myApiInstance ?: synchronized(LOCK) {
            myApiInstance ?: createClient().also {
                myApiInstance = it
            }
        }


        private fun createClient(): MyApi {


            val AUTH: String = "Basic ${
                Base64.encodeToString(
                        ("${BuildConfig.USER_NAME}:${BuildConfig.USER_PASSWORD}").toByteArray(),
                        Base64.NO_WRAP
                )
            }"



            val interceptor = run {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.apply {
                    if(BuildConfig.DEBUG){
                        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    }else{
                        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
                    }
                }
            }




            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                    .readTimeout(2, TimeUnit.MINUTES)
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .callTimeout(2, TimeUnit.MINUTES)


                    .retryOnConnectionFailure(true)

                    .addInterceptor(interceptor)
                    .addInterceptor { chain ->
                        val original: Request = chain.request()
                        val requestBuilder: Request.Builder = original.newBuilder()
                                .addHeader("Connection", "close")
                                .addHeader("Authorization", AUTH)

                        requestBuilder.method(original.method, original.body)
                        val request: Request = requestBuilder.build()

                        chain.proceed(request)
                    }
                    .build()

            val gsonBuilder = GsonBuilder()
            gsonBuilder.setLenient()
            val gson = gsonBuilder.create()

            return Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)

                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
                    .create(MyApi::class.java)
        }


    }
}