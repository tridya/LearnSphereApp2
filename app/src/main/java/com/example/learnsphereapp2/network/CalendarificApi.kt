//package com.example.learnsphereapp2.network
//
//import com.example.learnsphereapp2.data.model.HolidayResponse
//import retrofit2.http.GET
//import retrofit2.http.Query
//
//interface CalendarificApi {
//    @GET("holidays")
//    suspend fun getHolidays(
//        @Query("api_key") apiKey: String,
//        @Query("country") country: String,
//        @Query("year") year: Int
//    ): HolidayResponse
//}