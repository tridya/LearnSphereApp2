package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class HolidayResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("response")
    val response: HolidayData
)

data class Meta(
    @SerializedName("code")
    val code: Int
)

data class HolidayData(
    @SerializedName("holidays")
    val holidays: List<Holiday>
)