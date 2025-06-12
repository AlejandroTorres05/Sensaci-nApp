package com.example.sensazionapp.feature.auth.ui.viewModels

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.authentication.AuthenticationException
import com.example.sensazionapp.data.remote.dto.UserDTO
import com.example.sensazionapp.domain.model.User
import com.example.sensazionapp.util.Auth0Manager
import com.example.sensazionapp.util.NetworkUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth0Manager = Auth0Manager(application)

    private val apiService by lazy {
        NetworkUtil.createApiService {
            auth0Manager.getAccessToken()
        }
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    companion object {
        private const val TAG = "AuthViewModel"
    }

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            if (auth0Manager.isAuthenticated()) {
                // Validar token con una llamada real al API
                validateTokenAndCheckProfile()
            } else {
                _authState.value = AuthState.Initial
            }
        }
    }

    private fun validateTokenAndCheckProfile() {
        viewModelScope.launch {
            try {
                val response = apiService.getUserProfile()

                when (response.code()) {
                    200 -> {
                        val userDTO = response.body()
                        _currentUser.value = userDTO?.toUser()

                        if (userDTO?.profileCompleted == true) {
                            _authState.value = AuthState.Authenticated
                        } else {
                            _authState.value = AuthState.ProfileIncomplete
                        }
                    }

                    403, 401 -> {
                        auth0Manager.forceLogout()
                        _currentUser.value = null
                        _authState.value = AuthState.Initial
                    }

                    404 -> {
                        _authState.value = AuthState.NeedsRegistration
                    }

                    else -> {
                        _authState.value = AuthState.Error("Error del servidor: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                // En caso de error de red, limpiar credenciales por seguridad
                auth0Manager.forceLogout()
                _currentUser.value = null
                _authState.value = AuthState.Initial
            }
        }
    }

    fun login(activity: Activity) {
        _authState.value = AuthState.Loading

        auth0Manager.login(
            activity = activity,
            onSuccess = { credentials ->
                checkExistingUserAfterAuth0()
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
                registerUserInApi()
            },
            onFailure = { error ->
                handleAuthError(error)
            }
        )
    }

    private fun checkExistingUserAfterAuth0() {
        viewModelScope.launch {
            try {
                val response = apiService.getUserProfile()

                when (response.code()) {
                    200 -> {
                        val userDTO = response.body()
                        _currentUser.value = userDTO?.toUser()

                        if (userDTO?.profileCompleted == true) {
                            _authState.value = AuthState.Authenticated
                        } else {
                            _authState.value = AuthState.ProfileIncomplete
                        }
                    }

                    404 -> {
                        registerUserInApi()
                    }

                    else -> {
                        _authState.value = AuthState.Error("Error verificando usuario: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                registerUserInApi()
            }
        }
    }

    private fun registerUserInApi() {
        viewModelScope.launch {
            try {
                val response = apiService.registerUser(emptyMap())

                if (response.isSuccessful) {
                    val userDTO = response.body()

                    _currentUser.value = userDTO?.toUser()
                    _authState.value = AuthState.ProfileIncomplete
                } else {
                    _authState.value = AuthState.Error("Error registrando usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun completeProfile(
        firstName: String,
        lastName: String,
        phone: String,
        notificationRadius: Double,
        avatarUrl: String? = null
    ) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val userDTO = UserDTO(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    notificationRadius = notificationRadius,
                    avatarUrl = avatarUrl,
                    profileCompleted = true // Asegurar que se marca como completado
                )

                val response = apiService.completeProfile(userDTO)

                if (response.isSuccessful) {
                    val updatedUser = response.body()

                    _currentUser.value = updatedUser?.toUser()
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Error completando perfil: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun getAccessToken(): String? {
        return auth0Manager.getAccessToken()
    }

    fun logout(activity: Activity) {
        _authState.value = AuthState.Loading

        auth0Manager.logout(
            activity = activity,
            onSuccess = {
                _currentUser.value = null
                _authState.value = AuthState.Initial
            },
            onFailure = { error ->
                // Aunque falle el logout en Auth0, limpiar estado local
                _currentUser.value = null
                _authState.value = AuthState.Initial
            }
        )
    }

    /**
     * Método para manejar navegación entre Login y SignUp
     */
    fun goToLogin() {
        _authState.value = AuthState.Initial
    }

    fun goToSignUp() {
        _authState.value = AuthState.NeedsRegistration
    }

    private fun handleAuthError(error: AuthenticationException) {
        _authState.value = AuthState.Error(error.message ?: "Error de autenticación")
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        object NeedsRegistration : AuthState()
        object ProfileIncomplete : AuthState()
        object Authenticated : AuthState()
        data class Error(val message: String) : AuthState()
    }

}

// Extension functions
private fun UserDTO.toUser(): User {
    return User(
        id = this.id,
        email = this.email ?: "",
        firstName = this.firstName,
        lastName = this.lastName,
        phone = this.phone,
        avatarUrl = this.avatarUrl,
        notificationsEnabled = this.notificationsEnabled ?: true,
        locationSharingEnabled = this.locationSharingEnabled ?: true,
        notificationRadius = this.notificationRadius ?: 1000.0,
        totalIncidentsReported = this.totalIncidentsReported ?: 0,
        totalConfirmations = this.totalConfirmations ?: 0,
        verificationScore = this.verificationScore ?: 0.0,
        profileCompleted = this.profileCompleted ?: false
    )
}