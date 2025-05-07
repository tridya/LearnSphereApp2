package com.example.learnsphereapp2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnsphereapp2.data.model.Holiday
import com.example.learnsphereapp2.network.RetrofitClient
import kotlinx.coroutines.launch

class HolidayViewModel : ViewModel() {
    private val _holidays = MutableLiveData<List<Holiday>>()
    val holidays: LiveData<List<Holiday>> get() = _holidays

    fun fetchHolidays(apiKey: String, country: String, year: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.calendarificApi.getHolidays(apiKey, country, year)
                if (response.meta.code == 200) {
                    _holidays.postValue(response.response.holidays)
                } else {
                    _holidays.postValue(emptyList())
                }
            } catch (e: Exception) {
                _holidays.postValue(emptyList())
                e.printStackTrace()
            }
        }
    }
}