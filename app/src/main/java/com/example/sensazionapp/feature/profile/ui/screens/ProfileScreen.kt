package com.example.sensazionapp.feature.profile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel.AuthState
import android.app.Activity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.sensazionapp.util.getActivity
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Row

@Composable
fun ProfileScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    isInMainScreen: Boolean = false // Flag para saber si está dentro de MainScreen
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val context = LocalContext.current
    val activity: Activity? = remember(context) {
        context.getActivity()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Initial -> {
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            }
            is AuthState.ProfileIncomplete -> {
                navController.navigate("complete_profile") {
                    popUpTo("profile") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    // Content sin Scaffold cuando está en MainScreen, con Scaffold cuando está solo
    if (isInMainScreen) {
        ProfileContent(
            authState = authState,
            currentUser = currentUser,
            activity = activity,
            authViewModel = authViewModel
        )
    } else {
        // Mantener comportamiento original para compatibilidad
        androidx.compose.material3.Scaffold { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                ProfileContent(
                    authState = authState,
                    currentUser = currentUser,
                    activity = activity,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    authState: AuthState,
    currentUser: com.example.sensazionapp.domain.model.User?,
    activity: Activity?,
    authViewModel: AuthViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer superior
            Box(modifier = Modifier.height(40.dp))

            // Header card con información del usuario
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar del usuario
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFF4287F5).copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF4287F5)
                        )
                    }

                    Box(modifier = Modifier.height(16.dp))

                    when (authState) {
                        is AuthState.Authenticated -> {
                            currentUser?.let { user ->
                                Text(
                                    text = "Mi Perfil",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )

                                // Manejo seguro de nombres
                                val fullName = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
                                Text(
                                    text = if (fullName.isNotEmpty()) fullName else "Usuario",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF333333)
                                )

                                Box(modifier = Modifier.height(16.dp))

                                // Información del usuario
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.Gray
                                    )
                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                // Verificación segura de teléfono
                                user.phone?.let { phone ->
                                    if (phone.isNotBlank()) {
                                        Box(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = "Teléfono",
                                                modifier = Modifier.size(18.dp),
                                                tint = Color.Gray
                                            )
                                            Text(
                                                text = phone,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }

                                // Mostrar estadísticas del usuario
                                Box(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${user.totalIncidentsReported}",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4287F5)
                                            )
                                        )
                                        Text(
                                            text = "Reportes",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${user.totalConfirmations}",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF4CAF50)
                                            )
                                        )
                                        Text(
                                            text = "Confirmaciones",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = String.format("%.1f", user.verificationScore),
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFF9800)
                                            )
                                        )
                                        Text(
                                            text = "Score",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }

                            } ?: run {
                                Text(
                                    text = "Usuario",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF333333)
                                )
                            }
                        }
                        is AuthState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color(0xFF4287F5),
                                strokeWidth = 4.dp
                            )
                            Box(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cargando...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        else -> {
                            Text(
                                text = "Usuario",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.height(40.dp))

            // Botón de cerrar sesión
            Button(
                onClick = {
                    activity?.let { authViewModel.logout(it) }
                },
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5252)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    vertical = 16.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Cerrar sesión",
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Cerrar Sesión",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Box(modifier = Modifier.height(40.dp))
        }
    }
}