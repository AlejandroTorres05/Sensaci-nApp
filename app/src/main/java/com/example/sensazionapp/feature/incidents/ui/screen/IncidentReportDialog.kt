// IncidentReportScreen.kt
package com.example.sensazionapp.feature.incidents.ui.screen

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.sensazionapp.feature.incidents.data.model.*
import com.example.sensazionapp.feature.incidents.ui.viewmodel.IncidentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentReportDialog(
    isVisible: Boolean,
    currentLocation: Location?,
    incidentViewModel: IncidentViewModel,
    onDismiss: () -> Unit
) {
    val formState by incidentViewModel.formState.collectAsState()
    val reportResult by incidentViewModel.reportResult.collectAsState()

    // Manejar el resultado del reporte
    LaunchedEffect(reportResult) {
        if (reportResult is IncidentResult.Success) {
            // Esperar un poco para mostrar el éxito y luego cerrar
            kotlinx.coroutines.delay(1500)
            incidentViewModel.clearReportResult()
            onDismiss()
        }
    }

    if (isVisible) {
        Dialog(
            onDismissRequest = {
                if (!formState.isLoading) {
                    onDismiss()
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = !formState.isLoading,
                dismissOnClickOutside = !formState.isLoading
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Crear Reporte",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (!formState.isLoading) {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar ubicación actual
                    currentLocation?.let { location ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = "Ubicación del reporte:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de título
                    OutlinedTextField(
                        value = formState.title,
                        onValueChange = incidentViewModel::updateTitle,
                        label = { Text("Título del reporte *") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !formState.isLoading,
                        isError = formState.error?.contains("título") == true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo de descripción
                    OutlinedTextField(
                        value = formState.description,
                        onValueChange = incidentViewModel::updateDescription,
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !formState.isLoading,
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de categoría
                    Text(
                        text = "Categoría:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IncidentCategory.values().forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = formState.category == category,
                                    onClick = { incidentViewModel.updateCategory(category) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = formState.category == category,
                                onClick = null,
                                enabled = !formState.isLoading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = getCategoryDisplayName(category),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de severidad
                    Text(
                        text = "Severidad:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    IncidentSeverity.values().forEach { severity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = formState.severity == severity,
                                    onClick = { incidentViewModel.updateSeverity(severity) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = formState.severity == severity,
                                onClick = null,
                                enabled = !formState.isLoading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = getSeverityDisplayName(severity),
                                style = MaterialTheme.typography.bodyMedium,
                                color = getSeverityColor(severity)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mostrar error si existe
                    formState.error?.let { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Mostrar éxito
                    if (reportResult is IncidentResult.Success) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(
                                text = "¡Reporte creado exitosamente!",
                                modifier = Modifier.padding(12.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            enabled = !formState.isLoading
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = { incidentViewModel.createIncident(currentLocation) },
                            enabled = !formState.isLoading && formState.title.isNotBlank() && currentLocation != null
                        ) {
                            if (formState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creando...")
                            } else {
                                Text("Crear Reporte")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Funciones helper para mostrar nombres amigables
 */
@Composable
private fun getCategoryDisplayName(category: IncidentCategory): String {
    return when (category) {
        IncidentCategory.ACCIDENT -> "Accidente"
        IncidentCategory.CRIME -> "Crimen/Delito"
        IncidentCategory.EMERGENCY -> "Emergencia"
        IncidentCategory.NATURAL_DISASTER -> "Desastre Natural"
        IncidentCategory.INFRASTRUCTURE -> "Infraestructura"
        IncidentCategory.OTHER -> "Otro"
    }
}

@Composable
private fun getSeverityDisplayName(severity: IncidentSeverity): String {
    return when (severity) {
        IncidentSeverity.LOW -> "Baja"
        IncidentSeverity.MEDIUM -> "Media"
        IncidentSeverity.HIGH -> "Alta"
        IncidentSeverity.CRITICAL -> "Crítica"
    }
}

@Composable
private fun getSeverityColor(severity: IncidentSeverity): Color {
    return when (severity) {
        IncidentSeverity.LOW -> Color(0xFF4CAF50)     // Verde
        IncidentSeverity.MEDIUM -> Color(0xFFFF9800)  // Naranja
        IncidentSeverity.HIGH -> Color(0xFFFF5722)    // Rojo
        IncidentSeverity.CRITICAL -> Color(0xFFD32F2F) // Rojo oscuro
    }
}