// app/src/main/java/com/example/learnsphereapp2/data/model/ErrorResponse.kt
package com.example.learnsphereapp2.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("detail")
    val detail: Any?
) {
    fun getErrorMessage(): String {
        return when (detail) {
            is String -> detail
            is List<*> -> detail.joinToString(", ") { it.toString() }
            else -> "Unknown error"
        }
    }
}