package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Holiday(
    @SerializedName("name")
    val name: String,
    @SerializedName("date")
    val date: HolidayDate
)

data class HolidayDate(
    @SerializedName("iso")
    val iso: String
)