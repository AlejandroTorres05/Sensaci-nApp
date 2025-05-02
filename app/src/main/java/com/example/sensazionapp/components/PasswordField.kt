package com.example.sensazionapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    onValue: (String) -> Unit,
    value: String
) {
    var showPassword by remember { mutableStateOf(false) }

    Row(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValue,
            modifier = Modifier.weight(1f),
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            placeholder = { Text("Contraseña") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            modifier = Modifier
                .width(56.dp)
                .height(56.dp),
            onClick = { showPassword = !showPassword }
        ) {
            Icon(
                imageVector = if (showPassword) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff,
                contentDescription = if (showPassword) "Ocultar contraseña"
                else "Mostrar contraseña",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}