package com.example.sensazionapp.feature.auth.ui.viewModels

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.authentication.AuthenticationException
import com.example.sensazionapp.domain.model.User
import com.example.sensazionapp.util.SessionManager
import com.example.sensazionapp.util.Auth0Manager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.auth0.android.result.Credentials

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth0Manager = Auth0Manager(application)
    private val sessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        if (sessionManager.isLoggedIn() && auth0Manager.isAuthenticated()) {
            _authState.value = AuthState.Authenticated
            _currentUser.value = auth0Manager.getUser()
        }
    }

    fun login(activity: Activity) {
        _authState.value = AuthState.Loading

        auth0Manager.login(
            activity = activity,
            onSuccess = { credentials ->
                handleAuthSuccess(credentials)
            },
            onFailure = { error ->
                handleAuthError(error)
            }
        )
    }

    fun signUp(activity: Activity) {
        _authState.value = AuthState.Loading

        auth0Manager.signUp(
            activity = activity,
            onSuccess = { credentials ->
                handleAuthSuccess(credentials)
            },
            onFailure = { error ->
                handleAuthError(error)
            }
        )
    }

    fun logout(activity: Activity) {
        _authState.value = AuthState.Loading

        auth0Manager.logout(
            activity = activity,
            onSuccess = {
                sessionManager.clearSession()
                _currentUser.value = null
                _authState.value = AuthState.Initial
            },
            onFailure = { error ->
                handleAuthError(error)
            }
        )
    }

    private fun handleAuthSuccess(credentials: Credentials) {
        viewModelScope.launch {
            val user = auth0Manager.getUser()
            if (user != null) {
                sessionManager.saveAuthToken(credentials.accessToken)
                sessionManager.saveUser(user)
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Error("Error obteniendo información del usuario")
            }
        }
    }

    private fun handleAuthError(error: AuthenticationException) {
        _authState.value = AuthState.Error(error.message ?: "Error desconocido")
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object Authenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }
    

}