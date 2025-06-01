package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class Holiday(
    @SerializedName("date") val date: String,  // Format: "YYYY-MM-DD"
    @SerializedName("event") val event: String,
    @SerializedName("is_national_holiday") val isNationalHoliday: Boolean
)