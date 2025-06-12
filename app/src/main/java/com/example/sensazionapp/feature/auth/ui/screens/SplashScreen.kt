package com.example.sensazionapp.feature.auth.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sensazionapp.R
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel.AuthState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Variables para controlar navegación y timing
    var hasNavigated by remember { mutableStateOf(false) }
    var minDelayCompleted by remember { mutableStateOf(false) }

    // ✅ DELAY MÍNIMO DE 2 SEGUNDOS
    LaunchedEffect(Unit) {
        delay(2000)
        minDelayCompleted = true
    }

    // ✅ NAVEGACIÓN REACTIVA - Solo cuando delay completado Y estado final
    LaunchedEffect(authState, minDelayCompleted) {
        if (minDelayCompleted && !hasNavigated) {
            when (authState) {
                is AuthState.Authenticated -> {
                    hasNavigated = true
                    navController.navigate("profile") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.ProfileIncomplete -> {
                    hasNavigated = true
                    navController.navigate("complete_profile") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.NeedsRegistration -> {
                    hasNavigated = true
                    navController.navigate("signIn") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Initial -> {
                    hasNavigated = true
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Error -> {
                    hasNavigated = true
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Loading -> {
                    // NO NAVEGAR - Esperar a que termine la validación
                }
            }
        } else if (!minDelayCompleted) {
            println("SPLASH: Minimum delay not completed yet, waiting...")
        } else if (hasNavigated) {
            println("SPLASH: Already navigated, skipping...")
        }
    }

    // ✅ LOG ADICIONAL - Cambios en authState con timestamps
    LaunchedEffect(authState) {
        val timestamp = System.currentTimeMillis()
        // ✅ SI EL ESTADO CAMBIA A FINAL DESPUÉS DEL DELAY, NAVEGAR INMEDIATAMENTE
        if (minDelayCompleted && !hasNavigated) {
            when (authState) {
                is AuthState.Authenticated -> {
                    hasNavigated = true
                    navController.navigate("profile") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.ProfileIncomplete -> {
                    hasNavigated = true
                    navController.navigate("complete_profile") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Initial -> {
                    hasNavigated = true
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Error -> {
                    hasNavigated = true
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                is AuthState.Loading -> {
                    println("SPLASH: [IMMEDIATE] Still validating token...")
                }
                is AuthState.NeedsRegistration -> {
                    println("SPLASH: [IMMEDIATE] User needs registration")
                    hasNavigated = true
                    navController.navigate("signIn") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }
    }

    // ✅ LOG - Cambios en currentUser
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            println("SPLASH: User loaded: profileCompleted=${currentUser?.profileCompleted}")
        }
    }

    // ✅ TIMEOUT DE SEGURIDAD - Si después de 10 segundos sigue en Loading
    LaunchedEffect(Unit) {
        delay(10000) // 10 segundos
        if (!hasNavigated && minDelayCompleted) {
            println("SPLASH: TIMEOUT - Token validation took too long, navigating to login")
            hasNavigated = true
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // UI de la SplashScreen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4287F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo grande
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo SensaziónApp",
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp)
            )

            // Texto SENSAZIONAPP
            Text(
                text = "SENSAZIONAPP",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    fontSize = 32.sp
                ),
                color = Color.White
            )
        }
    }
}