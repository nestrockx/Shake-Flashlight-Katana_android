package com.wegielek.katanaflashlight.presentation.ui.views.screen

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.wegielek.katanaflashlight.R
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaBackground
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaIconButton
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaImageButton
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaInstructionDialog
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaSlider
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaSwitch
import com.wegielek.katanaflashlight.presentation.ui.views.katana.KatanaTextButton
import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LandingScreen(
    viewModel: LandingViewModel = koinViewModel(),
    navigateToAbout: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    val oldAndroidInit by viewModel.olderAndroidInit.collectAsState()
    val oldAndroidClicked by viewModel.olderAndroidClicked.collectAsState()

    val configuration = LocalConfiguration.current
    val hasCameraPermission by viewModel.hasCameraPermission.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()
    val padding =
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            96.dp
        } else {
            32.dp
        }

    var cameraRequested by remember { mutableStateOf(false) }
    val cameraLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            viewModel.updatePermissions()
            if (isGranted) {
                viewModel.startService()
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ cameraRequested = true }, 100)
            }
        }

    var notificationRequested by remember { mutableStateOf(false) }
    val notificationLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            viewModel.updatePermissions()
            if (isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                } else {
                    viewModel.startService()
                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({ notificationRequested = true }, 100)
                }
            }
        }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_STOP) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                        viewModel.stopService()
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }

    LaunchedEffect(state) {
        viewModel.saveState(context)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        KatanaBackground()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (cameraRequested) {
                KatanaInstructionDialog(
                    instructionExpired = state.instructionExpired,
                    onInit = { viewModel.startService() },
                    onConfirm = { viewModel.setInstructionExpired(true) },
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (notificationRequested) {
                KatanaInstructionDialog(
                    instructionExpired = state.instructionExpired,
                    onInit = { viewModel.startService() },
                    onConfirm = { viewModel.setInstructionExpired(true) },
                )
            }
        } else if (oldAndroidClicked) {
            KatanaInstructionDialog(
                instructionExpired = state.instructionExpired,
                onInit = { viewModel.startService() },
                onConfirm = { viewModel.setInstructionExpired(true) },
            )
        } else if (!state.instructionExpired) {
            viewModel.setOldAndroidInit(true)
        }
        if (!oldAndroidInit) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                KatanaIconButton(
                    imageVector = Icons.Rounded.Menu,
                    contentDescription = stringResource(R.string.menu_icon),
                ) { navigateToAbout() }
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = padding),
                    ) {
                        Spacer(modifier = Modifier.size(16.dp))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            if (!hasNotificationPermission || !hasCameraPermission) {
                                KatanaTextButton(stringResource(R.string.allow_notification))
                                {
                                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (!hasNotificationPermission) {
                                KatanaTextButton(stringResource(R.string.allow_notification))
                                {
                                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        }

                        if (state.hasStrengthLevels) {
                            KatanaSlider(
                                stringResource(R.string.light_strength),
                                state.strength.toFloat(),
                                1f..state.maxStrength.toFloat(),
                                (state.maxStrength - 2).coerceAtLeast(0),
                            ) {
                                viewModel.onStrengthChange(it.toInt())
                            }
                        }

                        Spacer(Modifier.padding(4.dp))
                        KatanaSwitch(
                            stringResource(R.string.on_off),
                            state.katanaServiceRunning,
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                                hasCameraPermission &&
                                hasNotificationPermission
                            ) {
                                viewModel.onKatanaServiceSwitch(it)
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                hasNotificationPermission
                            ) {
                                viewModel.onKatanaServiceSwitch(it)
                            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                viewModel.onKatanaServiceSwitch(it)
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        context.getString(R.string.lack_permissions),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                        }
                        Spacer(Modifier.padding(4.dp))
                        KatanaSwitch(
                            stringResource(R.string.vibrations),
                            state.vibrationEnabled,
                        ) {
                            viewModel.onVibrationSwitch(it)
                        }
                        Spacer(Modifier.padding(4.dp))
                        KatanaSlider(
                            stringResource(R.string.slash_sensitivity),
                            state.sensitivity,
                            0f..10f,
                            9,
                        ) {
                            viewModel.onSensitivityChange(it)
                        }
                        Spacer(Modifier.padding(4.dp))
                        KatanaImageButton(
                            painterResource(R.drawable.ic_katana_with_handle),
                            contentDescription = stringResource(R.string.flash_button),
                        ) {
                            viewModel.toggleFlashlight()
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = { viewModel.clickStart() },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.start),
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}
