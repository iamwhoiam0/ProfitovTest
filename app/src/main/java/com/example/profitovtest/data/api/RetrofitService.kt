package com.example.profitovtest.data.api

import com.example.profitovtest.data.entities.Entry
import com.example.profitovtest.data.entities.HotListId
import com.example.profitovtest.utils.Constants
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitService{

    @GET(Constants.API_HOT_LIST)
    suspend fun getList(): Response<HotListId>

    @GET(Constants.API_POST)
    suspend fun getCurrentPost(@Path("postId") postId:Int): Response<Entry>

    companion object {
        var retrofitService: RetrofitService? = null
        fun getInstance() : RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }

}