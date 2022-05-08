package com.example.profitovtest.data.repository

import com.example.profitovtest.data.api.RetrofitService

class MainRepository (private val retrofitService: RetrofitService){

    suspend fun getList() = retrofitService.getList()

}