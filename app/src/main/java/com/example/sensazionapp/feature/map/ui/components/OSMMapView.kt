package com.example.sensazionapp.feature.map.ui.components

import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    currentLocation: Location? = null,
    onMapReady: () -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = "com.example.sensazionapp"
            // Configurar cache en directorio interno de la app
            osmdroidBasePath = context.filesDir
            osmdroidTileCache = context.filesDir
        }
    }

    val mapView = remember {
        MapView(context).apply {
            // Configuración básica del mapa
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)

            // Ubicación por defecto (Cali, Colombia)
            val defaultLocation = GeoPoint(3.4516, -76.5320) // Cali, Valle del Cauca
            controller.setCenter(defaultLocation)
        }
    }

    // Overlay para mostrar ubicación del usuario
    val locationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableMyLocation() // Habilitar seguimiento de ubicación
            enableFollowLocation() // Seguir automáticamente la ubicación del usuario
        }
    }

    // AndroidView para integrar la vista nativa en Compose
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            // Agregar overlay de ubicación si no está presente
            if (!map.overlays.contains(locationOverlay)) {
                map.overlays.add(locationOverlay)
            }

            // Actualizar ubicación cuando cambie
            currentLocation?.let { location ->
                val geoPoint = GeoPoint(location.latitude, location.longitude)

                // Centrar mapa en la nueva ubicación
                map.controller.animateTo(geoPoint)

                // Crear marcador personalizado para ubicación actual
                val marker = Marker(map).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Tu ubicación actual"
                    snippet = "Lat: ${String.format("%.6f", location.latitude)}, Lon: ${String.format("%.6f", location.longitude)}"
                }

                // Limpiar marcadores anteriores y agregar el nuevo
                map.overlays.removeAll { it is Marker }
                map.overlays.add(marker)

                // Invalidar para redibujar
                map.invalidate()
            }

            // Notificar que el mapa está listo
            onMapReady()
        }
    )

    // Cleanup cuando el Composable se destruye
    DisposableEffect(mapView) {
        onDispose {
            mapView.onDetach()
        }
    }
}