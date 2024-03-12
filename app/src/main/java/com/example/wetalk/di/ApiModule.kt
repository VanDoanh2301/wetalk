package com.example.wetalk.di

import androidx.databinding.ktx.BuildConfig
import com.example.wetalk.data.remote.ApiChat
import com.example.wetalk.data.remote.ApiUser
import com.example.wetalk.data.remote.ApiTopicStudy
import com.example.wetalk.data.remote.ApiUpload
import com.example.wetalk.ui.viewmodels.ChatHomeViewModel
import com.example.wetalk.ui.viewmodels.ChatTabViewModel
import com.example.wetalk.util.BASE_URL_1
import com.example.wetalk.util.BASE_URL_2
import com.example.wetalk.util.BASE_URL_3
import com.example.wetalk.util.BASE_URL_4
import com.example.wetalk.util.BASE_URL_SOCKET
import com.example.wetalk.util.SharedPreferencesUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object ApiModule {
    @Provides
    @ViewModelScoped
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return loggingInterceptor
    }

    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val token = SharedPreferencesUtils.getToken()
                val requestBuilder = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            .callTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }
    /** Build api host 8090 */
    @Provides
    @ViewModelScoped
    @ApiOne
    fun provideRetrofitOne(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_1)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    /** Build api host 8080 */
    @Provides
    @ViewModelScoped
    @ApiTwo
    fun provideRetrofitTwo(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_2)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    /** Build api host 8060 */
    @Provides
    @ViewModelScoped
    @ApiThree
    fun provideRetrofitThree(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_3)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @ViewModelScoped
    @ApiFour
    fun provideRetrofitFour(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_4)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    @Provides
    @ViewModelScoped
    @ApiFive
    fun provideRetrofitFive(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SOCKET)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }


    @Provides
    @ViewModelScoped
    fun provideNewsApi(@ApiOne retrofit: Retrofit): ApiUser {
        return retrofit.create(ApiUser::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideUploadApi(@ApiTwo retrofit: Retrofit): ApiUpload {
        return retrofit.create(ApiUpload::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideStudy(@ApiThree retrofit: Retrofit): ApiTopicStudy {
        return retrofit.create(ApiTopicStudy::class.java)
    }
    @Provides
    @ViewModelScoped
    fun provideChat(@ApiFour retrofit: Retrofit): ApiChat {
        return retrofit.create(ApiChat::class.java)
    }

}


