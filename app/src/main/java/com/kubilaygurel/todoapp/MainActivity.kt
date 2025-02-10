package com.kubilaygurel.todoapp

import NoteNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.TodoAppTheme
import com.kubilaygurel.todoapp.presentation.screens.permissionScreen.PermissionScreen
import com.kubilaygurel.todoapp.presentation.screens.permissionScreen.PermissionScreenViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TodoAppTheme {
                val permissionViewModel: PermissionScreenViewModel = hiltViewModel()


                val isPermissionGranted by permissionViewModel.isPermissionGranted.collectAsState()

                LaunchedEffect(isPermissionGranted) {
                    if (!isPermissionGranted) {
                        permissionViewModel.checkNotificationPermission(this@MainActivity)
                    }
                }

                val requestPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    permissionViewModel.updatePermissionStatus(isGranted)
                }

                LaunchedEffect(Unit) {
                    permissionViewModel.requestPermission = {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                if (isPermissionGranted) {
                    NoteNavigation()
                } else {
                    PermissionScreen { permissionViewModel.askForPermission() }
                }
            }
        }
    }
}

