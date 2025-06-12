package com.example.sensazionapp.feature.auth.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel.AuthState
import com.example.sensazionapp.util.getActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip

@Composable
fun SignInScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current
    val activity: Activity? = remember(context) {
        context.getActivity()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("profile") {
                    popUpTo("signIn") { inclusive = true }
                }
            }
            is AuthState.ProfileIncomplete -> {
                navController.navigate("complete_profile") {
                    popUpTo("signIn") { inclusive = true }
                }
            }
            is AuthState.Initial -> {
                // Ir a login cuando el estado cambie a Initial
                navController.navigate("login") {
                    popUpTo("signIn") { inclusive = true }
                }
            }
            is AuthState.NeedsRegistration -> {
                // Ya está en la pantalla correcta (SignIn/Register)
                // No hacer nada
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4287F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Spacer superior
            Box(modifier = Modifier.height(120.dp))

            // Logo/Icono de la app
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Registro",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Box(modifier = Modifier.height(32.dp))

            Text(
                text = "SENSAZIONAPP",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )

            Box(modifier = Modifier.height(48.dp))

            // Card contenedora para el contenido principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFF333333)
                    )

                    Box(modifier = Modifier.height(24.dp))

                    when (authState) {
                        is AuthState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFF4287F5),
                                strokeWidth = 4.dp
                            )
                            Box(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Procesando registro...",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        is AuthState.Error -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = (authState as AuthState.Error).message,
                                    color = Color(0xFFD32F2F),
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Box(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { activity?.let { authViewModel.signUp(it) } },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4287F5)
                                )
                            ) {
                                Text(
                                    "Intentar de nuevo",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                        else -> {
                            Button(
                                onClick = { activity?.let { authViewModel.signUp(it) } },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4287F5)
                                )
                            ) {
                                Text(
                                    "Registrarse",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.height(24.dp))

                    TextButton(
                        onClick = { authViewModel.goToLogin() } // ✅ Usar ViewModel
                    ) {
                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión",
                            color = Color(0xFF4287F5),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Spacer inferior
            Box(modifier = Modifier.height(120.dp))
        }
    }
}