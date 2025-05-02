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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(){
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(modifier = Modifier.height(64.dp))
        Text("SensaziónApp")
        //////////////////////////////////////
        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = name, onValueChange = {
                name = it
            },
            placeholder = { Text("Nombre(s)")},
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Blue,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Blue,
            )
        )

        //////////////////////////////////////
        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = lastName, onValueChange = {
                lastName = it
            },
            placeholder = { Text("Apellido(s)")},
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Blue,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Blue,
            )
        )

        //////////////////////////////////////
        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = email, onValueChange = {
                email = it
            },
            placeholder = { Text("Correo electrónico")},
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

        /////////////////////////////////////
        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = phone, onValueChange = {
                phone = it
            },
            placeholder = { Text("Número de celular")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Blue,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Blue,
            )
        )

        //////////////////////////////////////
        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = password,
            onValueChange = {
                password = it
                passwordsMatch = it == confirmPassword // Actualiza el estado de coincidencia
            },
            placeholder = { Text("Contraseña") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Blue,
                unfocusedLabelColor = Color.Gray,
                focusedIndicatorColor = Color.Blue,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Box(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordsMatch = password == it // Actualiza el estado de coincidencia
            },
            placeholder = { Text("Introduce de nuevo la contraseña") },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = if (passwordsMatch) Color.Blue else Color.Red,
                unfocusedLabelColor = if (passwordsMatch) Color.Gray else Color.Red,
                focusedIndicatorColor = if (passwordsMatch) Color.Blue else Color.Red,
                unfocusedIndicatorColor = if (passwordsMatch) Color.Gray else Color.Red
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Mostrar mensaje de error si no coinciden
        if (!passwordsMatch && confirmPassword.isNotEmpty()) {
            Text(
                text = "Las contraseñas no coinciden",
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        //////////////////////////////////////
        Box(modifier = Modifier.height(16.dp))

        Button(onClick = { }){
            Text(text = "Iniciar Sesión")
        }
        Box(modifier = Modifier.height(200.dp))
    }
}
