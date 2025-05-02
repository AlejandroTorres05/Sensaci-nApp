package com.example.sensazionapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.sensazionapp.components.PasswordField

@Composable
fun LoginScreen(){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Box(modifier = Modifier.height(200.dp))
        Text("SensaziónApp")
        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = email, onValueChange = {
                email = it
            },
            label = { Text("Correo electrónico")},
            placeholder = { Text("JohnDoe@ejemplo.com")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Blue,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Blue,
            )
        )


        PasswordField(onValue = { password = it },
            value = password,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp))

        Box(modifier = Modifier.height(16.dp))

        Button(onClick = { }){
            Text(text = "Iniciar Sesión")
        }
        Box(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { /* ... */ }
        ) {
            Text(
                text = "Regístrate",
                color = Color.Gray
            )
        }

        Box(modifier = Modifier.height(200.dp))
    }
}