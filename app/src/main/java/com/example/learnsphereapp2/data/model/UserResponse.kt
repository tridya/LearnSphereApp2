// app/src/main/java/com/example/learnsphereapp2/data/model/UserResponse.kt
package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("role")
    val role: String
)