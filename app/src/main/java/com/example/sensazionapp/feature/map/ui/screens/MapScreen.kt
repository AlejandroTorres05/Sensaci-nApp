package com.example.sensazionapp.feature.map.ui.screens

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.map.ui.components.OSMMapView
import com.example.sensazionapp.feature.map.ui.viewModels.MapViewModel
import com.example.sensazionapp.feature.map.ui.viewModels.MapViewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    authViewModel: AuthViewModel
) {
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(authViewModel)
    )
    val context = LocalContext.current
    val uiState by mapViewModel.uiState.collectAsState()

    // Manejar permisos de ubicación
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (isGranted) {
            mapViewModel.onLocationPermissionGranted()
        } else {
            mapViewModel.onLocationPermissionDenied()
        }
    }

    // Inicializar servicios de ubicación
    LaunchedEffect(Unit) {
        mapViewModel.initializeLocationServices(context)
    }

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted) {
            mapViewModel.onLocationPermissionGranted()
        }
    }

    // Log de estados para debugging
    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let { location ->
        }
    }

    LaunchedEffect(uiState.isLocationSharingActive) {
        Log.d("MapScreen", "Nuevo estado: ${uiState.isLocationSharingActive}")
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Log.e("MapScreen", "Error: $error")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        when {
            // Permisos no concedidos
            !locationPermissionState.status.isGranted -> {
                PermissionRequestScreen(
                    onRequestPermission = {
                        Log.d("MapScreen", "Usuario solicitando permisos")
                        locationPermissionState.launchPermissionRequest()
                    },
                    shouldShowRationale = locationPermissionState.status.shouldShowRationale
                )
            }

            // Permisos concedidos
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header con información de estado
                    MapStatusHeader(
                        isLoadingLocation = uiState.isLoadingLocation,
                        currentLocation = uiState.currentLocation,
                        isLocationSharingActive = uiState.isLocationSharingActive,
                        errorMessage = uiState.errorMessage,
                        onClearError = { mapViewModel.clearError() },
                        onToggleLocationSharing = {
                            mapViewModel.toggleLocationSharing()
                        }
                    )

                    // Mapa principal
                    OSMMapView(
                        modifier = Modifier.weight(1f),
                        currentLocation = uiState.currentLocation,
                        onMapReady = {
                            mapViewModel.onMapReady()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestScreen(
    onRequestPermission: () -> Unit,
    shouldShowRationale: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(Color(0xFFFF9800).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFFFF9800)
                    )
                }

                Box(modifier = Modifier.padding(16.dp))

                Text(
                    text = "Permiso de Ubicación",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF333333)
                )

                Box(modifier = Modifier.padding(8.dp))

                Text(
                    text = if (shouldShowRationale) {
                        "SensaziónApp necesita acceso a tu ubicación para mostrarte el mapa de seguridad y reportes cercanos. Por favor, concede el permiso en la configuración."
                    } else {
                        "Para mostrarte el mapa interactivo y reportes de seguridad cerca de ti, necesitamos acceso a tu ubicación."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Box(modifier = Modifier.padding(16.dp))

                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4287F5)
                    )
                ) {
                    Text(
                        text = "Conceder Permiso",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MapStatusHeader(
    isLoadingLocation: Boolean,
    currentLocation: android.location.Location?,
    isLocationSharingActive: Boolean,
    errorMessage: String?,
    onClearError: () -> Unit,
    onToggleLocationSharing: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                errorMessage != null -> Color(0xFFFFEBEE)
                isLoadingLocation -> Color(0xFFF3E5F5)
                currentLocation != null -> Color(0xFFE8F5E8)
                else -> Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Primera fila: Estado principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    errorMessage != null -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Error de ubicación",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Button(
                            onClick = onClearError,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.size(width = 60.dp, height = 32.dp)
                        ) {
                            Text("OK", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    isLoadingLocation -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF4287F5)
                            )
                            Text(
                                text = "Obteniendo ubicación...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                    currentLocation != null -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Ubicación",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Ubicación obtenida",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // BOTÓN IMPORTANTE: Toggle location sharing - AHORA SIEMPRE VISIBLE
                        Button(
                            onClick = onToggleLocationSharing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLocationSharingActive) Color(0xFF4CAF50) else Color(0xFF2196F3)
                            ),
                            modifier = Modifier.size(width = 100.dp, height = 32.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = if (isLocationSharingActive) "Detener envío" else "Iniciar envío",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isLocationSharingActive) "ON" else "OFF",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "Mapa de Seguridad",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF333333)
                        )
                    }
                }
            }

            // Segunda fila: Información detallada
            if (currentLocation != null && errorMessage == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lat: ${String.format("%.4f", currentLocation.latitude)}, Lon: ${String.format("%.4f", currentLocation.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )

                    Text(
                        text = if (isLocationSharingActive) "● ENVIANDO AL SERVIDOR" else "● NO ENVIANDO",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isLocationSharingActive) Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                }
            }

            // Mostrar mensaje de error si existe
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F),
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }
}