package com.example.updater_etis.app.presentation.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.updater_etis.R
import com.example.updater_etis.app.presentation.viewModel.CheckInternetConnectionViewModel
import com.example.updater_etis.databinding.ActivityMainBinding
import com.example.updater_etis.framework.remote.RemoteDataSource
import com.example.updater_etis.utils.Constants
import com.example.updater_etis.utils.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.codecision.startask.permissions.Permission
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val checkInternetConnectionViewModel by viewModel<CheckInternetConnectionViewModel>()
    private val permission: Permission by lazy {
        Permission.Builder(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .setRequestCode(Constants.REQUEST_PERMISSION_CODE)
            .build()
    }
    private var isBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        checkInternetConnection()
        checkPermission()
    }


    private fun checkPermission() {
        permission.check(this)
            .onShowRationale {
                showPermissionDialog()
            }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && PreferencesManager.getInstance(this)
                .getBoolean(PreferencesManager.PREF_NETWORK_IS_ACTIVE, false)
        ) {
            Log.d("LOX", "YES")
            CoroutineScope(Dispatchers.IO).launch {
                val a = RemoteDataSource.retrofit.getApplication()
                Log.d("LOX", "${a.map { it.name }}")
            }
        } else {
            Log.d("LOX", "NO")
            if (isBackPressed) {
                showPermissionDialog()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        isBackPressed = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permission.onRequestPermissionsResult(this, requestCode, grantResults)
            .onDenied {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Constants.REQUEST_PERMISSION_CODE
                    )
                }
            }.onNeverAskAgain {
                showPermissionDialog()
            }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_title))
            .setMessage(getString(R.string.permission_message))
            .setIcon(R.drawable.icon_folder)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.setting)) { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                this.startActivity(intent)
                isBackPressed = true
                dialog.dismiss()
            }
            .show()
    }

    private fun checkInternetConnection() {
        checkInternetConnectionViewModel.networkIsConnected.observe(this) {
            with(binding) {
                if (it) {
                    Log.d(Constants.INTERNET_CONNECTED_LOG, "Internet Connected")
                    textLoading?.isVisible = true
                    container?.isVisible = true
                    lottieInternetError?.isVisible = false
                } else {
                    Log.d(Constants.INTERNET_CONNECTED_LOG, "Internet no connected")
                    textLoading?.isVisible = false
                    container?.isVisible = false
                    lottieInternetError?.isVisible = true
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkInternetConnectionViewModel.startNetworkCheckState()
    }

    override fun onStop() {
        super.onStop()
        checkInternetConnectionViewModel.stopNetworkCheckState()
    }


    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
            window.decorView.apply {
                systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }
}