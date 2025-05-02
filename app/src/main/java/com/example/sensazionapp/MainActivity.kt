package com.example.sensazionapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensazionapp.screens.LoginScreen
import com.example.sensazionapp.screens.SignInScreen
import com.example.sensazionapp.ui.theme.SensazionAppTheme

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore("AppVariables")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //LocalDataSourceProvider.init(applicationContext.dataStore)
        enableEdgeToEdge()
        setContent {
            SensazionAppTheme {
                App()
            }
        }
    }
}

@Composable
fun App(){
    val navController = rememberNavController()
    NavHost(navController= navController, startDestination = "login"){
        composable("login"){ LoginScreen()}
        composable("signIn") { SignInScreen() }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensazionAppTheme {
        App()
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    SensazionAppTheme {
        SignInScreen()
    }
}


/*
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

 */
///////////////////////////////////////////////////////////
/*
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
*/