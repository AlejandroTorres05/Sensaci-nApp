package com.example.sensazionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sensazionapp.feature.auth.ui.screens.CompleteProfileScreen
import com.example.sensazionapp.feature.auth.ui.screens.LoginScreen
import com.example.sensazionapp.feature.auth.ui.screens.SignInScreen
import com.example.sensazionapp.feature.auth.ui.viewModels.AuthViewModel
import com.example.sensazionapp.feature.auth.ui.screens.SplashScreen
import com.example.sensazionapp.feature.profile.ui.screens.ProfileScreen
import com.example.sensazionapp.ui.theme.SensazionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController= navController, startDestination = "splash"){
        composable("splash"){ SplashScreen(navController, authViewModel) }
        composable("login"){ LoginScreen(navController, authViewModel)}
        composable("signIn") { SignInScreen(navController, authViewModel) }
        composable("complete_profile") { CompleteProfileScreen(navController, authViewModel) }
        composable("profile") { ProfileScreen(navController, authViewModel) }
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

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SensazionAppTheme {
        SplashScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CompletePrfileScreenPreview() {
    SensazionAppTheme {
        CompleteProfileScreen()
    }
}

/*
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SensazionAppTheme {
        HomeScreen()
    }
}

 */

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SensazionAppTheme {
        ProfileScreen()
    }
}
