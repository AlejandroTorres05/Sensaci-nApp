package com.example.sensazionapp.feature.map.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.sensazionapp.feature.map.ui.components.OSMMapView
import com.example.sensazionapp.feature.map.ui.viewModels.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel()
) {
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
            // llamar onLocationPermissionGranted
            mapViewModel.onLocationPermissionGranted()
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
                    onRequestPermission = { locationPermissionState.launchPermissionRequest() },
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
                        errorMessage = uiState.errorMessage,
                        onClearError = { mapViewModel.clearError() }
                    )

                    // Mapa principal
                    OSMMapView(
                        modifier = Modifier.weight(1f),
                        currentLocation = uiState.currentLocation,
                        onMapReady = { mapViewModel.onMapReady() }
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
    errorMessage: String?,
    onClearError: () -> Unit
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
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                errorMessage != null -> {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onClearError,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Cerrar", color = Color.White)
                    }
                }
                isLoadingLocation -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF4287F5)
                    )
                    Text(
                        text = "Obteniendo ubicación...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
                currentLocation != null -> {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Ubicación: ${String.format("%.4f", currentLocation.latitude)}, ${String.format("%.4f", currentLocation.longitude)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50)
                    )
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
    }
}