package com.react___gps.app.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.react___gps.utils.Constants
import com.react___gps.utils.Constants.Companion.INTERNET_CONNECTED_LOG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class CheckInternetConnectionViewModel : ViewModel() {

    private val _networkIsConnected = MutableLiveData<Boolean>()
    val networkIsConnected: LiveData<Boolean> = _networkIsConnected

    private val _isStoopedPingServer = MutableLiveData<Boolean>()
    val isStoopedPingServer: LiveData<Boolean> = _isStoopedPingServer

    private var job: Job? = null
    private var jobForExitValue: Job? = null
    private var exitValue = 0

    private lateinit var process: Process

    fun startNetworkCheckState() {
        job = viewModelScope.launch(Dispatchers.IO) {
            val commandList = ArrayList<String>()
            commandList.add("ping")
            commandList.add(Constants.BASE_URL)
            networkState(commandList)
        }
        _isStoopedPingServer.postValue(false)
        Log.d(INTERNET_CONNECTED_LOG, "Start check internet connection.")
    }

    fun startCheckExitValue() {
        jobForExitValue = viewModelScope.launch(Dispatchers.IO) {
            checkExitValue()
        }
        Log.d(INTERNET_CONNECTED_LOG, "Start check exit value.")
    }

    fun stopCheckExitValue() {
        jobForExitValue?.cancel()
        Log.d(INTERNET_CONNECTED_LOG, "Stop check exit value.")
    }

    fun stopNetworkCheckState() {
        job?.cancel()
        Log.d(INTERNET_CONNECTED_LOG, "Stop check internet connection.")
    }

    private suspend fun networkState(commandList: ArrayList<String>) {
        runCatching {
            while (true) {
                val build = ProcessBuilder(commandList)
                process = build.start()
                val input = BufferedReader(InputStreamReader(process.inputStream))
                var s: String?
                if (input.readLine().also { s = it } != null) {
                    exitValue = 0
                    _networkIsConnected.postValue(true)
                    Log.d(INTERNET_CONNECTED_LOG, "$s")
                } else {
                    exitValue = 1
                    _networkIsConnected.postValue(false)
                }
                delay(10000)
            }
        }
    }

    private suspend fun checkExitValue() {
        while (true) {
            if (exitValue != 0) {
                delay(60000)
                if (exitValue != 0) {
                    stopNetworkCheckState()
                    _isStoopedPingServer.postValue(true)
                    break
                }
            }
            delay(10000)
        }
    }
}