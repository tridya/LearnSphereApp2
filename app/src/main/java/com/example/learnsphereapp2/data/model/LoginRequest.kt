// app/src/main/java/com/example/learnsphereapp2/data/model/LoginRequest.kt
package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)