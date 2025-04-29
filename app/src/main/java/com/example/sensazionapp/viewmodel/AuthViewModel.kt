package com.example.sensazionapp.viewmodel

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensazionapp.datasource.LoginData
import com.example.sensazionapp.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    val authRepository: AuthRepository=AuthRepository()
):ViewModel() {

    var authState:MutableStateFlow<AuthState> = MutableStateFlow<AuthState>(AuthState())

    fun login(email: String,password:String){
        viewModelScope.launch(Dispatchers.IO){
            authRepository.login(LoginData(
                email,password
            ))
        }
    }

    fun getAuthStatus(){
        viewModelScope.launch(Dispatchers.IO){
            var accessToken = authRepository.getAccessToken()
            accessToken?.let {
                if(it.isEmpty()){
                    authState.value=AuthState(state = NOAUTHSTATE)
                }else{
                    authState.value=AuthState(state = AUTHSTATE)
                }
            }
        }
    }

    fun getAllUsers(){
        viewModelScope.launch(Dispatchers.IO){
            authRepository.getAllUsers()
        }
    }
}

data class AuthState(
    var state:String= IDLE_AUTH_STATE
)

var AUTHSTATE = "AUTH"
var NOAUTHSTATE = "NOAUTH"
var IDLE_AUTH_STATE="IDLE_AUTH"