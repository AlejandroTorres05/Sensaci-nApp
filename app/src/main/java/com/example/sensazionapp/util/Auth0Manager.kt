package com.example.sensazionapp.util

import android.content.Context
import android.util.Log
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.example.sensazionapp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.auth0.android.authentication.AuthenticationAPIClient
import android.app.Activity

class Auth0Manager(private val context: Context) {

    private val auth0 = Auth0(
        CLIENT_ID,
        DOMAIN
    )

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _credentials = MutableStateFlow<Credentials?>(null)
    val credentials: StateFlow<Credentials?> = _credentials.asStateFlow()

    fun login(
        activity: Activity,
        onSuccess: (Credentials) -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        Log.d("Auth0Manager", "Iniciando login con scheme: $SCHEME")
        Log.d("Auth0Manager", "Domain: $DOMAIN")
        WebAuthProvider.login(auth0)
            .withScheme(SCHEME)
            .withScope("openid profile email")
            .withAudience("https://$DOMAIN/api/v2/")
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    _credentials.value = result
                    getUserProfile(result.accessToken) { profile ->
                        _userProfile.value = profile
                        onSuccess(result)
                    }
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("Auth0Manager", "Error en login: ${error.message}")
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
            .withAudience("https://$DOMAIN/api/v2/")
            .withParameters(mapOf("screen_hint" to "signup"))
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                override fun onSuccess(result: Credentials) {
                    _credentials.value = result
                    getUserProfile(result.accessToken) { profile ->
                        _userProfile.value = profile
                        onSuccess(result)
                    }
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("Auth0Manager", "Error en signup: ${error.message}")
                    onFailure(error)
                }
            })
    }

    fun logout(
        activity: Activity,
        onSuccess: () -> Unit,
        onFailure: (AuthenticationException) -> Unit
    ) {
        WebAuthProvider.logout(auth0)
            .withScheme(SCHEME)
            .start(activity, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    _credentials.value = null
                    _userProfile.value = null
                    onSuccess()
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("Auth0Manager", "Error en logout: ${error.message}")
                    onFailure(error)
                }
            })
    }

    private fun getUserProfile(accessToken: String, callback: (UserProfile) -> Unit) {
        val authApiClient = AuthenticationAPIClient(auth0)
        authApiClient.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onSuccess(result: UserProfile) {
                    callback(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("Auth0Manager", "Error obteniendo perfil: ${error.message}")
                }
            })
    }

    fun getUser(): User? {
        val profile = _userProfile.value ?: return null
        val creds = _credentials.value ?: return null

        return User(
            id = profile.getId() ?: "",
            name = profile.name ?: "",
            lastName = profile.familyName ?: "",
            email = profile.email ?: "",
            phone = "",
            token = creds.accessToken
        )
    }

    fun getIdToken(): String? {
        return _credentials.value?.idToken
    }

    fun getAccessToken(): String? {
        return _credentials.value?.accessToken
    }

    fun isAuthenticated(): Boolean {
        return _credentials.value != null
    }

    companion object {
        private const val CLIENT_ID = "iRhsviVofweC1mMPPzrNrnlq7wX9tE0v"
        private const val DOMAIN = "dev-vguxq7gicpeheej8.us.auth0.com"
        private const val SCHEME = "demo"
    }
}