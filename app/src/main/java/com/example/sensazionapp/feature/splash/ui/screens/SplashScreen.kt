package com.example.sensazionapp.feature.splash.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sensazionapp.R

@Composable
fun SplashScreen (){
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(modifier = Modifier.height(200.dp))
        Image(  painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = "logo")
        Text("SensaziónApp")
        Box(modifier = Modifier.height(200.dp))
    }
}