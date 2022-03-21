package com.example.updater_etis.app.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.updater_etis.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CheckInternetConnectionViewModel() : ViewModel() {

    private val _networkIsConnected = MutableLiveData<Boolean>()
    val networkIsConnected: LiveData<Boolean> = _networkIsConnected

    private var job: Job? = null
    private var exitValue = 0

    fun startNetworkCheckState() {
        job = viewModelScope.launch(Dispatchers.IO) {
            networkState()
        }
        Log.d(Constants.INTERNET_CONNECTED_LOG, "start check internet connection")
    }

    fun stopNetworkCheckState() {
        job?.cancel()
        Log.d(Constants.INTERNET_CONNECTED_LOG, "stop check internet connection")
    }

    private suspend fun networkState() {
        while (true) {
            val runtime = Runtime.getRuntime()
            runCatching {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 ${Constants.BASE_URL}")
                exitValue = ipProcess.waitFor()
                _networkIsConnected.postValue(exitValue == 0)
            }
            delay(5000)
        }
    }
}