package com.example.sensazionapp.feature.auth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import android.app.Activity
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.sensazionapp.util.getActivity
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import com.example.sensazionapp.R

@Composable
fun LoginScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val context = LocalContext.current
    val activity: Activity? = remember(context) {
        context.getActivity()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Usuario completamente autenticado
                navController.navigate("profile") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.ProfileIncomplete -> {
                // Usuario logueado pero perfil incompleto
                navController.navigate("complete_profile") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.NeedsRegistration -> {
                // Usuario necesita completar registro, ir a SignIn
                navController.navigate("signIn") {
                    popUpTo("login") { inclusive = true }
                }
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

            // Logo de la app
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo SensaziónApp",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            Box(modifier = Modifier.height(32.dp))

            // Título de la app
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
                        text = "Bienvenido",
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
                                text = "Verificando credenciales...",
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
                                onClick = { activity?.let { authViewModel.login(it) } },
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
                                onClick = { activity?.let { authViewModel.login(it) } },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4287F5)
                                )
                            ) {
                                Text(
                                    "Iniciar Sesión",
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
                        onClick = { authViewModel.goToSignUp() } // ✅ CAMBIADO: Usar ViewModel en lugar de navegación directa
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate",
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