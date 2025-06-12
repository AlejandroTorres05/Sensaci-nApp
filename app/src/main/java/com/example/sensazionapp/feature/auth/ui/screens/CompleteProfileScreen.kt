package com.example.sensazionapp.feature.auth.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel.AuthState
import com.example.sensazionapp.util.getActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    val context = LocalContext.current
    val activity: Activity? = remember(context) {
        context.getActivity()
    }

    // Estados para los campos del formulario
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Validaciones
    val isFirstNameValid = firstName.isNotBlank() && firstName.length >= 2
    val isLastNameValid = lastName.isNotBlank() && lastName.length >= 2
    val isPhoneValid = phone.isNotBlank() && phone.length >= 8
    val isFormValid = isFirstNameValid && isLastNameValid && isPhoneValid

    // MANEJO DE ESTADOS
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("main") {
                    popUpTo("complete_profile") { inclusive = true }
                }
            }
            is AuthState.Initial -> {
                navController.navigate("login") {
                    popUpTo("complete_profile") { inclusive = true }
                }
            }
            is AuthState.ProfileIncomplete -> {
                println("User in ProfileIncomplete state - staying on complete profile screen")
            }
            is AuthState.Error -> {
                println("Error in CompleteProfileScreen: ${(authState as AuthState.Error).message}")
            }
            is AuthState.Loading -> {
                println("Loading state in CompleteProfileScreen")
            }
            is AuthState.NeedsRegistration -> {
                println("User needs registration, navigating to signIn")
                navController.navigate("signIn") {
                    popUpTo("complete_profile") { inclusive = true }
                }
            }
        }
    }

    // PRECARGAR DATOS DEL USUARIO
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (firstName.isEmpty() && !user.firstName.isNullOrBlank()) {
                firstName = user.firstName!!
            }
            if (lastName.isEmpty() && !user.lastName.isNullOrBlank()) {
                lastName = user.lastName!!
            }
            if (phone.isEmpty() && !user.phone.isNullOrBlank()) {
                phone = user.phone!!
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacer superior
            Box(modifier = Modifier.height(60.dp))

            // Header
            Text(
                text = "Completa tu Perfil",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF333333)
            )

            Box(modifier = Modifier.height(8.dp))

            Text(
                text = "Solo faltan unos datos para empezar",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Box(modifier = Modifier.height(40.dp))

            // Card principal del formulario
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
                    // Avatar placeholder (futuro para subir foto)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50.dp))
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

                    // Botón para subir foto (deshabilitado por ahora)
                    OutlinedButton(
                        onClick = {
                            // TODO: Implementar subida de foto después
                        },
                        enabled = false,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            disabledContentColor = Color.Gray
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Subir foto",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Subir foto después",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Box(modifier = Modifier.height(32.dp))

                    // Campo Nombre
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Nombre") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nombre"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = firstName.isNotEmpty() && !isFirstNameValid,
                        supportingText = if (firstName.isNotEmpty() && !isFirstNameValid) {
                            { Text("El nombre debe tener al menos 2 caracteres") }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4287F5),
                            focusedLabelColor = Color(0xFF4287F5)
                        )
                    )

                    Box(modifier = Modifier.height(16.dp))

                    // Campo Apellido
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Apellido") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Apellido"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = lastName.isNotEmpty() && !isLastNameValid,
                        supportingText = if (lastName.isNotEmpty() && !isLastNameValid) {
                            { Text("El apellido debe tener al menos 2 caracteres") }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4287F5),
                            focusedLabelColor = Color(0xFF4287F5)
                        )
                    )

                    Box(modifier = Modifier.height(16.dp))

                    // Campo Teléfono
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Teléfono"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = phone.isNotEmpty() && !isPhoneValid,
                        supportingText = if (phone.isNotEmpty() && !isPhoneValid) {
                            { Text("El teléfono debe tener al menos 8 dígitos") }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4287F5),
                            focusedLabelColor = Color(0xFF4287F5)
                        )
                    )

                    Box(modifier = Modifier.height(32.dp))

                    // Mostrar email del usuario (solo lectura)
                    currentUser?.let { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Email: ${user.email}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Box(modifier = Modifier.height(32.dp))

                    // Botón de completar perfil
                    Button(
                        onClick = {
                            authViewModel.completeProfile(
                                firstName = firstName,
                                lastName = lastName,
                                phone = phone,
                                notificationRadius = 1000.0 // 1 km por defecto
                            )
                        },
                        enabled = isFormValid && authState !is AuthState.Loading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4287F5),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        when (authState) {
                            is AuthState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Guardando...",
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            else -> {
                                Text(
                                    text = "Completar Perfil",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    // Mostrar error si existe
                    if (authState is AuthState.Error) {
                        Box(modifier = Modifier.height(16.dp))
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
                    }
                }
            }

            Box(modifier = Modifier.height(40.dp))
        }
    }
}