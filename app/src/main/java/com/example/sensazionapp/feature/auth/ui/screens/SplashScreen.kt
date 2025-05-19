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
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Efecto que maneja la navegación después de mostrar la splash screen
    LaunchedEffect(authState) {
        android.util.Log.d("SplashScreen", "Current authState: $authState")
        android.util.Log.d("SplashScreen", "Current user: $currentUser")
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Mostrar la splash screen por 2 segundos
            delay(2000)

            android.util.Log.d("SplashScreen", "After delay, authState: $authState")

            // Verificar el estado de autenticación
            when (authState) {
                is AuthViewModel.AuthState.Authenticated -> {
                    // Si está autenticado, navegar a Home
                    val email = currentUser?.email ?: ""
                    android.util.Log.d("SplashScreen", "Navigating to home with email: $email")
                    navController.navigate("home/$email") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
                else -> {
                    // Si no está autenticado, navegar a Login
                    android.util.Log.d("SplashScreen", "Navigating to login")
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
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