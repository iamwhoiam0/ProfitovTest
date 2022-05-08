package com.example.profitovtest.utils

interface NetworkResultHandler {

    fun handleCharactersResult(networkState: NetworkState<Any>) {
        return when(networkState) {
            is NetworkState.Success<*> -> handleSuccess(networkState.data)
            is NetworkState.HttpErrors.ResourceForbidden -> handleError(networkState.exception)
            is NetworkState.HttpErrors.ResourceNotFound -> handleError(networkState.exception)
            is NetworkState.HttpErrors.InternalServerError -> handleError(networkState.exception)
            is NetworkState.HttpErrors.BadGateWay -> handleError(networkState.exception)
            is NetworkState.HttpErrors.ResourceRemoved -> handleError(networkState.exception)
            is NetworkState.HttpErrors.RemovedResourceFound -> handleError(networkState.exception)
            is NetworkState.InvalidData -> showEmptyView()
            is NetworkState.Error -> handleError(networkState.error)
            is NetworkState.NetworkException -> handleError(networkState.error)
        }
    }

    fun handleError(errorMessage: String?)

    fun <T> handleSuccess(data: T)

    fun showEmptyView()
}