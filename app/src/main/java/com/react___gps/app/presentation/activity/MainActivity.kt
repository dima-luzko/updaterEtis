package com.react___gps.app.presentation.activity


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageInfo
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
import com.react___gps.R
import com.react___gps.databinding.ActivityMainBinding
import com.react___gps.app.domain.repository.ApplicationRepository
import com.react___gps.app.presentation.viewModel.CheckInternetConnectionViewModel
import com.react___gps.utils.*
import kotlinx.coroutines.*
import net.codecision.startask.permissions.Permission
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pInfo: PackageInfo
    private var appETISVersion = 0
    private val checkInternetConnectionViewModel by viewModel<CheckInternetConnectionViewModel>()
    private val permission: Permission by lazy {
        Permission.Builder(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .setRequestCode(Constants.REQUEST_PERMISSION_CODE)
            .build()
    }
    private var isBackPressed = false
    private val applicationRepository: ApplicationRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideSystemUI()
        checkInternetConnection()
        checkPermission()
        checkIsStoopedPingServer()
        if (isAppInstalled(this, Constants.OLD_UPDATER_PACKAGE_NAME)) {
            deletePackage()
        }
        exitInAppOnLongPress()
    }

    private fun exitInAppOnLongPress() {
        binding.root.setOnLongClickListener {
            exitProcess(0)
        }
    }

    private fun writeETISVersion() {
        runCatching {
            pInfo = packageManager.getPackageInfo(Constants.APP_ETIS_PACKAGE_NAME, 0)
        }.onSuccess {
            appETISVersion = convertAppVersionToInt(pInfo.versionName)
        }.onFailure {
            appETISVersion = 0
        }
    }

    private fun equalsETISVersion() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                val applicationInfo = applicationRepository.getApplicationInfo()
                val requestVersion = convertAppVersionToInt(applicationInfo.version)
                val dirPath = application.filesDir.absolutePath + "/"
                if (requestVersion > appETISVersion) {
                    DownloadHelper.downloadApk(
                        context = this@MainActivity,
                        url = applicationInfo.appUrl,
                        dirPath = dirPath,
                        fileName = getAppNameFromUrl(applicationInfo.appUrl),
                        appName = getAppNameFromUrl(applicationInfo.appUrl),
                        viewModel = checkInternetConnectionViewModel
                    )
                    delay(5000)
                    withContext(Dispatchers.Main) {
                        changeTextColor()
                    }

                } else {
                    Log.d(
                        Constants.APP_INSTALL_LOG,
                        "Update is not required. Current app version - ${applicationInfo.version}"
                    )
                    openApp(context = this@MainActivity)
                }
            }.onFailure {
                Log.e(Constants.INTERNET_CONNECTED_LOG, "Server error: $it")
                delay(Constants.REQUEST_EXCEPTION_TIMEOUT)
                checkInstallAppETIS()
            }
        }
    }

    private fun changeTextColor() {
        if (isApkInstalledFailed) {
            binding.textLoading?.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.text_loading_orange
                )
            )
        }
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
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(Constants.PERMISSION_LOG, "Permission granted.")
            writeETISVersion()
            startPingServer()
        } else {
            Log.d(Constants.PERMISSION_LOG, "Permission denied.")
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
        with(checkInternetConnectionViewModel) {
            networkIsConnected.observe(this@MainActivity) {
                with(binding) {
                    if (it) {
                        Log.d(Constants.INTERNET_CONNECTED_LOG, "Internet Connected.")
                        textLoading?.isVisible = true
                        container?.isVisible = true
                        lottieInternetError?.isVisible = false
                        stopPingServer()
                        equalsETISVersion()
                    } else {
                        Log.d(Constants.INTERNET_CONNECTED_LOG, "Internet no connected.")
                        textLoading?.isVisible = false
                        container?.isVisible = false
                        lottieInternetError?.isVisible = true
                    }
                }
            }
        }
    }

    private fun checkInstallAppETIS() {
        if (isAppInstalled(
                context = this@MainActivity,
                packageName = Constants.APP_ETIS_PACKAGE_NAME
            )
        ) {
            openApp(context = this@MainActivity)
        } else {
            startPingServer()
        }
    }

    private fun checkIsStoopedPingServer() {
        checkInternetConnectionViewModel.isStoopedPingServer.observe(this) { isStoopedPingServer ->
            if (isStoopedPingServer) {
                checkInstallAppETIS()
            }
        }
    }

    private fun startPingServer() {
        with(checkInternetConnectionViewModel) {
            startNetworkCheckState()
            startCheckExitValue()
        }
    }

    private fun stopPingServer() {
        with(checkInternetConnectionViewModel) {
            stopNetworkCheckState()
            stopCheckExitValue()
        }
    }

    override fun onStop() {
        super.onStop()
        stopPingServer()
        exitInApp()
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