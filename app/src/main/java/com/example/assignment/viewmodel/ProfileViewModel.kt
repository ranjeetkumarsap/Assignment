package com.example.assignment.viewmodel

import AppRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.assignment.model.CurrentWeatherResponse
import com.example.assignment.utils.BASE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(
    app: Application,
    private val appRepository: AppRepository
) : AndroidViewModel(app) {


    internal val currentWeatherLiveData = MutableLiveData<CurrentWeatherResponse>()




    fun getCurrentWeather(cityName: String, units: String, defaultLang: String, apiKey: String) = viewModelScope.launch{
        // val data = HashMap<String, String>()
        //  data.put("token", token)
        val url: String = BASE_URL +"weather?q="+cityName+"&units="+units+"&lang="+defaultLang+"&appid="+apiKey

        CoroutineScope(Dispatchers.IO).launch {
            val response: Response<CurrentWeatherResponse?> = appRepository.getCurrentWeather(url)
            if(response.isSuccessful)
                currentWeatherLiveData.postValue(response.body())
        }

    }


}