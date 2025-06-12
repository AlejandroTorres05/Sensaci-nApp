// MapScreen.kt - Actualizado con sistema de reportes
package com.example.sensazionapp.feature.map.ui.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.map.ui.viewModels.MapViewModel
import com.example.sensazionapp.feature.map.ui.viewModels.MapViewModelFactory
import com.example.sensazionapp.feature.map.ui.components.OSMMapView
import com.example.sensazionapp.feature.incidents.ui.viewmodel.IncidentViewModel
import com.example.sensazionapp.feature.incidents.ui.viewmodel.IncidentViewModelFactory
import com.example.sensazionapp.feature.incidents.ui.screen.IncidentReportDialog
import com.example.sensazionapp.feature.incidents.data.repository.IncidentRepository
import com.example.sensazionapp.util.NetworkUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    authViewModel: AuthViewModel,
    showReportDialog: Boolean = false
) {
    val context = LocalContext.current

    // ViewModels
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(authViewModel)
    )

    // Crear IncidentViewModel
    val incidentRepository = remember {
        val apiService = NetworkUtil.createApiService { authViewModel.getAccessToken() }
        IncidentRepository(apiService)
    }

    val incidentViewModel: IncidentViewModel = viewModel(
        factory = IncidentViewModelFactory(incidentRepository)
    )

    // Estados
    val mapUiState by mapViewModel.uiState.collectAsState()
    val nearbyIncidents by incidentViewModel.nearbyIncidents.collectAsState()
    val isLoadingIncidents by incidentViewModel.isLoadingIncidents.collectAsState()
    var showReportDialogState by remember { mutableStateOf(showReportDialog) }

    Log.d("MapScreen", "=== MAPSCREEN RECOMPOSED ===")
    Log.d("MapScreen", "UiState: isLoading=${mapUiState.isLoadingLocation}, hasLocation=${mapUiState.currentLocation != null}, isSharing=${mapUiState.isLocationSharingActive}")

    // Launcher para permisos de ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MapScreen", "=== PERMISO CONCEDIDO ===")
            mapViewModel.onLocationPermissionGranted()
        } else {
            Log.d("MapScreen", "=== PERMISO DENEGADO ===")
            mapViewModel.onLocationPermissionDenied()
        }
    }

    // Inicializar servicios de ubicación
    LaunchedEffect(Unit) {
        Log.d("MapScreen", "=== LAUNCHED EFFECT INICIALIZACIÓN ===")
        mapViewModel.initializeLocationServices(context)
    }

    // Observar cambios en los permisos
    LaunchedEffect(mapUiState.isLocationPermissionGranted) {
        Log.d("MapScreen", "=== LAUNCHED EFFECT PERMISO CAMBIÓ ===")
        Log.d("MapScreen", "Nuevo estado permiso: ${mapUiState.isLocationPermissionGranted}")
    }

    // Observar cambios en el estado de sharing
    LaunchedEffect(mapUiState.isLocationSharingActive) {
        Log.d("MapScreen", "=== ESTADO SHARING CAMBIÓ ===")
        Log.d("MapScreen", "Nuevo estado: ${mapUiState.isLocationSharingActive}")
    }

    // Cargar incidentes cercanos cuando cambie la ubicación
    LaunchedEffect(mapUiState.currentLocation) {
        mapUiState.currentLocation?.let { location ->
            Log.d("MapScreen", "=== NUEVA UBICACIÓN EN UI ===")
            Log.d("MapScreen", "Lat: ${location.latitude}, Lon: ${location.longitude}")

            // Cargar incidentes cercanos
            incidentViewModel.loadNearbyIncidents(location.latitude, location.longitude)
        }
    }

    // Limpiar error después de un tiempo
    LaunchedEffect(mapUiState.errorMessage) {
        mapUiState.errorMessage?.let {
            kotlinx.coroutines.delay(5000)
            mapViewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (mapUiState.isLocationPermissionGranted) {
            Log.d("MapScreen", "Permisos concedidos, mostrando mapa")

            // Mapa con incidentes
            OSMMapView(
                currentLocation = mapUiState.currentLocation,
                incidents = nearbyIncidents,
                onMapReady = {
                    Log.d("MapScreen", "Mapa reporta que está listo")
                    mapViewModel.onMapReady()
                }
            )

            // Botón flotante para crear reporte
            FloatingActionButton(
                onClick = { showReportDialogState = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color(0xFFE53E3E),
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear Reporte"
                )
            }

            // Panel de control superior
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Compartir Ubicación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (mapUiState.isLocationSharingActive) "ACTIVO" else "INACTIVO",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (mapUiState.isLocationSharingActive) Color(0xFF4CAF50) else Color(0xFFFF5722),
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = { mapViewModel.toggleLocationSharing() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (mapUiState.isLocationSharingActive) Color(0xFFFF5722) else Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (mapUiState.isLocationSharingActive) "OFF" else "ON")
                        }
                    }

                    // Mostrar información de incidentes
                    if (nearbyIncidents.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Incidentes cercanos: ${nearbyIncidents.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF5722),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Indicador de carga de incidentes
            if (isLoadingIncidents) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cargando incidentes...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

        } else {
            // Vista cuando no hay permisos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Permisos de Ubicación Requeridos",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Para usar el mapa y ver incidentes cercanos, necesitamos acceso a tu ubicación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                ) {
                    Text("Conceder Permisos")
                }
            }
        }

        // Mostrar errores
        mapUiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Modal de reporte
    IncidentReportDialog(
        isVisible = showReportDialogState,
        currentLocation = mapUiState.currentLocation,
        incidentViewModel = incidentViewModel,
        onDismiss = {
            showReportDialogState = false
            incidentViewModel.clearForm()
        }
    )
}