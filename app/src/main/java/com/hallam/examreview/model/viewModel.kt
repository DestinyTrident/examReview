package com.hallam.examreview.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.collectLatest

class viewModel: ViewModel() {
    private val _connectionStatus = MutableLiveData<Boolean>()
            val connectionStatus: LiveData<Boolean> = _connectionStatus

    suspend fun testConnection(ctx: Context){
        var connect: Boolean
        val observer = AndroidConnectivityObserver(ctx)
        observer.isConnected.collectLatest{value->_connectionStatus.value = value}
    }
}