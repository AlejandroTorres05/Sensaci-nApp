package com.example.sensazionapp.util

import android.content.Context
import android.util.Log
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.auth0.android.authentication.AuthenticationAPIClient
import android.app.Activity
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Auth0Manager(private val context: Context) {

    private val auth0 = Auth0(
        CLIENT_ID,
        DOMAIN
    )

    // CredentialsManager para persistencia
    private val credentialsManager = SecureCredentialsManager(
        context,
        AuthenticationAPIClient(auth0),
        SharedPreferencesStorage(context)
    )

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _credentials = MutableStateFlow<Credentials?>(null)
    val credentials: StateFlow<Credentials?> = _credentials.asStateFlow()

    companion object {
        private const val TAG = "Auth0Manager"
        private const val CLIENT_ID = "iRhsviVofweC1mMPPzrNrnlq7wX9tE0v"
        private const val DOMAIN = "dev-vguxq7gicpeheej8.us.auth0.com"
        private const val AUDIENCE = "https://api.sensazionapp.com/"
        private const val SCHEME = "demo"
    }

    // Inicializar y verificar credenciales guardadas
    init {
        checkSavedCredentials()
    }

    private fun checkSavedCredentials() {
        if (credentialsManager.hasValidCredentials()) {
            credentialsManager.getCredentials(object : Callback<Credentials, CredentialsManagerException> {
                override fun onSuccess(result: Credentials) {
                    _credentials.value = result
                    // También cargar el perfil de usuario
                    getUserProfile(result.accessToken) { profile ->
                        _userProfile.value = profile
                    }
                }

                override fun onFailure(error: CredentialsManagerException) {
                    _credentials.value = null
                    _userProfile.value = null
                }
            })
        } else {
            Log.d(TAG, "No valid saved credentials found")
        }
    }

    /**
     * Verifica si hay credenciales válidas y no expiradas
     */
    fun isAuthenticated(): Boolean {
        // Verificar tanto memoria como credenciales guardadas
        return _credentials.value != null || credentialsManager.hasValidCredentials()
    }

    /**
     * Obtiene el token de acceso actual de forma síncrona
     */
    fun getAccessToken(): String? {
        return _credentials.value?.accessToken
    }

    fun login(
        activity: Activity,
        onSuccess: (Credentials) -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        WebAuthProvider.login(auth0)
            .withScheme(SCHEME)
            .withScope("openid profile email")
            .withAudience(AUDIENCE)
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    // Guardar credenciales
                    credentialsManager.saveCredentials(result)
                    _credentials.value = result

                    // Cargar perfil de usuario
                    getUserProfile(result.accessToken) { profile ->
                        _userProfile.value = profile
                        onSuccess(result)
                    }
                }

                override fun onFailure(error: AuthenticationException) {
                    onFailure(error)
                }
            })
    }

    fun signUp(
        activity: Activity,
        onSuccess: (Credentials) -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        WebAuthProvider.login(auth0)
            .withScheme(SCHEME)
            .withScope("openid profile email")
            .withAudience(AUDIENCE)
            .withParameters(mapOf("screen_hint" to "signup"))
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    // Guardar credenciales
                    credentialsManager.saveCredentials(result)
                    _credentials.value = result

                    // Cargar perfil de usuario
                    getUserProfile(result.accessToken) { profile ->
                        _userProfile.value = profile
                        onSuccess(result)
                    }
                }

                override fun onFailure(error: AuthenticationException) {
                    onFailure(error)
                }
            })
    }

    /**
     * Logout completo
     */
    fun logout(
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        // Primero limpiar credenciales locales
        forceLogout()

        // Luego hacer logout en Auth0
        WebAuthProvider.logout(auth0)
            .withScheme(SCHEME)
            .start(activity, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    onSuccess()
                }

                override fun onFailure(error: AuthenticationException) {
                    // Aún así consideramos exitoso porque las credenciales ya se limpiaron
                    onSuccess()
                }
            })
    }

    /**
     * Fuerza la limpieza completa de credenciales
     */
    fun forceLogout() {
        credentialsManager.clearCredentials()
        _credentials.value = null
        _userProfile.value = null
    }

    private fun getUserProfile(accessToken: String, callback: (UserProfile?) -> Unit) {
        val authApiClient = AuthenticationAPIClient(auth0)
        authApiClient.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onSuccess(result: UserProfile) {
                    callback(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    callback(null)
                }
            })
    }

    fun getIdToken(): String? {
        return _credentials.value?.idToken
    }
}