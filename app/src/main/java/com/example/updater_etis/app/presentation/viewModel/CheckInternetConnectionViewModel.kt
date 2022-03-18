package com.example.updater_etis.app.presentation.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.updater_etis.utils.Constants
import com.example.updater_etis.utils.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CheckInternetConnectionViewModel(val context: Context) : ViewModel() {

    private val _networkIsConnected = MutableLiveData<Boolean>()
    val networkIsConnected: LiveData<Boolean> = _networkIsConnected

    private var job: Job? = null

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
    var exitValue = 0

    private suspend fun networkState() {
        while (true) {
//            val connectivityManager =
//                getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
//            val activeNetworkInfo = connectivityManager.activeNetworkInfo
//            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting) {
//                _networkIsConnected.postValue(true)
//                PreferencesManager.getInstance(context)
//                    .putBoolean(PreferencesManager.PREF_NETWORK_IS_ACTIVE, true)
//            } else {
//                PreferencesManager.getInstance(context)
//                    .putBoolean(PreferencesManager.PREF_NETWORK_IS_ACTIVE, false)
//                _networkIsConnected.postValue(false)
//            }
            val runtime = Runtime.getRuntime()
            runCatching {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 tc.by")
                exitValue = ipProcess.waitFor()
            }.onSuccess {
                _networkIsConnected.postValue(exitValue == 0)
            }.onFailure {
                _networkIsConnected.postValue(false)
            }

            delay(5000)
        }

    }
}