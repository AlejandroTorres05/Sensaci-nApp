package com.example.sensazionapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensazionapp.datasource.local.LocalDataSourceProvider
import com.example.sensazionapp.datasource.local.LocalDataStore
import com.example.sensazionapp.ui.theme.SensazionAppTheme
import com.example.sensazionapp.viewmodel.AUTHSTATE
import com.example.sensazionapp.viewmodel.AuthViewModel
import com.example.sensazionapp.viewmodel.IDLE_AUTH_STATE
import com.example.sensazionapp.viewmodel.NOAUTHSTATE

import java.util.prefs.Preferences

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AppVariables")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalDataSourceProvider.init(applicationContext.dataStore)
        enableEdgeToEdge()
        setContent {
            SensazionAppTheme {
                val navController = rememberNavController()
                NavHost(navController= navController, startDestination = "profile"){
                    composable("login"){ LoginScreen()}
                    composable("profile"){ ProfileScreen(navController)}
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: ViewModel= viewModel()){
    var email by remember { mutableStateOf("drincon@icesi.edu.co") }
    var password by remember { mutableStateOf("contrasena12345") }

    Column {
        Box(modifier = Modifier.height(200.dp))
        TextFiel d(value = email, onValueChange = {email=it})
        TextField(value = password, onValueChange = {password=it})
        Button(onClick = { viewModel.login(email,password)}){
            Text(text = "Iniciar Sesion")
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel = viewModel()){

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAuthStatus()
    }

    when(authState.state){
        NOAUTHSTATE -> {
            navController.navigate("login"){
                popUpTo(0){inclusive=true}
                launchSingleTop = true
            }
        }
        AUTHSTATE -> {
            Column {
                Box(modifier = Modifier.height(200.dp))
                Button(onClick = {
                    viewModel.getAllUsers()
                }){
                    Text(text = "Listar Usuarios")
                }
            }
        }
        IDLE_AUTH_STATE -> {
            CircularProgressIndicator()
        }
    }
}