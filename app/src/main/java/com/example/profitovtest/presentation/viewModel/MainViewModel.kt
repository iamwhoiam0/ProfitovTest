package com.example.profitovtest.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profitovtest.data.entities.HotListId
import com.example.profitovtest.data.repository.MainRepository
import com.example.profitovtest.utils.NetworkState
import kotlinx.coroutines.*
import java.io.IOException

class MainViewModel(
    private val mainRepository: MainRepository,
): ViewModel() {

    private val _hotList: MutableLiveData<NetworkState<HotListId>> = MutableLiveData()
    val hotList : LiveData<NetworkState<HotListId>>
        get() = _hotList

    fun getHotList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mainRepository.getList().let { response ->
                    if (response.isSuccessful) {
                        if (response.body() !== null) {
                            _hotList.postValue(NetworkState.Success(response.body()!!))
                        } else {
                            _hotList.postValue(NetworkState.InvalidData)
                        }
                    } else {
                        when (response.code()) {
                            403 -> _hotList.postValue(NetworkState.HttpErrors.ResourceForbidden(response.message()))
                            404 -> _hotList.postValue(NetworkState.HttpErrors.ResourceNotFound(response.message()))
                            500 -> _hotList.postValue(NetworkState.HttpErrors.InternalServerError(response.message()))
                            502 -> _hotList.postValue(NetworkState.HttpErrors.BadGateWay(response.message()))
                            301 -> _hotList.postValue(NetworkState.HttpErrors.ResourceRemoved(response.message()))
                            302 -> _hotList.postValue(NetworkState.HttpErrors.RemovedResourceFound(response.message()))
                            else -> _hotList.postValue(NetworkState.Error(response.message()))
                        }
                    }
                }
            }catch (error: IOException){
                _hotList.postValue(NetworkState.NetworkException(error.message))
            }
        }
    }
}