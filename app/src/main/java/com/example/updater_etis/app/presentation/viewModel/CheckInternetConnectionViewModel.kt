package com.example.updater_etis.app.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.updater_etis.utils.Constants
import com.example.updater_etis.utils.Constants.Companion.INTERNET_CONNECTED_LOG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class CheckInternetConnectionViewModel : ViewModel() {

    private val _networkIsConnected = MutableLiveData<Boolean>()
    val networkIsConnected: LiveData<Boolean> = _networkIsConnected

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
        Log.d(INTERNET_CONNECTED_LOG, "start check internet connection")
    }


    fun startCheckExitValue() {
        jobForExitValue = viewModelScope.launch(Dispatchers.IO) {
            checkExitValue()
        }
        Log.d(INTERNET_CONNECTED_LOG, "start check exit value")
    }

    fun stopCheckExitValue() {
        jobForExitValue?.cancel()
        Log.d(INTERNET_CONNECTED_LOG, "stop check exit value")
    }

    fun stopNetworkCheckState() {
        job?.cancel()
        Log.d(INTERNET_CONNECTED_LOG, "stop check internet connection")
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
                    Log.d(INTERNET_CONNECTED_LOG, "ok - $s")
                } else {
                    exitValue = 1
                    _networkIsConnected.postValue(false)
                }
                delay(5000)
            }
        }
    }

    private suspend fun checkExitValue() {
        while (true) {
            Log.d(INTERNET_CONNECTED_LOG, "!!!!!!!!!!")
            if (exitValue != 0) {
                delay(30000)
                if (exitValue != 0) {
                    Log.d(INTERNET_CONNECTED_LOG, "@@@@@@@@@@@@@@@@@@@")
                    stopNetworkCheckState()
                    //delay
                    //flag true
                    break
                }
            }
            delay(5000)
        }
    }
}