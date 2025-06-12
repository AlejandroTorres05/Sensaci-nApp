// OSMMapView.kt - Actualizado
package com.example.sensazionapp.feature.map.ui.components

import android.content.Context
import android.graphics.Color
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sensazionapp.feature.incidents.data.model.IncidentDTO
import com.example.sensazionapp.feature.incidents.data.model.IncidentSeverity
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    currentLocation: Location? = null,
    incidents: List<IncidentDTO> = emptyList(),
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
            val defaultLocation = GeoPoint(3.4516, -76.5320)
            controller.setCenter(defaultLocation)
        }
    }

    // Overlay para mostrar ubicación del usuario
    val locationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableMyLocation()
            enableFollowLocation()
        }
    }

    // AndroidView para integrar la vista nativa en Compose
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            // Limpiar overlays anteriores excepto el de ubicación
            val overlaysToRemove = map.overlays.filter {
                it !is MyLocationNewOverlay
            }
            map.overlays.removeAll(overlaysToRemove)

            // Agregar overlay de ubicación si no está presente
            if (!map.overlays.contains(locationOverlay)) {
                map.overlays.add(locationOverlay)
            }

            // Actualizar ubicación del usuario
            currentLocation?.let { location ->
                val geoPoint = GeoPoint(location.latitude, location.longitude)

                // Centrar mapa en la nueva ubicación (solo la primera vez)
                if (map.mapCenter.latitude == 3.4516 && map.mapCenter.longitude == -76.5320) {
                    map.controller.animateTo(geoPoint)
                }

                // Crear marcador para ubicación actual
                val userMarker = Marker(map).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Tu ubicación actual"
                    snippet = "Lat: ${String.format("%.6f", location.latitude)}, Lon: ${String.format("%.6f", location.longitude)}"
                    // Personalizar el ícono para diferenciarlo de los incidentes
                    icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
                }
                map.overlays.add(userMarker)
            }

            // Agregar overlays de incidentes
            incidents.forEach { incident ->
                addIncidentOverlay(map, incident, context)
            }

            // Invalidar para redibujar
            map.invalidate()

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

/**
 * Agregar overlay de incidente al mapa
 */
private fun addIncidentOverlay(mapView: MapView, incident: IncidentDTO, context: Context) {
    val center = GeoPoint(incident.latitude, incident.longitude)

    // Crear círculo de radio del incidente
    val circle = Polygon().apply {
        // Crear círculo usando puntos aproximados
        val points = createCirclePoints(center, incident.radius)
        setPoints(points)

        // Configurar colores basados en intensidad
        val (fillColor, strokeColor) = getIncidentColors(incident)
        this.fillPaint.color = fillColor
        this.outlinePaint.color = strokeColor
        this.outlinePaint.strokeWidth = 2f

        // Información del overlay
        title = incident.title
        snippet = """
            ${incident.description ?: ""}
            Severidad: ${getSeverityText(incident.severity)}
            Confirmaciones: ${incident.confirmationCount}
            Negaciones: ${incident.denialCount}
        """.trimIndent()
    }

    mapView.overlays.add(circle)

    // Agregar marcador central
    val marker = Marker(mapView).apply {
        position = center
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        title = incident.title
        snippet = incident.description ?: "Sin descripción"

        // Ícono basado en la categoría
        icon = context.getDrawable(android.R.drawable.ic_dialog_alert)
    }

    mapView.overlays.add(marker)
}

/**
 * Crear puntos para formar un círculo
 */
private fun createCirclePoints(center: GeoPoint, radiusInMeters: Double): List<GeoPoint> {
    val points = mutableListOf<GeoPoint>()
    val numberOfPoints = 32 // Más puntos = círculo más suave

    // Convertir radio a grados aproximadamente
    // 1 grado ≈ 111,000 metros en el ecuador
    val radiusInDegrees = radiusInMeters / 111000.0

    for (i in 0..numberOfPoints) {
        val angle = (i * 360.0 / numberOfPoints) * Math.PI / 180.0
        val lat = center.latitude + radiusInDegrees * Math.cos(angle)
        val lon = center.longitude + radiusInDegrees * Math.sin(angle) / Math.cos(center.latitude * Math.PI / 180.0)
        points.add(GeoPoint(lat, lon))
    }

    return points
}

/**
 * Obtener colores para el incidente basados en intensidad y severidad
 */
private fun getIncidentColors(incident: IncidentDTO): Pair<Int, Int> {
    // Color base según severidad
    val baseColor = when (incident.severity) {
        IncidentSeverity.LOW -> Color.rgb(76, 175, 80)      // Verde
        IncidentSeverity.MEDIUM -> Color.rgb(255, 152, 0)   // Naranja
        IncidentSeverity.HIGH -> Color.rgb(244, 67, 54)     // Rojo
        IncidentSeverity.CRITICAL -> Color.rgb(139, 69, 19) // Rojo oscuro
    }

    // Calcular opacidad basada en intensidad (0-100)
    // intensityLevel viene del backend, aumenta con confirmaciones
    val intensity = incident.intensityLevel.coerceIn(0.0, 100.0)

    // Opacidad mínima 20%, máxima 60% para el relleno
    val fillAlpha = (0.2 + (intensity / 100.0) * 0.4).coerceIn(0.2, 0.6)

    // Opacidad del borde siempre más alta
    val strokeAlpha = (0.6 + (intensity / 100.0) * 0.4).coerceIn(0.6, 1.0)

    val fillColor = Color.argb(
        (fillAlpha * 255).toInt(),
        Color.red(baseColor),
        Color.green(baseColor),
        Color.blue(baseColor)
    )

    val strokeColor = Color.argb(
        (strokeAlpha * 255).toInt(),
        Color.red(baseColor),
        Color.green(baseColor),
        Color.blue(baseColor)
    )

    return Pair(fillColor, strokeColor)
}

/**
 * Obtener texto de severidad
 */
private fun getSeverityText(severity: IncidentSeverity): String {
    return when (severity) {
        IncidentSeverity.LOW -> "Baja"
        IncidentSeverity.MEDIUM -> "Media"
        IncidentSeverity.HIGH -> "Alta"
        IncidentSeverity.CRITICAL -> "Crítica"
    }
}