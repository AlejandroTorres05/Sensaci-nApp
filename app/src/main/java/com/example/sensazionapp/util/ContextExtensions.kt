package com.example.sensazionapp.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

// Función de extensión para obtener Activity del Context
fun Context.getActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}