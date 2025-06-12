package com.example.sensazionapp.feature.incidents.data.model

import com.google.gson.annotations.SerializedName

enum class IncidentCategory {
    @SerializedName("ACCIDENT")
    ACCIDENT,

    @SerializedName("CRIME")
    CRIME,

    @SerializedName("EMERGENCY")
    EMERGENCY,

    @SerializedName("NATURAL_DISASTER")
    NATURAL_DISASTER,

    @SerializedName("INFRASTRUCTURE")
    INFRASTRUCTURE,

    @SerializedName("OTHER")
    OTHER
}