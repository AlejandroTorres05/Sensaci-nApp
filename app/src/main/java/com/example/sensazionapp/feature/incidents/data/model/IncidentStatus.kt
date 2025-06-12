package com.example.sensazionapp.feature.incidents.data.model

import com.google.gson.annotations.SerializedName

enum class IncidentStatus {
    @SerializedName("ACTIVE")
    ACTIVE,

    @SerializedName("RESOLVED")
    RESOLVED,

    @SerializedName("EXPIRED")
    EXPIRED,

    @SerializedName("VERIFIED")
    VERIFIED
}