package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    object Empty : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Empty)
    val weatherState: StateFlow<WeatherUiState> = _weatherState

    fun fetchWeather(city: String) {
        _weatherState.value = WeatherUiState.Loading
        viewModelScope.launch {
            _weatherState.value = try {
                val response = RetrofitInstance.api.getWeather(city) // <-- Now it's correct!
                WeatherUiState.Success(response)
            } catch (e: Exception) {
                WeatherUiState.Error("City not found or network error. \n $e")
            }
        }
    }
}