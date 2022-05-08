package com.example.profitovtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.profitovtest.data.api.RetrofitService
import com.example.profitovtest.data.entities.HotListId
import com.example.profitovtest.data.repository.MainRepository
import com.example.profitovtest.presentation.viewModel.MainViewModel
import com.example.profitovtest.presentation.viewModel.MainViewModelFactory
import com.example.profitovtest.utils.NetworkResultHandler

class MainActivity : AppCompatActivity(), NetworkResultHandler {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofitService = RetrofitService.getInstance()
        val mainRepository = MainRepository(retrofitService)

        viewModel = ViewModelProvider(this, MainViewModelFactory(mainRepository))[MainViewModel::class.java]

        viewModel.hotList.observe(this) { result ->
            handleCharactersResult(result)
        }

        if (viewModel.hotList.value == null){ // Избавляемся от повторных запросов на сервер при повороте экрана
            viewModel.getHotList()
        }
    }

    override fun handleError(errorMessage: String?) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun <T> handleSuccess(data: T) {
        if (data is HotListId){

        }
    }

    override fun showEmptyView() {
        Toast.makeText(this, "Data is empty", Toast.LENGTH_LONG).show()
    }

}

