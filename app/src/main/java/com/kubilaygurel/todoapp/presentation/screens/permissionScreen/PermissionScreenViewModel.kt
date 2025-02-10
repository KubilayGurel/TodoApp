package com.kubilaygurel.todoapp.presentation.screens.permissionScreen



import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionScreenViewModel @Inject constructor() : ViewModel() {


    private val _isPermissionGranted = MutableStateFlow(false)
    val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted

    var requestPermission: (() -> Unit)? = null

    fun checkNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            _isPermissionGranted.value = isGranted
        } else {
            _isPermissionGranted.value = true
        }
    }

    fun askForPermission() {
        requestPermission?.invoke()
    }

    fun updatePermissionStatus(isGranted: Boolean) {
        _isPermissionGranted.value = isGranted
    }
}